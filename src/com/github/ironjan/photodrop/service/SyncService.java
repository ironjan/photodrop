package com.github.ironjan.photodrop.service;

import android.app.IntentService;
import android.content.Intent;

import com.github.ironjan.photodrop.dbwrap.Syncer;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;

/**
 * This service starts a <b>automatic</b> downSync every time it receives an intent. 
 */
@EService
public class SyncService extends IntentService{

	@Bean
	Syncer mSyncer;
	
	public SyncService() {
		super(SyncService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		mSyncer.downSync();
	}

}
