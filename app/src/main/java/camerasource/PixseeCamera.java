package camerasource;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.Frame;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tudor on 18-Aug-16.
 */

public abstract class PixseeCamera {
	@SuppressLint("InlinedApi")
	protected static final int CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
	@SuppressLint("InlinedApi")
	protected static final int CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
	/**
	 * If the absolute difference between a preview size aspect ratio and a picture size aspect
	 * ratio is less than this tolerance, they are considered to be the same aspect ratio.
	 */
	protected static final float ASPECT_RATIO_TOLERANCE = 0.01f;
	private static final String TAG = "pixseeCamera";
	protected final Object mCameraLock = new Object();
	protected Context mContext;
	// Guarded by mCameraLock
	protected Camera mCamera;
	protected int mFacing = CAMERA_FACING_FRONT;
	protected Size mPreviewSize;
	// These values may be requested by the caller.  Due to hardware limitations, we may need to
	// select close, but not exactly the same values for these.
	protected float mRequestedFps = 30.0f;
	protected int mRequestedPreviewWidth = 1024;
	protected int mRequestedPreviewHeight = 768;
	protected String mFocusMode = null;
	protected String mFlashMode = null;
	/**
	 * Rotation of the device, and thus the associated preview images captured from the device.
	 * See {@link Frame.Metadata#getRotation()}.
	 */
	protected int mRotation;
	/**
	 * Map to convert between a byte array, received from the camera, and its associated byte
	 * buffer.  We use byte buffers internally because this is a more efficient way to call into
	 * native code later (avoids a potential copy).
	 */
	protected Map<byte[], ByteBuffer> mBytesToByteBuffer = new HashMap<>();

	public PixseeCamera(Context context) {
		mContext = context;
	}

	/**
	 * Gets the id for the camera specified by the direction it is facing.  Returns -1 if no such
	 * camera was found.
	 *
	 * @param facing the desired camera (front-facing or rear-facing)
	 */
	protected static int getIdForRequestedCamera(int facing) {
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == facing) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Selects the most suitable preview and picture size, given the desired width and height.
	 * <p/>
	 * Even though we may only need the preview size, it's necessary to find both the preview
	 * size and the picture size of the camera together, because these need to have the same aspect
	 * ratio.  On some hardware, if you would only set the preview size, you will get a distorted
	 * image.
	 *
	 * @param camera        the camera to select a preview size from
	 * @param desiredWidth  the desired width of the camera preview frames
	 * @param desiredHeight the desired height of the camera preview frames
	 * @return the selected preview and picture size pair
	 */
	protected static CameraSource.SizePair selectSizePair(Camera camera, int desiredWidth, int desiredHeight) {
		List<CameraSource.SizePair> validPreviewSizes = generateValidPreviewSizeList(camera);

		// The method for selecting the best size is to minimize the sum of the differences between
		// the desired values and the actual values for width and height.  This is certainly not the
		// only way to select the best size, but it provides a decent tradeoff between using the
		// closest aspect ratio vs. using the closest pixel area.
		CameraSource.SizePair selectedPair = null;
		int minDiff = Integer.MAX_VALUE;
		for (CameraSource.SizePair sizePair : validPreviewSizes) {
			Size size = sizePair.previewSize();
			int diff = Math.abs(size.getWidth() - desiredWidth) +
					           Math.abs(size.getHeight() - desiredHeight);
			if (diff < minDiff) {
				selectedPair = sizePair;
				minDiff = diff;
			}
		}

		return selectedPair;
	}

