package com.github.ironjan.photodrop.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.github.ironjan.photodrop.dbwrap.Uploader;
import com.github.ironjan.photodrop.helper.ImageStorage;
import com.github.ironjan.photodrop.persistence.PostMetadata;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class PostSharer {
	private static final String TAG = null;

	@Bean
	DirKeeper mDirKeeper;

	@Bean
	Uploader mUploader;
	
	@Bean
	ImageStorage mImageStorage;

	/**
	 * Convenience method for share(imageUri, null, comment)
	 * 
	 * @param imageUri
	 *            the shared image
	 * @param comment
	 *            the user's comment
	 */
	public void share(Uri imageUri, String comment) {
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
	 */
	@Background
	public void share(Uri imageUri, Location location, String comment) {
		Log.v(TAG, String.format("Sharing %s", imageUri)); //$NON-NLS-1$
		File photoFile = copyImageToDir(imageUri);
		PostMetadata metadata = new PostMetadata(location, comment);
		File metadataFile = saveMetadata(photoFile, metadata);
		mUploader.addToUpload(photoFile);
		mUploader.addToUpload(metadataFile);
		mUploader.exec();
	}

	private File saveMetadata(File photoFile, PostMetadata metadata) {
		// todo what to do if we could not write metadata?
		String metadataFileName = photoFile.getName().concat(".meta"); //$NON-NLS-1$

		File metadataFile = new File(mDirKeeper.getExtFilesDir(),
				metadataFileName);
		try {
			FileWriter fw = new FileWriter(metadataFile);
			fw.write(metadata.toFileContent());
			fw.close();
			return metadataFile;
		} catch (Exception e) {
			Log.e(TAG, "Could not write metadata", e); //$NON-NLS-1$
		}
		
		return null;
	}

	/**
	 * Copys the given file to our app's directory. Based on
	 * http://stackoverflow
	 * .com/questions/9292954/how-to-make-a-copy-of-a-file-in-android
	 * 
	 * @param imageUri
	 * @return
	 */
	private File copyImageToDir(Uri imageUri) {
		File imageFile = mImageStorage.getImageFile(String.format(
				"%s", imageUri)); //$NON-NLS-1$
		File targetDir = mDirKeeper.getExtFilesDir();
		
		if (imageFile.getParentFile().equals(targetDir)) {
			return imageFile; // already in correct folder
		}

		File targetFile = new File(targetDir, imageFile.getName());

		try {
			InputStream in = new FileInputStream(imageFile);
			OutputStream out = new FileOutputStream(targetFile);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			Log.e(TAG, "Could not copy file to app directory", e); //$NON-NLS-1$
		}
		return targetFile;
	}

}
