package com.marked.pixsee.face;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.getExternalStorageState;

/**
 * Created by Tudor on 4/14/2016.
 */
public class Utils {
	static File getPublicPixseeDir() {
		File file = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "Pixsee/");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	static Bitmap flip(Bitmap src) {
		Matrix m = new Matrix();
		m.preScale(-1, 1);
		Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
		dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		return dst;
	}

	static Bitmap combineImages(Bitmap c, Bitmap s) {
		Bitmap cs = null;

		int width, height = 0;

		width = c.getWidth();
		height = c.getHeight();


		cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas comboImage = new Canvas(cs);

		comboImage.drawBitmap(c, 0f, 0f, null);
		comboImage.drawBitmap(s, 0, 0f, null);

		return cs;
	}

	/* Checks if external storage is available for read and write */
	static boolean isExternalStorageWritable() {
		String state = getExternalStorageState();
		if (MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
}
