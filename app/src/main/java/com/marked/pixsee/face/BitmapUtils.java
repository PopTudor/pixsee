package com.marked.pixsee.face;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.getExternalStorageState;

/**
 * Created by Tudor on 4/14/2016.
 */
public class BitmapUtils {
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

	static byte[] flipHorizontal(byte[] src) {
		Bitmap bitmap = getBitmapFromBytes(src);
		Matrix m = new Matrix();
		m.preScale(-1, 1);
		Bitmap dst = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
		return getBytesFromBitmap(dst);
	}

	public static Bitmap getBitmapFromView(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		view.layout(view.getLeft(), view.getTop(), view.getRight(),
				view.getBottom());
		view.draw(canvas);

		return bitmap;
	}

	public static Bitmap getBitmapFromResource(Resources res, int resId,
	                                           int reqWidth, int reqHeight) {
		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inJustDecodeBounds = true;
		// BitmapFactory.decodeResource(res, resId, options);
		// options = BitmapHelper.calculateInSampleSize(options, reqWidth,
		// reqHeight);
		// return BitmapFactory.decodeResource(res, resId, options);

		// 通过JNI的形式读取本地图片达到节省内存的目的
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		InputStream is = res.openRawResource(resId);
		return getBitmapFromStream(is, null, reqWidth, reqHeight);
	}

	public static Bitmap getBitmapFromStream(InputStream is, Rect outPadding,
	                                         int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, outPadding, options);
		options = calculateInSampleSize(options, reqWidth, reqHeight);
		return BitmapFactory.decodeStream(is, outPadding, options);
	}

	public static Bitmap getBitmapFromFile(String pathName, int reqWidth,
	                                       int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		options = calculateInSampleSize(options, reqWidth, reqHeight);
		return BitmapFactory.decodeFile(pathName, options);
	}

	public static BitmapFactory.Options calculateInSampleSize(
			                                                         final BitmapFactory.Options options, final int reqWidth,
			                                                         final int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > 400 || width > 450) {
			if (height > reqHeight || width > reqWidth) {
				// 计算出实际宽高和目标宽高的比率
				final int heightRatio = Math.round((float) height
						                                   / (float) reqHeight);
				final int widthRatio = Math.round((float) width
						                                  / (float) reqWidth);
				// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
				// 一定都会大于等于目标的宽和高。
				inSampleSize = heightRatio < widthRatio ? heightRatio
						               : widthRatio;
			}
		}
		// 设置压缩比例
		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
		return options;
	}

	public static Bitmap combineImages(Bitmap bgd, Bitmap fg) {
		Bitmap bmp;

		int width = bgd.getWidth() > fg.getWidth() ? bgd.getWidth() : fg.getWidth();
		int height = bgd.getHeight() > fg.getHeight() ? bgd.getHeight() : fg.getHeight();

		bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

		Canvas canvas = new Canvas(bmp);
		canvas.drawBitmap(bgd, 0, 0, null);
		canvas.drawBitmap(fg, 0, 0, paint);

		return bmp;
	}

	public static Bitmap getBitmapFromByteArray(byte[] data, int offset,
	                                            int length, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, offset, length, options);
		options = calculateInSampleSize(options, reqWidth, reqHeight);
		return BitmapFactory.decodeByteArray(data, offset, length, options);
	}

	public static byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static byte[] getBytesFromStream(InputStream inputStream) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return os.toByteArray();
	}

	public static Bitmap getBitmapFromDrawable(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				                                                   .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				                                                   : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	public static Bitmap getBitmapFromBytes(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}


	/* Checks if external storage is available for read and write */
	static boolean isExternalStorageWritable() {
		String state = getExternalStorageState();
		if (MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
	/**
	 * Saves the given bitmap to public picture directory
	 *
	 * @param screenshot the bitmap to save
	 * @param format     the format to save the image {@link android.graphics.Bitmap.CompressFormat} (JPEG, PNG)
	 * @param quality    quality of the image (PNG is loseless so this is ignored)
	 * @param filename   the name of the saved image
	 * @return the path of the image
	 */
	public static File saveFile(Bitmap screenshot, Bitmap.CompressFormat format, int quality, String filename) {
		File file = getPublicPicturesPixseeDir("/" + filename);
		try {
			screenshot.compress(format, quality, new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

}
