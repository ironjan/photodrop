package com.github.ironjan.photodrop.dbwrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream.GetField;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;
import com.github.ironjan.photodrop.StartActivity;
import com.github.ironjan.photodrop.helper.ConnectionBean;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class DropboxWrapper {
	private static final String TAG = DropboxWrapper.class.getSimpleName();

	private DbxAccountManager mDbxAcctMgr;

	@StringRes
	String appKey, appSecret, authentificationNoInternet,
			authentificationError;

	@Bean
	ConnectionBean connectionBean;

	@RootContext
	Context context;

	private DbxFileSystem mDbxFs;

	@Bean
	SyncStatusListenerBean mSyncStatusListener;

	@AfterInject
	void init() {
		mDbxAcctMgr = DbxAccountManager.getInstance(context, appKey, appSecret);
	}

	public void startAuthentication(Activity activity) {
		if (connectionBean.hasInternetConnection()) {
			mDbxAcctMgr.startLink(activity, StartActivity.REQUEST_LINK_TO_DBX);
		} else {
			throw new IllegalStateException(authentificationNoInternet);
		}
	}

	/**
	 * Unlinks the user.
	 */
	public void unlink() {
		mDbxAcctMgr.unlink();
	}

	public boolean isLinked() {
		return mDbxAcctMgr.hasLinkedAccount();
	}

	public DbxFileSystem getDropboxFilesystem() throws DbxException {
		if (mDbxAcctMgr.hasLinkedAccount()) {
			this.mDbxFs = DbxFileSystem.forAccount(mDbxAcctMgr
					.getLinkedAccount());
			mDbxFs.addSyncStatusListener(mSyncStatusListener);
		}

		return mDbxFs;
	}

	public void add(File localFile) {
		DbxFile remoteFile = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			remoteFile = mDbxFs.create(new DbxPath(localFile.getName()));

			in = new FileInputStream(localFile);
			out = remoteFile.getWriteStream();
			copyFromInToOut(in, out);

			remoteFile.close();
			in.close();
			out.close();
		} catch (InvalidPathException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (DbxException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private static void copyFromInToOut(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public void sync(SyncStatusListenerBean syncListener) throws DbxException {
		mDbxFs.addSyncStatusListener(syncListener);
		mDbxFs.syncNowAndWait();
	}

	public List<DbxFileInfo> listFiles() throws DbxException{
		return getDropboxFilesystem().listFolder(DbxPath.ROOT);
	}
	
}
