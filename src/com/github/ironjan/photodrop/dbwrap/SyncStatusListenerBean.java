package com.github.ironjan.photodrop.dbwrap;

import android.util.Log;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxFileSystem.SyncStatusListener;
import com.dropbox.sync.android.DbxSyncStatus;
import com.dropbox.sync.android.DbxSyncStatus.OperationStatus;
import com.github.ironjan.photodrop.fragments.SyncListenerCallback;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class SyncStatusListenerBean implements SyncStatusListener {

	private static final String TAG = SyncStatusListenerBean.class
			.getSimpleName();

	private static final SyncListenerCallback mDummy = new SyncListenerCallback() {

		@Override
		public void downloadStatusChanged(OperationStatus download) { /* dummy */
		}

		@Override
		public void syncActiveChanged(boolean isSyncActive) { /* dummy */
		}

		@Override
		public void uploadStatusChanged(OperationStatus upload) { /* dummy */
		}

		@Override
		public void metadataStatusChanged(OperationStatus metadata) { /* dummy */
		}
	};
	private SyncListenerCallback mCallback = mDummy;

	OperationStatus mLastStatusDownload, mLastStatusUpload,
			mLastStatusMetadata;
	boolean mLastStatusIsSyncActive;

	@Override
	public void onSyncStatusChange(DbxFileSystem fs) {
		try {
			DbxSyncStatus status = fs.getSyncStatus();
			propagateChanges(status);
			updateSavedStatus(status);
		} catch (DbxException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private void propagateChanges(DbxSyncStatus status) {
		propagateDownloadChange(status);
		propagateUploadChange(status);
		propagateMetadataChange(status);
		propagateSyncChange(status);
	}

	private void propagateSyncChange(DbxSyncStatus status) {
		boolean isSyncActive = status.isSyncActive;
		if (isSyncActive != mLastStatusIsSyncActive) {
			mCallback.syncActiveChanged(isSyncActive);
		}
	}

	private void propagateMetadataChange(DbxSyncStatus status) {
		OperationStatus metadata = status.metadata;
		if (!metadata.equals(mLastStatusMetadata)) {
			mCallback.metadataStatusChanged(metadata);
		}
	}

	private void propagateUploadChange(DbxSyncStatus status) {
		OperationStatus upload = status.upload;
		if (!upload.equals(mLastStatusUpload)) {
			mCallback.uploadStatusChanged(upload);
		}
	}

	private void propagateDownloadChange(DbxSyncStatus status) {
		OperationStatus download = status.download;
		if (!download.equals(mLastStatusDownload)) {
			mCallback.downloadStatusChanged(download);
		}
	}

	private void updateSavedStatus(DbxSyncStatus status) {
		mLastStatusDownload = status.download;
		mLastStatusUpload = status.upload;
		mLastStatusMetadata = status.metadata;
		mLastStatusIsSyncActive = status.isSyncActive;
	}

	public void setCallBack(SyncListenerCallback callback) {
		this.mCallback = callback;
	}

}
