package com.github.ironjan.photodrop.dbwrap;

import java.io.File;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class Syncer {
	@Bean
	DownSync downSync;

	public void downSync() {
		downSync.autoSync();
	}
	
	public void forceDownSync(){
		downSync.sync();
	}

	public static void upload(File f) throws Exception {
		throw new Exception("Not implemented yet"); //$NON-NLS-1$
	}
}
