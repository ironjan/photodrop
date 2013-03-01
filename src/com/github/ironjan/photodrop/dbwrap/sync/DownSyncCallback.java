package com.github.ironjan.photodrop.dbwrap.sync;

public interface DownSyncCallback {
	public void syncFinished();

	public void syncStarted();
}
