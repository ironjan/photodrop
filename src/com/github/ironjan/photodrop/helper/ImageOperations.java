package com.github.ironjan.photodrop.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageOperations {
	public static Bitmap scaleKeepRatio(Bitmap bm, int maxWidth, int maxHeigth) {
		if (bm == null) {
			return null;
		}

		double width = bm.getWidth();
		double height = bm.getHeight();

		final double scale = calcScalingFactor(maxWidth, maxHeigth, width,
				height);

		int scaledWidth = (int) (scale * width);
		int scaledHeight = (int) (scale * height);

		Bitmap scaledBm = Bitmap.createScaledBitmap(bm, scaledWidth,
				scaledHeight, true);

		return scaledBm;
	}

	public static double calcScalingFactor(int maxWidth, int maxHeigth,
			double width, double height) {
		final double scaleW = maxWidth / width;
		final double scaleH = maxHeigth / height;

		final double scale = Math.min(scaleW, scaleH);
		return scale;
	}

	protected static Bitmap scale(Bitmap bm, int width, int heigth) {
		return Bitmap.createScaledBitmap(bm, width, heigth, true);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		while (height / inSampleSize > reqHeight
				|| width / inSampleSize > reqWidth) {
			inSampleSize *= 2;
		}

		return inSampleSize;
	}

	
	public static Bitmap loadScaledImage(final String path, final int reqWidth, final int reqHeigth) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path,opt);

		opt.inSampleSize = ImageOperations.calculateInSampleSize(opt, reqWidth, reqHeigth);
		opt.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, opt);
	}
}
