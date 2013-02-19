package com.github.ironjan.photodrop.model;

import java.io.File;
import java.io.FileWriter;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.github.ironjan.photodrop.dbwrap.DropboxWrapper;
import com.github.ironjan.photodrop.persistence.PostMetadata;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class PostSharer {
	private static final String TAG = null;

	@Bean
	DropboxWrapper mSessionKeeper;

	/**
	 * Convenience method for share(imageUri, null, comment)
	 * 
	 * @param imageUri
	 *            the shared image
	 * @param comment
	 *            the user's comment
	 * @throws Exception 
	 */
	public void share(Uri imageUri, String comment) throws Exception {
		share(imageUri, null, comment);
	}

	/**
	 * Copys the image behing imageUri into the local folder, saves the metadata
	 * and starts the upload into Dropbox
	 * 
	 * @param imageUri
	 *            the shared image
	 * @param location
	 *            where the image was shared
	 * @param comment
	 *            the user's comment
	 * @throws Exception
	 */
	public void share(Uri imageUri, Location location, String comment)
			throws Exception {
		Log.v(TAG, String.format("Sharing %s", imageUri)); //$NON-NLS-1$

		File imageFile = new File(imageUri.getPath()); // fixme does this work

		PostMetadata metadata = new PostMetadata(location, comment);
		File metadataFile = saveMetadata(imageFile.getName(), metadata);

		mSessionKeeper.add(imageFile);
		mSessionKeeper.add(metadataFile);
	}

	private static File saveMetadata(String name, PostMetadata metadata)
			throws Exception {
		// todo what to do if we could not write metadata?

		try {
			File metadataFile = File.createTempFile(name, ".meta"); //$NON-NLS-1$
			FileWriter fw = new FileWriter(metadataFile);
			fw.write(metadata.toFileContent());
			fw.close();
			return metadataFile;
		} catch (Exception e) {
			final String message = "Could not write metadata"; //$NON-NLS-1$
			Log.e(TAG, message, e);
			throw new Exception(message);
		}
	}

}
