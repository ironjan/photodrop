package com.github.ironjan.photodrop.persistence;

import java.io.IOException;
import java.nio.channels.AlreadyConnectedException;

import android.util.Log;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxException.AlreadyOpen;
import com.github.ironjan.photodrop.dbwrap.DropboxWrapper;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class PostCreator {
	private static final String TAG = null;
	@Bean
	DropboxWrapper mSessionKeeper;

	private synchronized Post fromDropboxPaths(DbxPath meta, DbxPath photo)
			throws AlreadyOpen, DbxException, IOException {
		Post p = null;
		DbxFile metaFile = null;

		try {
			Log.v(TAG, "try  to open " + meta); //$NON-NLS-1$
			metaFile = mSessionKeeper.open(meta);
			Log.v(TAG, "opened " + meta); //$NON-NLS-1$
			PostMetadata metadata = readMetadata(metaFile);

			p = new Post(metadata, photo);
		} finally {
			if (metaFile != null) {
				metaFile.close();
				Log.v(TAG, "closed " + meta);
			}
		}
		return p;
	}

	private static PostMetadata readMetadata(DbxFile metaFile)
			throws IOException {
		return PostMetadata.fromDropboxFile(metaFile);
	}

	public Post fromDropboxMetaFileInfo(DbxFileInfo fi) {
		Post p = null;
		try {
			DbxPath metaPath = fi.path;
			DbxPath photoPath = new DbxPath((fi.path.toString().replace(
					".meta", ""))); //$NON-NLS-1$//$NON-NLS-2$

			p = fromDropboxPaths(metaPath, photoPath);
		} catch (IOException e) {
			Log.e(PostCreator.class.getSimpleName(), e.getMessage(), e);
		}

		return p;
	}
}
