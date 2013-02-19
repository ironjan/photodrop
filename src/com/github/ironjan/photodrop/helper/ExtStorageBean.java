package com.github.ironjan.photodrop.helper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;

import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class ExtStorageBean {

	private boolean mExternalStorageAvailable = false,
			mExternalStorageWriteable = false;

	private static final int SDK = Build.VERSION.SDK_INT;
	private static final int FROYO = Build.VERSION_CODES.FROYO;

	private void updateStorageState() {
		mExternalStorageAvailable = false;
		mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}

	@SuppressLint("SimpleDateFormat")
	public File createImageFile() throws Exception {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = timeStamp + ".jpg";
		File image = new File(getSharedPicturesDir(),imageFileName);
		return image;
	}

	private File getSharedPicturesDir() throws Exception {
		updateStorageState();
		if (!(mExternalStorageAvailable && mExternalStorageWriteable)) {
			throw new Exception("External storage not available");
		}
		if (SDK < FROYO) {
			return getSharedPicturesDirPreFroyo();
		}

		return getSharedPicturesDirPostFroyo();
	}

	private static File getSharedPicturesDirPostFroyo() {
		return Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	}

	private static File getSharedPicturesDirPreFroyo() {
		File extDir = Environment.getExternalStorageDirectory();
		return new File(extDir, Environment.DIRECTORY_PICTURES);
	}
}
