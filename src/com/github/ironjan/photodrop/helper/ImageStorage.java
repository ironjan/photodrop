package com.github.ironjan.photodrop.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.github.ironjan.photodrop.dbwrap.SessionKeeper;
import com.github.ironjan.photodrop.model.DirKeeper;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.api.Scope;

/**
 * @author ljan
 * 
 */
@EBean(scope = Scope.Singleton)
public class ImageStorage {

	private final static String TAG = ImageStorage.class.getSimpleName();

	private static final String[] filePathColumn = { MediaColumns.DATA,
			MediaColumns.DISPLAY_NAME };

	@SuppressWarnings("nls")
	private final static String jpgSuffix = ".jpg";

	@StringRes
	String picasaUrl, noPicasaSupport, photoNameScheme, picasaPreHC,
			picasaPostHC, picturesDir, remoteDbFolder, unhandledExceptionLog,
			dropboxExceptionUnknownCauseLog, dropboxParseException,
			dropboxParseExceptionLog, dropboxNetworkExceptionLog,
			dropboxUnlinkedExceptionLog, dropboxFileTooBig,
			dropboxPartialFileException;

	@RootContext
	Context context;

	private DropboxAPI<AndroidAuthSession> mApi;

	@Bean
	SessionKeeper mSessionKeeper;

	@Bean
	DirKeeper dirKeeper;

	void upload(File file) {
		if (file == null) {
			return;
		}
		String path = remoteDbFolder + file.getName();

		try {
			FileInputStream fis = new FileInputStream(file);

			UploadRequest request = mApi.putFileOverwriteRequest(path, fis,
					file.length(), null);

			if (request != null) {
				request.upload();
			}
			fis.close();

		} catch (DropboxUnlinkedException e) {
			Log.e(TAG, dropboxUnlinkedExceptionLog, e);
		} catch (DropboxFileSizeException e) {
			Log.e(TAG, dropboxFileTooBig, e);
		} catch (DropboxPartialFileException e) {
			Log.e(TAG, dropboxPartialFileException, e);
		} catch (DropboxServerException e) {
			Log.e(TAG, e.body.userError, e);
		} catch (DropboxIOException e) {
			Log.e(TAG, dropboxNetworkExceptionLog, e);
			// Happens all the time, probably want to retry automatically.
			// TODO retry
		} catch (DropboxParseException e) {
			// Probably due to Dropbox server restarting, should retry
			Log.e(TAG, dropboxParseException, e);
			// TODO retry
		} catch (DropboxException e) {
			// Unknown error
			Log.e(TAG, dropboxExceptionUnknownCauseLog, e);
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Trace
	public Bitmap getBitmap(String filePath) {
		File imageFile = getImageFile(filePath);

		if (imageFile == null) {
			return null;
		}

		Bitmap image = ImageOperations.loadScaledImage(imageFile.getPath(),
				2048, 2048);
		return image;
	}

	private File getImageFile(String filePath) {
		Uri imageUri = reconstructUri(filePath);

		File imageFile = null;

		Cursor cursor = context.getContentResolver().query(imageUri,
				filePathColumn, null, null, null);

		if (cursor == null && filePath.length() > 0) {
			imageFile = fetchPicasaPreHC(imageUri);
		} else if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(MediaColumns.DATA);
			if (filePath.startsWith(picasaPostHC)) {
				imageFile = fetchPicasaPostHC(imageUri, cursor);
			} else {
				imageFile = fetchLocal(cursor, columnIndex);
			}
			cursor.close();
		}
		return imageFile;
	}

	private static File fetchLocal(Cursor cursor, int columnIndex) {
		String filePath = cursor.getString(columnIndex);
		File imageFile = new File(filePath);
		return imageFile;
	}

	private File fetchPicasaPostHC(Uri uri, Cursor cursor) {
		int columnIndex;
		columnIndex = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME);
		File imageFile = null;
		if (columnIndex != -1) {
			imageFile = downloadPicasaImage(uri);
		}
		return imageFile;
	}

	private File fetchPicasaPreHC(Uri uri) {
		return downloadPicasaImage(uri);
	}

	@Trace
	File downloadPicasaImage(Uri url) {
		File file = createImageFile();
		if (file == null) {
			return null;
		}
		InputStream is = null;
		OutputStream os = null;
		try {

			os = new FileOutputStream(file);

			if (url.toString().startsWith(picasaPostHC)) {
				is = context.getContentResolver().openInputStream(url);
			} else {
				is = new URL(url.toString()).openStream();
			}

			byte[] buffer = new byte[1024];
			int len = is.read(buffer);
			while (len != -1) {
				os.write(buffer, 0, len);
				len = is.read(buffer);
			}

			is.close();
			os.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);

			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					Log.e(TAG, ex.getMessage(), ex);
				}
			}

			if (os != null) {
				try {
					os.close();
				} catch (IOException ex) {
					Log.e(TAG, ex.getMessage(), ex);
				}
			}
		}

		return file;
	}

	private Uri reconstructUri(String filePath) {
		if (filePath.startsWith(picasaPreHC)) {
			return Uri.parse(filePath.toString().replace(
					"content://com.android.gallery3d", picasaPostHC));//$NON-NLS-1$
		}

		return Uri.parse(filePath);
	}

	@SuppressLint("SimpleDateFormat")
	public File createImageFile() {
		String timeStamp = new SimpleDateFormat(photoNameScheme)
				.format(new Date());
		String imageFileName = timeStamp + jpgSuffix;
		File imageFile = new File(dirKeeper.getExtFilesDir(), imageFileName);
		return imageFile;
	}

	@Trace
	public Bitmap getThumbnail(String filePath) {
		Bitmap scaledImage = ImageOperations.scaleKeepRatio(
				getBitmap(filePath), 256, Integer.MAX_VALUE);
		return scaledImage;
	}

}
