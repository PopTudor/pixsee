package com.marked.pixsee.face;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.DisplayMetrics;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.getExternalStorageState;

/**
 * Created by Tudor on 4/14/2016.
 */
public class Utils {
	static File getPublicPicturesPixseeDir() {
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

	static Bitmap combineImages(@NotNull Bitmap c, @NotNull Bitmap s) {
		Bitmap overlay = Bitmap.createBitmap(c.getWidth(), c.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas result = new Canvas(overlay);

		result.drawBitmap(c, 0f, 0f, null);
		result.drawBitmap(s, 0, 0f, null);
		return overlay;
	}

	/* Checks if external storage is available for read and write */
	static boolean isExternalStorageWritable() {
		String state = getExternalStorageState();
		if (MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static void saveBitmapToFile(Bitmap screenshot, String filename) {
		try {
			File file = getPublicPicturesPixseeDir();
			FileOutputStream out = new FileOutputStream(file.getPath() + filename);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
			screenshot.compress(Bitmap.CompressFormat.PNG, 100, out);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
