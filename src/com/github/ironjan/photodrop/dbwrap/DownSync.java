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
			return;
		}

		if (!cb.hasInternetConnection()) {
			scheduleNext();
		}

		Entry folderEntry = getRemoteFolderContent();

		if (folderEntry == null || mRemoteFolderUnchanged == true) {
			scheduleNext();
			return;
		}

		cleanUpLocalFiles(folderEntry);
		updateLocalFiles(folderEntry);
	}

	private void updateLocalFiles(Entry folderEntry) {
		Log.v(TAG, "opened DB");
		fileRevDao.open();

		List<Entry> remoteFiles = folderEntry.contents;

		Vector<String> update = new Vector<String>();

		for (Entry re : remoteFiles) {
			String fileName = re.fileName();
			FileRev frev = fileRevDao.findOrCreateFileRevByName(fileName);
			if (!re.rev.equals(frev.rev)) {
				update.add(fileName);
			}
		}

		if (update.size() == 0) {
			Log.v(TAG, "nothing to update");
			return;
		}

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
				System.out.println("Something went wrong: " + e);
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
					}
				}
			}
		}

		syncFinish(new UpdateResult(), null);
	}

	private void cleanUpLocalFiles(Entry folderEntry) {
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
			Entry folderEntry = mApi.metadata(remoteDbFolder, -1, hash, true,
					null);
			prefs.edit().folderHash().put(folderEntry.hash).apply();
			return folderEntry;
		} catch (DropboxServerException e) {
			if (e.error == DropboxServerException._304_NOT_MODIFIED) {
				mRemoteFolderUnchanged = true;
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
	}

	class UpdateResult {
	}

	class CleanUpResult {
	}

	public void setCallback(DownSyncCallback callback) {
		this.mCallback = callback;
	}
}
