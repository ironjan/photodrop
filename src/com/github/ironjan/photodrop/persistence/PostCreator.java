package com.github.ironjan.photodrop.persistence;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.github.ironjan.photodrop.helper.ImageStorage;
import com.github.ironjan.photodrop.model.DirKeeper;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class PostCreator {
	private static final String TAG = null;

	@Bean
	DirKeeper mDirKeeper;

	String mDirPath;

	@AfterInject
	void setMDirPath() {
		mDirPath = mDirKeeper.getExtFilesDir().getPath();
	}

	@Bean
	ImageStorage mImageStorage;

	public Post fromMetadataFile(String metadataFilePath) {
		final String fullPath;
		if (metadataFilePath.contains(mDirPath)) {
			fullPath = metadataFilePath;
		} else {
			fullPath = mDirPath.concat("/").concat(metadataFilePath); //$NON-NLS-1$
			Log.w(TAG, "Had to correct path of metafile"); //$NON-NLS-1$
		}
		File metadataFile = new File(fullPath);
		PostMetadata metadata = readMetadata(metadataFile);

		final String photoFilePath = fullPath.replace(".meta", ""); //$NON-NLS-1$ //$NON-NLS-2$

		boolean photoExists = isPhotoExistent(photoFilePath);
		boolean hasMetadata = metadata != null;
		boolean isValidPost = photoExists && hasMetadata;

		if (isValidPost) {
			String photoUri = String.format(
					"%s", mImageStorage.reconstructUri(photoFilePath)); //$NON-NLS-1$

			return new Post(metadata, photoUri);
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
