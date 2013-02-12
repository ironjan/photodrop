package com.github.ironjan.photodrop.dbwrap;

import com.github.ironjan.photodrop.helper.ConnectionBean;
import com.github.ironjan.photodrop.helper.Prefs_;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
class DownSync {

	@Bean
	ConnectionBean cb;

	@Pref
	Prefs_ prefs;

	private boolean mIsSyncing;

	private Object mCleanUpFinished;

	private Object mUpdateFinished;

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

		if (isRemoteFolderUnchanged()) {
			scheduleNext();
		}

		Object o = getRemoteFolderContent();

		cleanUpLocalFiles();
		updateLocalFiles();
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
		}
	}

	private void updateLocalFiles() {
		// TODO Auto-generated method stub

		syncFinish(new UpdateResult(), null);
	}

	private void cleanUpLocalFiles() {
		// TODO Auto-generated method stub

		syncFinish(null, new CleanUpResult());
	}

	private Object getRemoteFolderContent() {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isRemoteFolderUnchanged() {
		// TODO Auto-generated method stub
		return false;
	}

	private void scheduleNext() {
		// TODO Auto-generated method stub

	}

	class UpdateResult {
	}

	class CleanUpResult {
	}
}
