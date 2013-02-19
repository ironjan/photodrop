package com.github.ironjan.photodrop.dbwrap.sync;

import java.io.File;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class Syncer {
	@Bean
	DownSync downSync;

	protected void downSync() {
		downSync.autoSync();
	}
	
	public void forceDownSync(){
		downSync.forceSync();
	}

	public static void upload(File f) throws Exception {
		throw new Exception("Not implemented yet"); //$NON-NLS-1$
	}

	public void setDownSyncCallback(
			DownSyncCallback callback) {
		downSync.setCallback(callback);
	}
}
