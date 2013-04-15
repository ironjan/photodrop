package com.github.ironjan.photodrop.persistence;

import java.io.*;

import android.graphics.*;

import com.github.ironjan.photodrop.helper.*;
import com.github.ironjan.photodrop.model.*;
import com.googlecode.androidannotations.annotations.*;
import com.googlecode.androidannotations.api.*;

@EBean(scope = Scope.Singleton)
public class PostCreator {
	private static final String TAG = null;

	@Bean
	DirKeeper mDirKeeper;

	String mDirPath;

	@AfterInject
	void setMDirPath() {
		File extFilesDir = mDirKeeper.getExtFilesDir();
		mDirPath = extFilesDir.getPath();
	}

	@Bean
	ImageStorage mImageStorage;

	/**
	 * Creates post from metadata file
	 * 
	 * @param metadataFilePath
	 *            the post's metadata file
	 * @return the corresponding post
	 */
	public Post fromMetadataFile(String metadataFilePath) {
		if (!metadataFilePath.endsWith(".meta")) {
			return null;
		}

		String fullPath = metadataFilePath;
		if (!fullPath.contains(mDirPath)) {
			fullPath = mDirPath.concat("/").concat(metadataFilePath); //$NON-NLS-1$
		}

		File metadataFile = new File(fullPath);
		PostMetadata metadata = readMetadata(metadataFile);

		final String photoFilePath = fullPath.replace(".meta", ""); //$NON-NLS-1$ //$NON-NLS-2$

		boolean photoExists = isPhotoExistent(photoFilePath);
		boolean hasMetadata = metadata != null;
		boolean isValidPost = photoExists && hasMetadata;

		if (isValidPost) {
			return new Post(metadata, photoFilePath);
		}

		return null;
	}

	private static boolean isPhotoExistent(final String photoFilePath) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		Bitmap bm = BitmapFactory.decodeFile(photoFilePath);
		return bm != null;
	}

	private static PostMetadata readMetadata(File metadataFile) {
		return PostMetadata.fromFile(metadataFile);
	}
}
