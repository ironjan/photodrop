package com.github.ironjan.photodrop.model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.github.ironjan.photodrop.StartActivity_;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

@EBean
public class DirKeeper {
	@RootContext
	Context context;

	private static final int FROYO = Build.VERSION_CODES.FROYO;
	private static final int SDK_INT = Build.VERSION.SDK_INT;

	private static final String packageName = StartActivity_.class.getPackage()
			.getName();

	@SuppressWarnings("nls")
	private static final String APP_FOLDER_PATH = "/Android/data/"
			+ packageName + "/files/";

	private static final String TAG = DirKeeper.class.getSimpleName();

	private boolean mExternalStorageAvailable;

	private boolean isExtStorageAvailable() {
		updateStorageAvailability();
		return mExternalStorageAvailable;
	}

	/**
	 * @return the external directory to save the files of this app. null if external storage not available
	 */
	public File getExtFilesDir() {
		if (!isExtStorageAvailable()) {
			Log.w(TAG, Environment.getExternalStorageState());
			return null;
		}

		if (SDK_INT >= FROYO) {
			return getExtFileDirPostFroyo();
		}
		return getExtFileDirPreFroyo();
	}

	private static File getExtFileDirPreFroyo() {
		File extStorageDir = Environment.getExternalStorageDirectory();
		File appFolder = new File(extStorageDir, APP_FOLDER_PATH);

		if (!appFolder.exists()) {
			if (!appFolder.mkdirs()) {
				throw new IllegalStateException(
						"Could not create local storage dir"); //$NON-NLS-1$
			}
		}
		return appFolder;
	}

	private File getExtFileDirPostFroyo() {
		return context.getExternalFilesDir(null);
	}

	void updateStorageAvailability() {
		mExternalStorageAvailable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
		} else {
			mExternalStorageAvailable = false;
		}
	}

	public File createNewPhotofile() {
		final File dir = getExtFilesDir();
		
		File f = new File(dir, createFileName());
		
		return f;
	}

	private String createFileName() {
		// todo create with simple date 
		return String.format(Locale.GERMAN, "%s.jpg", Long.valueOf(System.currentTimeMillis())); //$NON-NLS-1$
	}

}
