package com.github.ironjan.photodrop.dbwrap.sync;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.Extra;

/**
 * This service starts a <b>automatic</b> downSync every time it receives an
 * intent.
 */
@EService
public class SyncService extends IntentService {

	public static final String KEY_FORCED_EXTRA = "forced";

	@Bean
	Syncer mSyncer;

	public SyncService() {
		super(SyncService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v("syncService", "got intent");

		mSyncer.downSync();
	}

}