	/**
	 * Generates a list of acceptable preview sizes.  Preview sizes are not acceptable if there is
	 * not a corresponding picture size of the same aspect ratio.  If there is a corresponding
	 * picture size of the same aspect ratio, the picture size is paired up with the preview size.
	 * <p/>
	 * This is necessary because even if we don't use still pictures, the still picture size must be
	 * set to a size that is the same aspect ratio as the preview size we choose.  Otherwise, the
	 * preview images may be distorted on some devices.
	 */
	private static List<SizePair> generateValidPreviewSizeList(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		List<Camera.Size> supportedPreviewSizes =
				parameters.getSupportedPreviewSizes();
		List<Camera.Size> supportedPictureSizes =
				parameters.getSupportedPictureSizes();
		List<SizePair> validPreviewSizes = new ArrayList<>();
		for (Camera.Size previewSize : supportedPreviewSizes) {
			float previewAspectRatio = (float) previewSize.width / (float) previewSize.height;

			// By looping through the picture sizes in order, we favor the higher resolutions.
			// We choose the highest resolution in order to support taking the full resolution
			// picture later.
			for (Camera.Size pictureSize : supportedPictureSizes) {
				float pictureAspectRatio = (float) pictureSize.width / (float) pictureSize.height;
				if (Math.abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
					validPreviewSizes.add(new SizePair(previewSize, pictureSize));
					break;
				}
			}
		}

		// If there are no picture sizes with the same aspect ratio as any preview sizes, allow all
		// of the preview sizes and hope that the camera can handle it.  Probably unlikely, but we
		// still account for it.
		if (validPreviewSizes.size() == 0) {
			Log.w(TAG, "No preview sizes have a corresponding same-aspect-ratio picture size");
			for (Camera.Size previewSize : supportedPreviewSizes) {
				// The null picture size will let us know that we shouldn't set a picture size.
				validPreviewSizes.add(new SizePair(previewSize, null));
			}
		}

		return validPreviewSizes;
	}

