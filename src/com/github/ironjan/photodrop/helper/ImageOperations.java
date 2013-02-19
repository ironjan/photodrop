package com.github.ironjan.photodrop.helper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxPath;
import com.github.ironjan.photodrop.dbwrap.DropboxWrapper;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class ImageOperations {
	private static final String TAG = ImageOperations.class.getSimpleName();

	@Bean
	DropboxWrapper mDbWrapper;

	@Bean
	ExtStorageBean mExtStorageBean;

	public synchronized Bitmap loadScaledImage(DbxPath path) throws IOException {
		byte[] bytes = mDbWrapper.loadDropboxPathToByteArray(path);
		int sampleSize = calculateSampleSizeForThumb(bytes);
		Bitmap bm = loadSampleSizedImage(bytes, sampleSize);
		return bm;
	}

	private static Bitmap loadSampleSizedImage(byte[] bytes, int sampleSize) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = sampleSize;
		Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		return bm;
	}

	private static int calculateSampleSizeForThumb(byte[] bytes) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		final int width = opts.outWidth;
		final int heigth = opts.outHeight;

		int sampleSize = 1;

		while ((width / sampleSize) > 512 && (heigth / sampleSize) > 512) {
			sampleSize *= 2;
		}
		return sampleSize;
	}

	/**
	 * This method loads the image given by path into the given ImageView
	 * 
	 * @param path
	 *            the images dropbox path
	 * @param imgPhoto
	 *            the iamge view to load into
	 */
	@Background
	public void loadScaledImageInto(DbxPath path, ImageView imgPhoto) {
		try {
			Bitmap bm = loadScaledImage(path);
			imageLoaded(imgPhoto, bm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-method")
	// annotation -> cannot be static
	@UiThread
	void imageLoaded(ImageView imgPhoto, Bitmap bm) {
		imgPhoto.setImageBitmap(bm);
	}

	public Uri newPhotoUri() {
		try {
			return Uri.fromFile(mExtStorageBean.createImageFile());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

}
