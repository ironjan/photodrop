package com.github.ironjan.photodrop.dbwrap;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxFileSystem.PathListener;
import com.dropbox.sync.android.DbxPath;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class PathListenerBean implements PathListener {

	
	
	@Override
	public void onPathChange(DbxFileSystem fs, DbxPath registeredPath,
			Mode registeredMode) {
		downloadChanged(fs,registeredPath);
	}

	@Background
	void downloadChanged(DbxFileSystem fs, DbxPath registeredPath) {
		try {
			DbxFileInfo fileInfo = fs.getFileInfo(registeredPath);
			
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
