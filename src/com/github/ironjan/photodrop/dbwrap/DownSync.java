package com.github.ironjan.photodrop.dbwrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.github.ironjan.photodrop.helper.ConnectionBean;
import com.github.ironjan.photodrop.helper.Prefs_;
import com.github.ironjan.photodrop.model.DirKeeper;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
class DownSync {
	@Bean
	DirKeeper mDirKeeper;
	@Bean
	SessionKeeper mSessionKeeper;
	private DropboxAPI<AndroidAuthSession> mApi;
	private File mExtDir;

	@AfterInject
	void fetchValues() {
		mApi = mSessionKeeper.getmDBApi();
		mExtDir = mDirKeeper.getExtFilesDir();
	}

	private static final DownSyncCallback mDummyCallback = new DownSyncCallback() {

		@Override
		public void syncFinished() { /* dummy */
		}
	};
	private static final String TAG = null;

	@Bean
	ConnectionBean cb;
	@Bean
	FileRevDAO fileRevDao;

	@StringRes
	String remoteDbFolder;

	@Pref
	Prefs_ prefs;

	private boolean mIsSyncing;

	private Object mCleanUpFinished;

	private Object mUpdateFinished;

	private DownSyncCallback mCallback = mDummyCallback;
	private boolean mRemoteFolderUnchanged;

	public void autoSync() {
		if (doesNotMeetAutoSyncConditions()) {
			return;
		}

		sync();
	}

	private boolean doesNotMeetAutoSyncConditions() {
		boolean syncWithoutWifiEnabled = !(prefs.syncOnlyOnWifi().get());
		boolean hasWifi = cb.hasWifiOrEthernet();

		boolean meetsConditions = hasWifi || syncWithoutWifiEnabled;

		return !meetsConditions;
	}

	@Background
	public void sync() {

		if (mIsSyncing) {
			Log.v(TAG, "already syncing...");
			return;
		}

		if (!cb.hasInternetConnection()) {
			Log.v(TAG,
					"scheduled next sync because there is no internet connection");
			scheduleNext();
		}

		Entry folderEntry = getRemoteFolderContent();

		if (folderEntry == null || mRemoteFolderUnchanged == true) {
			Log.v(TAG,
					"either folder is null or nothing changed. schedule next");
			scheduleNext();
			return;
		}

		cleanUpLocalFiles(folderEntry);
		updateLocalFiles(folderEntry);
	}

	@Background
	@Trace
	void updateLocalFiles(Entry folderEntry) {
		Log.v(TAG, "opened DB");
		fileRevDao.open();

		List<Entry> remoteFiles = folderEntry.contents;

		Vector<String> update = new Vector<String>();

		for (Entry re : remoteFiles) {
			String fileName = re.fileName();
			Log.v(TAG, "Checking " + fileName + " for updates");
			FileRev frev = fileRevDao.findOrCreateFileRevByName(fileName);
			if (!re.rev.equals(frev.rev)) {
				Log.v(TAG, fileName + " needs to be updated.");
				update.add(fileName);
			}
		}

		if (update.size() == 0) {
			Log.v(TAG, "nothing to update");
			return;
		}

		Log.v(TAG, update.size() + " files need updates!");

		for (String fileName : update) {
			FileOutputStream outputStream = null;
			try {
				File file = new File(mExtDir, fileName);
				outputStream = new FileOutputStream(file);
				Log.v(TAG, String.format("Trying to update %s", fileName));
				DropboxFileInfo info = mApi.getFile(remoteDbFolder + fileName,
						null, outputStream, null);
				FileRev fileRev = fileRevDao
						.findOrCreateFileRevByName(fileName);
				fileRev.rev = info.getMetadata().rev;
				fileRevDao.update(fileRev);
				Log.v(TAG, String.format("Successfully updated %s", fileName));
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						Log.e(TAG, e.getMessage(), e);
					}
				}
			}
		}

		syncFinish(new UpdateResult(), null);
	}

	@Background
	@Trace
	void cleanUpLocalFiles(Entry folderEntry) {
		File[] localFilesA = mExtDir.listFiles();
		HashMap<String, File> nameToFilesToDelete = new HashMap<String, File>();
		for (File lf : localFilesA) {
			nameToFilesToDelete.put(lf.getName(), lf);
		}

		List<Entry> remoteFiles = folderEntry.contents;

		for (Entry re : remoteFiles) {
			if (!re.isDeleted) {
				nameToFilesToDelete.remove(re.fileName());
			}
		}

		for (String s : nameToFilesToDelete.keySet()) {
			File f = nameToFilesToDelete.get(s);
			f.delete();
			Log.v(TAG,
					String.format("Deleted %s while cleaning up", f.getName()));
			// todo check for success
		}

		syncFinish(null, new CleanUpResult());
	}

	private Entry getRemoteFolderContent() {
		String hash = prefs.folderHash().get();
		try {
			// todo we're always getting "nothing changed" here
			Entry folderEntry = mApi.metadata(remoteDbFolder, -1, null, true,
					null);
			prefs.edit().folderHash().put(folderEntry.hash).apply();

			Log.v(TAG, "Got new folder entry: " + folderEntry);

			return folderEntry;
		} catch (DropboxServerException e) {
			if (e.error == DropboxServerException._304_NOT_MODIFIED) {
				mRemoteFolderUnchanged = true;
				Log.v(TAG, "Remote folder did not change, no update necessary.");
			}
		} catch (DropboxException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Joins updateLocalFiles() with cleanUp(). These were forked by sync().
	 * 
	 * @param u
	 *            null when calling from cleanUp(), not null otherwise
	 * @param c
	 *            null when calling from updateLocalFiles(), not null otherwise
	 */
	@UiThread
	void syncFinish(UpdateResult u, CleanUpResult c) {
		if (u != null) {
			mUpdateFinished = u;
		} else if (c != null) {
			mCleanUpFinished = c;
		}

		if (mUpdateFinished != null && mCleanUpFinished != null) {
			mUpdateFinished = null;
			mCleanUpFinished = null;
			mIsSyncing = false;
			mCallback.syncFinished();
		}
	}

	private void scheduleNext() {
		// TODO Auto-generated method stub
		mRemoteFolderUnchanged = false;
		mCallback.syncFinished();
	}

	class UpdateResult {
	}

	class CleanUpResult {
	}

	public void setCallback(DownSyncCallback callback) {
		this.mCallback = callback;
	}
}
