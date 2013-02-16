package com.github.ironjan.photodrop.persistence;

import java.io.IOException;

import android.util.Log;

import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxPath;
import com.github.ironjan.photodrop.dbwrap.DropboxWrapper;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class PostCreator {
	@Bean
	DropboxWrapper mSessionKeeper;

	private Post fromDropboxPaths(DbxPath meta, DbxPath photo)
			throws IOException {
		DbxFile metaFile = mSessionKeeper.getDropboxFilesystem().open(meta);
		DbxFile photoFile = mSessionKeeper.getDropboxFilesystem().open(photo);

		PostMetadata metadata = readMetadata(metaFile);

		metaFile.close();
		photoFile.close();
		return new Post(metadata, photo);
	}

	private static PostMetadata readMetadata(DbxFile metaFile)
			throws IOException {
		return PostMetadata.fromDropboxFile(metaFile);
	}

	public Post fromDropboxMetaFileInfo(DbxFileInfo fi) {
		DbxPath metaPath = fi.path;
		DbxPath photoPath = new DbxPath(
				(fi.path.toString().replace(".meta", "")));  //$NON-NLS-1$//$NON-NLS-2$

		try {
			return fromDropboxPaths(metaPath, photoPath);
		} catch (IOException e) {
			Log.e(PostCreator.class.getSimpleName(), e.getMessage(), e);
			return null;
		}
	}
}