	//==============================================================================================
	// Bridge Functionality for the Camera1 API
	//==============================================================================================
	public PixseeCamera start(SurfaceTexture surfaceTexture) throws IOException, RuntimeException {
		if (mCamera != null) {
			return this;
		}
		mCamera = createCamera();

		// SurfaceTexture was introduced in Honeycomb (11), so if we are running and
		// old version of Android. fall back to use SurfaceView.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mCamera.setPreviewTexture(surfaceTexture);
		} else {
			SurfaceView mDummySurfaceView = new SurfaceView(mContext);
			mCamera.setPreviewDisplay(mDummySurfaceView.getHolder());
			Toast.makeText(mContext, "Please close other camera apps, or update the system.", Toast.LENGTH_LONG);
			Log.d(TAG, "start: " + "Screen white ? Checkout CameraSource.start" +
					           "(surfaceTexture) because the camera is not sending frames to " +
					           "Surface texture but to a dummy SurfaceView");
		}
		mCamera.startPreview();
		return this;
	}

	/**
	 * Opens the camera and applies the user settings.
	 *
	 * @throws RuntimeException if the method fails
	 */
	@SuppressLint("InlinedApi")
	protected Camera createCamera() throws RuntimeException {
		int requestedCameraId = getIdForRequestedCamera(mFacing);
		if (requestedCameraId == -1) {
			throw new RuntimeException("Could not find requested camera.");
		}

		Camera camera = Camera.open(requestedCameraId);
		SizePair sizePair = selectSizePair(camera, mRequestedPreviewWidth, mRequestedPreviewHeight);
		if (sizePair == null) {
			throw new RuntimeException("Could not find suitable preview size.");
		}
		Size pictureSize = sizePair.pictureSize();
		mPreviewSize = sizePair.previewSize();

		int[] previewFpsRange = selectPreviewFpsRange(camera, mRequestedFps);
		if (previewFpsRange == null) {
			throw new RuntimeException("Could not find suitable preview frames per second range.");
		}

		Camera.Parameters parameters = camera.getParameters();

		if (pictureSize != null) {
			parameters.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
		}

		parameters.setPreviewSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
		parameters.setPreviewFpsRange(
				previewFpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
				previewFpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
		parameters.setPreviewFormat(ImageFormat.NV21);

		setRotation(camera, parameters, requestedCameraId);

		if (mFocusMode != null) {
			if (parameters.getSupportedFocusModes().contains(
					mFocusMode)) {
				parameters.setFocusMode(mFocusMode);
			} else {
				Log.i(TAG, "Camera focus mode: " + mFocusMode + " is not supported on this device.");
			}
		}

		// setting mFocusMode to the one set in the params
		mFocusMode = parameters.getFocusMode();

		if (mFlashMode != null) {
			if (parameters.getSupportedFlashModes().contains(
					mFlashMode)) {
				parameters.setFlashMode(mFlashMode);
			} else {
				Log.i(TAG, "Camera flash mode: " + mFlashMode + " is not supported on this device.");
			}
		}

		// setting mFlashMode to the one set in the params
		mFlashMode = parameters.getFlashMode();

		camera.setParameters(parameters);

		// Four frame buffers are needed for working with the camera:
		//
		//   one for the frame that is currently being executed upon in doing detection
		//   one for the next pending frame to process immediately upon completing detection
		//   two for the frames that the camera uses to populate future preview images
		camera.setPreviewCallbackWithBuffer(createPreviewCallback());
		camera.addCallbackBuffer(createPreviewBuffer(mPreviewSize));
		camera.addCallbackBuffer(createPreviewBuffer(mPreviewSize));
		camera.addCallbackBuffer(createPreviewBuffer(mPreviewSize));
		camera.addCallbackBuffer(createPreviewBuffer(mPreviewSize));

		return camera;
	}

	protected abstract Camera.PreviewCallback createPreviewCallback();

	/**
	 * Creates one buffer for the camera preview callback.  The size of the buffer is based off of
	 * the camera preview size and the format of the camera image.
	 *
	 * @return a new preview buffer of the appropriate size for the current camera settings
	 */
	private byte[] createPreviewBuffer(Size previewSize) {
		int bitsPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.NV21);
		long sizeInBits = previewSize.getHeight() * previewSize.getWidth() * bitsPerPixel;
		int bufferSize = (int) Math.ceil(sizeInBits / 8.0d) + 1;

		//
		// NOTICE: This code only works when using play services v. 8.1 or higher.
		//

		// Creating the byte array this way and wrapping it, as opposed to using .allocate(),
		// should guarantee that there will be an array to work with.
		byte[] byteArray = new byte[bufferSize];
		ByteBuffer buffer = ByteBuffer.wrap(byteArray);
		if (!buffer.hasArray() || (buffer.array() != byteArray)) {
			// I don't think that this will ever happen.  But if it does, then we wouldn't be
			// passing the preview content to the underlying detector later.
			throw new IllegalStateException("Failed to create valid buffer for camera source.");
		}

		mBytesToByteBuffer.put(byteArray, buffer);
		return byteArray;
	}

	/**
	 * Returns the selected camera; one of {@link #CAMERA_FACING_BACK} or
	 * {@link #CAMERA_FACING_FRONT}.
	 */
	public int getCameraFacing() {
		return mFacing;
	}

	public int doZoom(float scale) {
		synchronized (mCameraLock) {
			if (mCamera == null) {
				return 0;
			}
			int currentZoom = 0;
			int maxZoom;
			Camera.Parameters parameters = mCamera.getParameters();
			if (!parameters.isZoomSupported()) {
				Log.w(TAG, "Zoom is not supported on this device");
				return currentZoom;
			}
			maxZoom = parameters.getMaxZoom();

			currentZoom = parameters.getZoom() + 1;
			float newZoom;
			if (scale > 1) {
				newZoom = currentZoom + scale * (maxZoom / 10);
			} else {
				newZoom = currentZoom * scale;
			}
			currentZoom = Math.round(newZoom) - 1;
			if (currentZoom < 0) {
				currentZoom = 0;
			} else if (currentZoom > maxZoom) {
				currentZoom = maxZoom;
			}
			parameters.setZoom(currentZoom);
			mCamera.setParameters(parameters);
			return currentZoom;
		}
	}

	/**
	 * Initiates taking a picture, which happens asynchronously.  The camera source should have been
	 * activated previously with {@link #start(SurfaceTexture)} .  The camera
	 * preview is suspended while the picture is being taken, but will resume once picture taking is
	 * done.
	 *
	 * @param shutter the callback for image capture moment, or null
	 * @param jpeg    the callback for JPEG image data, or null
	 */
	public void takePicture(ShutterCallback shutter, PictureCallback jpeg) {
		synchronized (mCameraLock) {
			if (mCamera != null) {
				PictureStartCallback startCallback = new PictureStartCallback(shutter);
				PictureDoneCallback doneCallback = new PictureDoneCallback(jpeg);
				mCamera.takePicture(startCallback, null, null, doneCallback);
			}
		}
	}

	/**
	 * Gets the current focus mode setting.
	 *
	 * @return current focus mode. This value is null if the camera is not yet created. Applications should call {@link
	 * #autoFocus(AutoFocusCallback)} to start the focus if focus
	 * mode is FOCUS_MODE_AUTO or FOCUS_MODE_MACRO.
	 * @see Camera.Parameters#FOCUS_MODE_AUTO
	 * @see Camera.Parameters#FOCUS_MODE_INFINITY
	 * @see Camera.Parameters#FOCUS_MODE_MACRO
	 * @see Camera.Parameters#FOCUS_MODE_FIXED
	 * @see Camera.Parameters#FOCUS_MODE_EDOF
	 * @see Camera.Parameters#FOCUS_MODE_CONTINUOUS_VIDEO
	 * @see Camera.Parameters#FOCUS_MODE_CONTINUOUS_PICTURE
	 */
	@Nullable
	@CameraSource.FocusMode
	public String getFocusMode() {
		return mFocusMode;
	}

	/**
	 * Sets the focus mode.
	 *
	 * @param mode the focus mode
	 * @return {@code true} if the focus mode is set, {@code false} otherwise
	 * @see #getFocusMode()
	 */
	public boolean setFocusMode(@FocusMode String mode) {
		synchronized (mCameraLock) {
			if (mCamera != null && mode != null) {
				Camera.Parameters parameters = mCamera.getParameters();
				if (parameters.getSupportedFocusModes().contains(mode)) {
					parameters.setFocusMode(mode);
					mCamera.setParameters(parameters);
					mFocusMode = mode;
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * Gets the current flash mode setting.
	 *
	 * @return current flash mode. null if flash mode setting is not
	 * supported or the camera is not yet created.
	 * @see Camera.Parameters#FLASH_MODE_OFF
	 * @see Camera.Parameters#FLASH_MODE_AUTO
	 * @see Camera.Parameters#FLASH_MODE_ON
	 * @see Camera.Parameters#FLASH_MODE_RED_EYE
	 * @see Camera.Parameters#FLASH_MODE_TORCH
	 */
	@Nullable
	@FlashMode
	public String getFlashMode() {
		return mFlashMode;
	}

	/**
	 * Sets the flash mode.
	 *
	 * @param mode flash mode.
	 * @return {@code true} if the flash mode is set, {@code false} otherwise
	 * @see #getFlashMode()
	 */
	public boolean setFlashMode(@FlashMode String mode) {
		synchronized (mCameraLock) {
			if (mCamera != null && mode != null) {
				Camera.Parameters parameters = mCamera.getParameters();
				if (parameters.getSupportedFlashModes().contains(mode)) {
					parameters.setFlashMode(mode);
					mCamera.setParameters(parameters);
					mFlashMode = mode;
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * Starts camera auto-focus and registers a callback function to run when
	 * the camera is focused.  This method is only valid when preview is active
	 * (between {@link #start(SurfaceTexture)} and before {@link #stop()} or {@link #release()}).
	 * <p/>
	 * <p>Callers should check
	 * {@link #getFocusMode()} to determine if
	 * this method should be called. If the camera does not support auto-focus,
	 * it is a no-op and {@link AutoFocusCallback#onAutoFocus(boolean)}
	 * callback will be called immediately.
	 * <p/>
	 * <p>If the current flash mode is not
	 * {@link Camera.Parameters#FLASH_MODE_OFF}, flash may be
	 * fired during auto-focus, depending on the driver and camera hardware.<p>
	 *
	 * @param cb the callback to run
	 * @see #cancelAutoFocus()
	 */
	public void autoFocus(@Nullable AutoFocusCallback cb) {
		synchronized (mCameraLock) {
			if (mCamera != null) {
				CameraAutoFocusCallback autoFocusCallback = null;
				if (cb != null) {
					autoFocusCallback = new CameraAutoFocusCallback(cb);
				}
				mCamera.autoFocus(autoFocusCallback);
			}
		}
	}

	/**
	 * Cancels any auto-focus function in progress.
	 * Whether or not auto-focus is currently in progress,
	 * this function will return the focus position to the default.
	 * If the camera does not support auto-focus, this is a no-op.
	 *
	 * @see #autoFocus(AutoFocusCallback)
	 */
	public void cancelAutoFocus() {
		synchronized (mCameraLock) {
			if (mCamera != null) {
				mCamera.cancelAutoFocus();
			}
		}
	}

	/**
	 * Sets camera auto-focus move callback.
	 *
	 * @param cb the callback to run
	 * @return {@code true} if the operation is supported (i.e. from Jelly Bean), {@code false} otherwise
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public boolean setAutoFocusMoveCallback(@Nullable AutoFocusMoveCallback cb) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			return false;
		}

		synchronized (mCameraLock) {
			if (mCamera != null) {
				CameraAutoFocusMoveCallback autoFocusMoveCallback = null;
				if (cb != null) {
					autoFocusMoveCallback = new CameraAutoFocusMoveCallback(cb);
				}
				mCamera.setAutoFocusMoveCallback(autoFocusMoveCallback);
			}
		}

		return true;
	}

	/**
	 * Stops the camera and releases the resources of the camera and underlying detector.
	 */
	public void release() {
		synchronized (mCameraLock) {
			stop();
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
		}
	}

	/**
	 * Closes the camera
	 * <p/>
	 * This camera source may be restarted again by calling
	 * {@link #start(SurfaceTexture surfaceTexture)} or
	 * <p/>
	 * Call {@link #release()} instead to completely shut down this camera source and release the
	 * resources of the underlying detector.
	 */
	public void stop() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallbackWithBuffer(null);
			try {
				// We want to be compatible back to Gingerbread, but SurfaceTexture
				// wasn't introduced until Honeycomb.  Since the interface cannot use a SurfaceTexture, if the
				// developer wants to display a preview we must use a SurfaceHolder.  If the developer doesn't
				// want to display a preview we use a SurfaceTexture if we are running at least Honeycomb.

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					mCamera.setPreviewTexture(null);

				} else {
					mCamera.setPreviewDisplay(null);
				}
			} catch (Exception e) {
				Log.e(TAG, "Failed to clear camera preview: " + e);
			}

			mCamera.release();
			mCamera = null;
		}
	}

	public Camera getCamera() {
		return mCamera;
	}

	/**
	 * Selects the most suitable preview frames per second range, given the desired frames per
	 * second.
	 *
	 * @param camera            the camera to select a frames per second range from
	 * @param desiredPreviewFps the desired frames per second for the camera preview frames
	 * @return the selected preview frames per second range
	 */
	private int[] selectPreviewFpsRange(Camera camera, float desiredPreviewFps) {
		// The camera API uses integers scaled by a factor of 1000 instead of floating-point frame
		// rates.
		int desiredPreviewFpsScaled = (int) (desiredPreviewFps * 1000.0f);

		// The method for selecting the best range is to minimize the sum of the differences between
		// the desired value and the upper and lower bounds of the range.  This may select a range
		// that the desired value is outside of, but this is often preferred.  For example, if the
		// desired frame rate is 29.97, the range (30, 30) is probably more desirable than the
		// range (15, 30).
		int[] selectedFpsRange = null;
		int minDiff = Integer.MAX_VALUE;
		List<int[]> previewFpsRangeList = camera.getParameters().getSupportedPreviewFpsRange();
		for (int[] range : previewFpsRangeList) {
			int deltaMin = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
			int deltaMax = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
			int diff = Math.abs(deltaMin) + Math.abs(deltaMax);
			if (diff < minDiff) {
				selectedFpsRange = range;
				minDiff = diff;
			}
		}
		return selectedFpsRange;
	}

	/**
	 * Calculates the correct rotation for the given camera id and sets the rotation in the
	 * parameters.  It also sets the camera's display orientation and rotation.
	 *
	 * @param parameters the camera parameters for which to set the rotation
	 * @param cameraId   the camera id to set rotation based on
	 */
	private void setRotation(Camera camera, Camera.Parameters parameters, int cameraId) {
		WindowManager windowManager =
				(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		int degrees = 0;
		int rotation = windowManager.getDefaultDisplay().getRotation();
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			default:
				Log.e(TAG, "Bad rotation value: " + rotation);
		}

		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, cameraInfo);

		int angle;
		int displayAngle;
		if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			angle = (cameraInfo.orientation + degrees) % 360;
			displayAngle = (360 - angle); // compensate for it being mirrored
		} else {  // back-facing
			angle = (cameraInfo.orientation - degrees + 360) % 360;
			displayAngle = angle;
		}

		// This corresponds to the rotation constants in {@link Frame}.
		mRotation = angle / 90;

		camera.setDisplayOrientation(displayAngle);
		parameters.setRotation(angle);
	}

	/**
	 * Returns the preview size that is currently in use by the underlying camera.
	 */
	public Size getPreviewSize() {
		return mPreviewSize;
	}

	@StringDef({
			           Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
			           Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
			           Camera.Parameters.FOCUS_MODE_AUTO,
			           Camera.Parameters.FOCUS_MODE_EDOF,
			           Camera.Parameters.FOCUS_MODE_FIXED,
			           Camera.Parameters.FOCUS_MODE_INFINITY,
			           Camera.Parameters.FOCUS_MODE_MACRO
	})
	@Retention(RetentionPolicy.SOURCE)
	protected @interface FocusMode {
	}

	@StringDef({
			           Camera.Parameters.FLASH_MODE_ON,
			           Camera.Parameters.FLASH_MODE_OFF,
			           Camera.Parameters.FLASH_MODE_AUTO,
			           Camera.Parameters.FLASH_MODE_RED_EYE,
			           Camera.Parameters.FLASH_MODE_TORCH
	})
	@Retention(RetentionPolicy.SOURCE)
	protected @interface FlashMode {
	}

	/**
	 * Stores a preview size and a corresponding same-aspect-ratio picture size.  To avoid distorted
	 * preview images on some devices, the picture size must be set to a size that is the same
	 * aspect ratio as the preview size or the preview may end up being distorted.  If the picture
	 * size is null, then there is no picture size with the same aspect ratio as the preview size.
	 */
	protected static class SizePair {
		private Size mPreview;
		private Size mPicture;

		public SizePair(Camera.Size previewSize,
		                Camera.Size pictureSize) {
			mPreview = new Size(previewSize.width, previewSize.height);
			if (pictureSize != null) {
				mPicture = new Size(pictureSize.width, pictureSize.height);
			}
		}

		public Size previewSize() {
			return mPreview;
		}

		@SuppressWarnings("unused")
		public Size pictureSize() {
			return mPicture;
		}
	}

	/**
	 * Wraps the camera1 shutter callback so that the deprecated API isn't exposed.
	 */
	private class PictureStartCallback implements Camera.ShutterCallback {
		private ShutterCallback mDelegate;

		PictureStartCallback(ShutterCallback delegate) {
			mDelegate = delegate;
		}

		@Override
		public void onShutter() {
			if (mDelegate != null) {
				mDelegate.onShutter();
			}
		}
	}

	/**
	 * Wraps the final callback in the camera sequence, so that we can automatically turn the camera
	 * preview back on after the picture has been taken.
	 */
	private class PictureDoneCallback implements Camera.PictureCallback {
		private PictureCallback mDelegate;

		PictureDoneCallback(PictureCallback delegate) {
			mDelegate = delegate;
		}

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if (mDelegate != null) {
				mDelegate.onPictureTaken(data);
			}
			synchronized (mCameraLock) {
				if (mCamera != null) {
					mCamera.startPreview();
				}
			}
		}
	}

	/**
	 * Wraps the camera1 auto focus callback so that the deprecated API isn't exposed.
	 */
	private class CameraAutoFocusCallback implements Camera.AutoFocusCallback {
		private AutoFocusCallback mDelegate;

		CameraAutoFocusCallback(AutoFocusCallback autoFocusCallback) {
			mDelegate = autoFocusCallback;
		}

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (mDelegate != null) {
				mDelegate.onAutoFocus(success);
			}
		}
	}

	/**
	 * Wraps the camera1 auto focus move callback so that the deprecated API isn't exposed.
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private class CameraAutoFocusMoveCallback implements Camera.AutoFocusMoveCallback {
		private AutoFocusMoveCallback mDelegate;

		CameraAutoFocusMoveCallback(AutoFocusMoveCallback autoFocusMoveCallback) {
			mDelegate = autoFocusMoveCallback;
		}

		@Override
		public void onAutoFocusMoving(boolean start, Camera camera) {
			if (mDelegate != null) {
				mDelegate.onAutoFocusMoving(start);
			}
		}
	}
}
