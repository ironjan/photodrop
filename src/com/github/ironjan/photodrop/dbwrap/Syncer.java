package com.github.ironjan.photodrop.dbwrap;

import java.io.File;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.Trace;

@EBean
public class Syncer {
	@Bean
	DownSync downSync;

	@Background
	@Trace
	public void downSync() {
		downSync.autoSync();
	}
	
	@Background
	@Trace
	public void forceDownSync(){
		downSync.sync();
	}

	public static void upload(File f) throws Exception {
		throw new Exception("Not implemented yet"); //$NON-NLS-1$
	}

	public void setDownSyncCallback(
			DownSyncCallback callback) {
		downSync.setCallback(callback);
	}
}
