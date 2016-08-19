package com.marked.pixsee.selfie.camerasource;


import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Utility class for manipulating images.
 **/
public class ImageUtils {
	private static final String TAG = ImageUtils.class.getSimpleName();

	/**
	 * Utility method to compute the allocated size in bytes of a YUV420SP image
	 * of the given dimensions.
	 */
	public static int getYUVByteSize(final int width, final int height) {
		// The luminance plane requires 1 byte per pixel.
		final int ySize = width * height;

		// The UV plane works on 2x2 blocks, so dimensions with odd size must be rounded up.
		// Each 2x2 block takes 2 bytes to encode, one each for U and V.
		final int uvSize = ((width + 1) / 2) * ((height + 1) / 2) * 2;

		return ySize + uvSize;
	}

	/**
	 * Saves a Bitmap object to disk for analysis.
	 *
	 * @param bitmap The bitmap to save.
	 */
	public static void saveBitmap(final Bitmap bitmap) {
		final String root =
				Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dlib";
		Log.i(TAG, String.format("Saving %dx%d bitmap to %s.", bitmap.getWidth(), bitmap.getHeight(), root));
		final File myDir = new File(root);

		if (!myDir.mkdirs()) {
			Log.i(TAG, "Make dir failed");
		}

		final String fname = "preview.png";
		final File file = new File(myDir, fname);
		if (file.exists()) {
			file.delete();
		}
		try {
			final FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 99, out);
			out.flush();
			out.close();
		} catch (final Exception e) {
			Log.e(TAG, "Exception!", e);
		}
	}

	/**
	 * Converts YUV420 NV21 to ARGB8888
	 *
	 * @param data   byte array on YUV420 NV21 format.
	 * @param width  pixels width
	 * @param height pixels height
	 * @return a ARGB8888 pixels int array. Where each int is a pixels ARGB.
	 */
	public static int[] convertYUV420_NV21toARGB8888(byte[] data, int width, int height) {
		int size = width * height;
		int offset = size;
		int[] pixels = new int[size];
		int u, v, y1, y2, y3, y4;

		// i along Y and the final pixels
		// k along pixels U and V
		for (int i = 0, k = 0; i < size; i += 2, k += 2) {
			y1 = data[i] & 0xff;
			y2 = data[i + 1] & 0xff;
			y3 = data[width + i] & 0xff;
			y4 = data[width + i + 1] & 0xff;

			u = data[offset + k] & 0xff;
			v = data[offset + k + 1] & 0xff;
			u = u - 128;
			v = v - 128;

			pixels[i] = convertYUVtoARGB(y1, u, v);
			pixels[i + 1] = convertYUVtoARGB(y2, u, v);
			pixels[width + i] = convertYUVtoARGB(y3, u, v);
			pixels[width + i + 1] = convertYUVtoARGB(y4, u, v);

			if (i != 0 && (i + 2) % width == 0)
				i += width;
		}

		return pixels;
	}

	private static int convertYUVtoARGB(int y, int u, int v) {
		int r, g, b;

		r = y + (int) 1.402f * u;
		g = y - (int) (0.344f * v + 0.714f * u);
		b = y + (int) 1.772f * v;
		r = r > 255 ? 255 : r < 0 ? 0 : r;
		g = g > 255 ? 255 : g < 0 ? 0 : g;
		b = b > 255 ? 255 : b < 0 ? 0 : b;
		return 0xff000000 | (r<<16) | (g<<8) | b;
	}
	/**
	 * Converts YUV420 semi-planar data to ARGB 8888 data using the supplied width
	 * and height. The input and output must already be allocated and non-null.
	 * For efficiency, no error checking is performed.
	 *
	 * @param input    The array of YUV 4:2:0 input data.
	 * @param output   A pre-allocated array for the ARGB 8:8:8:8 output data.
	 * @param width    The width of the input image.
	 * @param height   The height of the input image.
	 * @param halfSize If true, downsample to 50% in each dimension, otherwise not.
	 */
	public static native void convertYUV420SPToARGB8888(byte[] input, int[] output, int width, int height, boolean halfSize);

	/**
	 * Converts YUV420 semi-planar data to ARGB 8888 data using the supplied width
	 * and height. The input and output must already be allocated and non-null.
	 * For efficiency, no error checking is performed.
	 *
	 * @param y
	 * @param u
	 * @param v
	 * @param uvPixelStride
	 * @param width         The width of the input image.
	 * @param height        The height of the input image.
	 * @param halfSize      If true, downsample to 50% in each dimension, otherwise not.
	 * @param output        A pre-allocated array for the ARGB 8:8:8:8 output data.
	 */
	public static native void convertYUV420ToARGB8888(
			                                                 byte[] y,
			                                                 byte[] u,
			                                                 byte[] v,
			                                                 int[] output,
			                                                 int width,
			                                                 int height,
			                                                 int yRowStride,
			                                                 int uvRowStride,
			                                                 int uvPixelStride,
			                                                 boolean halfSize);

	/**
	 * Converts YUV420 semi-planar data to RGB 565 data using the supplied width
	 * and height. The input and output must already be allocated and non-null.
	 * For efficiency, no error checking is performed.
	 *
	 * @param input  The array of YUV 4:2:0 input data.
	 * @param output A pre-allocated array for the RGB 5:6:5 output data.
	 * @param width  The width of the input image.
	 * @param height The height of the input image.
	 */
	public static native void convertYUV420SPToRGB565(
			                                                 byte[] input, byte[] output, int width, int height);

	/**
	 * Converts 32-bit ARGB8888 image data to YUV420SP data.  This is useful, for
	 * instance, in creating data to feed the classes that rely on raw camera
	 * preview frames.
	 *
	 * @param input  An array of input pixels in ARGB8888 format.
	 * @param output A pre-allocated array for the YUV420SP output data.
	 * @param width  The width of the input image.
	 * @param height The height of the input image.
	 */
	public static native void convertARGB8888ToYUV420SP(
			                                                   int[] input, byte[] output, int width, int height);

	/**
	 * Converts 16-bit RGB565 image data to YUV420SP data.  This is useful, for
	 * instance, in creating data to feed the classes that rely on raw camera
	 * preview frames.
	 *
	 * @param input  An array of input pixels in RGB565 format.
	 * @param output A pre-allocated array for the YUV420SP output data.
	 * @param width  The width of the input image.
	 * @param height The height of the input image.
	 */
	public static native void convertRGB565ToYUV420SP(
			                                                 byte[] input, byte[] output, int width, int height);
}