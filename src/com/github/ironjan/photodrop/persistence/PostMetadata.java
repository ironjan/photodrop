package com.github.ironjan.photodrop.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import com.dropbox.sync.android.DbxFile;

public class PostMetadata {
	private static final String TAG = PostMetadata.class.getSimpleName();

	public final String comment;
	public final Double latitude;
	public final Double longitude;

	@SuppressWarnings("nls")
	private static final String latitudeKey = "latitude:",
			longitudeKey = "longitude:", commentKey = "comment:";

	public PostMetadata(Location location, String comment) {
		if (location != null) {
			this.latitude = Double.valueOf(location.getLatitude());
			this.longitude = Double.valueOf(location.getLongitude());
		} else {
			this.latitude = null;
			this.longitude = null;
		}
		this.comment = comment;
	}

	public PostMetadata(Double latitude, Double longitude, String comment) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.comment = comment;
	}

	@SuppressLint("DefaultLocale")
	public String toFileContent() {
		return String
				.format(Locale.US,
						"%s%f\n%s%f\n%s%s", latitudeKey, latitude, longitudeKey, longitude, commentKey, comment); //$NON-NLS-1$
	}

	public static PostMetadata fromFile(File f) {
		try {
			final FileReader fileReader = new FileReader(f);
			Log.v(TAG,
					String.format("Reading metadata from file %s", f.getPath())); //$NON-NLS-1$
			BufferedReader br = new BufferedReader(fileReader);

			final String latitudeString = br.readLine().substring(
					latitudeKey.length());
			final String longitudeString = br.readLine().substring(
					longitudeKey.length());
			final String commentString = br.readLine().substring(
					commentKey.length());
			br.close();

			Double tLatitude = (latitudeString == null || latitudeString
					.equals("null")) ? null : Double.valueOf( //$NON-NLS-1$
					Double.parseDouble(latitudeString));
			Double tLongitude = (longitudeString == null || longitudeString
					.equals("null")) ? null : Double.valueOf(Double //$NON-NLS-1$
					.parseDouble(longitudeString));

			String tComment = commentString;

			return new PostMetadata(tLatitude, tLongitude, tComment);
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public static PostMetadata fromDropboxFile(DbxFile metaFile)
			throws IOException {
		String fileContent = metaFile.readString();

		String[] splittedContents = fileContent.split("\n"); //$NON-NLS-1$
		
		final String latitudeString = splittedContents[0].substring(
				latitudeKey.length());
		final String longitudeString = splittedContents[1].substring(
				longitudeKey.length());
		final String commentString = splittedContents[2].substring(
				commentKey.length());
		
		Double tLatitude = (latitudeString == null || latitudeString
				.equals("null")) ? null : Double.valueOf( //$NON-NLS-1$
				Double.parseDouble(latitudeString));
		Double tLongitude = (longitudeString == null || longitudeString
				.equals("null")) ? null : Double.valueOf(Double //$NON-NLS-1$
				.parseDouble(longitudeString));

		String tComment = commentString;

		return new PostMetadata(tLatitude, tLongitude, tComment);
	}

}
