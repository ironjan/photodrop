package com.github.ironjan.photodrop.persistence;

import java.io.IOException;

import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxPath;
import com.github.ironjan.photodrop.dbwrap.DropboxWrapper;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class PostCreator {
	@Bean
	DropboxWrapper mSessionKeeper;
	
	public Post fromDropboxFiles(DbxPath meta, DbxPath photo) throws IOException {
		DbxFile metaFile = mSessionKeeper.getDropboxFilesystem().open(meta);
		DbxFile photoFile = mSessionKeeper.getDropboxFilesystem().open(photo);
		
		PostMetadata metadata = readMetadata(metaFile);

		metaFile.close();
		photoFile.close();
		return new Post(metadata, photo);
	}

	private static PostMetadata readMetadata(DbxFile metaFile) throws IOException {
		return PostMetadata.fromDropboxFile(metaFile);
	}
}
