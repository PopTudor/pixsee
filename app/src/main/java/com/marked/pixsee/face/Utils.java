package com.marked.pixsee.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.getExternalStorageState;

/**
 * Created by Tudor on 4/14/2016.
 */
public class Utils {
	static File getPublicPicturesPixseeDir(String filename) {
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/Pixsee/");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Camera Guide", "Required media storage does not exist");
				return null;
			}
		}

		// Create a media file name
		return new File(mediaStorageDir.getPath() + filename);
	}

	static byte[] flip(byte[] src) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(src, 0, src.length);
		Matrix m = new Matrix();
		m.preScale(-1, 1);
		Bitmap dst = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
//		dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);

		ByteArrayOutputStream blob = new ByteArrayOutputStream();
		dst.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, blob);
		bitmap.recycle();
		return blob.toByteArray();
	}

	public static Bitmap combineImages(@NotNull Bitmap c, @NotNull Bitmap s) {
		Bitmap overlay = Bitmap.createScaledBitmap(c, c.getWidth(), c.getHeight(), false);
		Canvas result = new Canvas(overlay);
		Bitmap s1 = Bitmap.createScaledBitmap(s, s.getWidth(), s.getHeight(), false);
		result.drawBitmap(s1, 0f, 0f, null);
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

	public static File saveFile(Bitmap screenshot, Bitmap.CompressFormat format, int quality, String filename) {
		File file = getPublicPicturesPixseeDir("/"+filename);
		try {
			screenshot.compress(format, quality, new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

}
