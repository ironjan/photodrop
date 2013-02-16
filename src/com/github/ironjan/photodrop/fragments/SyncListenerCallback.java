package com.github.ironjan.photodrop.fragments;

import com.dropbox.sync.android.DbxSyncStatus.OperationStatus;

public interface SyncListenerCallback {

	void downloadStatusChanged(OperationStatus download);

	void syncActiveChanged(boolean isSyncActive);

	void uploadStatusChanged(OperationStatus upload);

	void metadataStatusChanged(OperationStatus metadata);

}
