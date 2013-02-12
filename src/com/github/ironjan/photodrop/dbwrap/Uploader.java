package com.github.ironjan.photodrop.dbwrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.github.ironjan.photodrop.model.DirKeeper;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class Uploader {
	@StringRes
	String dropboxExceptionUnknownCauseLog, dropboxParseException,
			dropboxParseExceptionLog, dropboxNetworkExceptionLog,
			dropboxUnlinkedExceptionLog, dropboxFileTooBig,
			dropboxPartialFileException, remoteDbFolder, unhandledExceptionLog;

	private static final String TAG = Uploader.class.getSimpleName();

	@Bean
	SessionKeeper mSessionKeeper;

	private DropboxAPI<AndroidAuthSession> mApi;

	private Vector<File> mUploads = new Vector<File>();

	private boolean mExecuting;

	@RootContext
	Context context;

	@Bean
	DirKeeper mDirKeeper;
	@AfterInject
	void fetchMApi() {
		mApi = mSessionKeeper.getmDBApi();
	}

	private void upload(File file) throws RetryLaterException, DropboxException {
		String path = remoteDbFolder + file.getName();
		try {
			FileInputStream fis = new FileInputStream(file);

			UploadRequest request = mApi.putFileOverwriteRequest(path, fis,
					file.length(), null);

			if (request != null) {
				request.upload();
				// todo save rev !
			}
			fis.close();
		} catch (DropboxPartialFileException e) {
			Log.e(TAG, dropboxPartialFileException, e);
			throw new RetryLaterException();
		} catch (DropboxServerException e) {
			Log.e(TAG, e.body.userError, e);
			throw new RetryLaterException();
		} catch (DropboxIOException e) {
			Log.e(TAG, dropboxNetworkExceptionLog, e);
			throw new RetryLaterException();
		} catch (DropboxParseException e) {
			Log.e(TAG, dropboxParseException, e);
			throw new RetryLaterException();
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public synchronized void addToUpload(File file) {
		if (file != null) {
			mUploads.add(file);
			Log.v(TAG,
					String.format("Added %s to upload queue", file.getPath())); //$NON-NLS-1$
		}
	}

	public synchronized void exec() {
		if (mExecuting) {
			return;
		}
		mExecuting = true;
		final int queueSize = mUploads.size();
		Log.v(TAG,
				String.format(
						"Start upload execution with %d files in queue", Integer.valueOf(queueSize)));//$NON-NLS-1$
		int finished = 0;
		Vector<File> tUploads = new Vector<File>();

		for (File f : mUploads) {
			try {
				Log.v(TAG, String.format("Trying to upload %s", f.getPath()));//$NON-NLS-1$
				upload(f);
				finished++;
				Log.v(TAG,
						String.format("Finished %d/%d (%s)",//$NON-NLS-1$
								Integer.valueOf(finished),
								Integer.valueOf(queueSize), f.getPath()));
			} catch (RetryLaterException e) {
				Log.e(TAG, e.getMessage(), e);
				tUploads.add(f);
			} catch (DropboxUnlinkedException e) {
				mSessionKeeper.unlink();
			} catch (DropboxFileSizeException e) {
				Log.e(TAG, e.getMessage(), e);
				tUploads.add(f);
				// todo notify user? ignore?
			} catch (DropboxException e) {
				Log.e(TAG, e.getMessage(), e);
				tUploads.add(f);
			}
		}

		mUploads = tUploads;
		Log.v(TAG, String.format(
				"Upload finished with %d elements left to upload",//$NON-NLS-1$
				Integer.valueOf(mUploads.size())));

		if (mUploads.size() > 0) {
			// schedule next try
		}
		mExecuting = false;
	}

	public boolean hasUploadsLeft() {
		return mUploads.size() > 0;
	}

	class RetryLaterException extends Exception {/* stub */
	}
}
