package com.marked.pixsee.selfie.renderer;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.marked.pixsee.selfie.SelfieFragment.OnFavoritesListener;
import com.marked.pixsee.selfie.data.SelfieObject;

import org.jetbrains.annotations.NotNull;
import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.ALoader;
import org.rajawali3d.loader.AMeshLoader;
import org.rajawali3d.loader.async.IAsyncLoaderCallback;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;

/**
 * Created by Tudor on 4/8/2016.
 * Used to render models onto a @{StreamingTexture} using the render thread
 * The renderer should only be created once we have a running camera
 */
public class SelfieRenderer extends Renderer implements IAsyncLoaderCallback, OnFavoritesListener, SelfieTrackerAR.TrackerCallback {
	private static final String TAG = "***********";
	private static final int CAMERA_Z = 6;
	private final Object mLock = new Object();

	private int mFacing = CameraSource.CAMERA_FACING_FRONT;
	private int mPreviewWidth, mPreviewHeight;

	private float mWidthScaleFactor = 1.0f, mHeightScaleFactor = 1.0f;

	private Object3D loadedObject = null;

	public SelfieRenderer(Context context) {
		super(context);
		setFrameRate(60);
	}

	@Override
	protected void initScene() {
		DirectionalLight directionalLight = new DirectionalLight(0f, 0f, -1.0f);
		directionalLight.setColor(1.0f, 1.0f, 1.0f);
		directionalLight.setPower(2);

		getCurrentScene().addLight(directionalLight);
		getCurrentCamera().setPosition(0, 0, CAMERA_Z);
	}

	@Override
	public void onModelLoadComplete(final ALoader loader) {
		Log.d(TAG, "onModelLoadComplete: ");
		mLoaderExecutor.execute(new Runnable() {
			@Override
			public void run() {
				final AMeshLoader obj = ((AMeshLoader) loader);

				final Object3D parsedObject = obj.getParsedObject();
				parsedObject.setPosition(Vector3.ZERO);

				if (loadedObject != null) {
					getCurrentScene().removeChild(loadedObject);
					loadedObject = null;
				}
				loadedObject = parsedObject;

				getCurrentScene().addChild(loadedObject);
			}
		});
	}

	@Override
	public void onFavoriteClicked(final SelfieObject object) {
		loadModel(object.getLoader(), SelfieRenderer.this, object.getResId());
		if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
			mWidthScaleFactor = (float) mCurrentViewportWidth / (float) mPreviewWidth;
			mHeightScaleFactor = (float) mCurrentViewportHeight / (float) mPreviewHeight;
		}
	}

	@Override
	public void onNewItem(int id, Face face) {
		if (loadedObject != null)
			loadedObject.setVisible(true);
	}

	@Override
	public void onUpdate(Detector.Detections<Face> detections, Face face) {
		if (loadedObject != null && face != null) {
			scale(loadedObject, face);
			rotate(loadedObject, face);
			translation(loadedObject, face);
		}
	}

	@Override
	public void onDone() {
		if (loadedObject != null)
			loadedObject.setVisible(false);
	}

	@Override
	public void onModelLoadFailed(ALoader loader) {
		Log.d(TAG, "onModelLoadFailed: ");
		onDone();
	}
	/**
	 * Scale the object based on face size
	 *
	 * @param object3D the object to scale
	 * @param face     the face to scale upon
	 */
	private void scale(@NotNull Object3D object3D, @NotNull Face face) {
		double dist = Utils.calculateFaceCenter(face);
		double scaleValue = dist / mDefaultViewportWidth; /* convert from pixels to normalized scale*/
		object3D.setScale(scaleValue);
	}

	/**
	 * Rotate the object based on face rotation
	 *
	 * @param object3D the object to rotate
	 * @param face     the face to get the rotation from
	 */
	private void rotate(@NotNull Object3D object3D, @NotNull Face face) {
		float eulerZ = face.getEulerZ();
		object3D.rotateAround(Vector3.Y, eulerZ, false);
	}

	/**
	 * Translate the object based on face location
	 *
	 * @param object the object to translate
	 * @param face   the face to get it's position
	 */
	private void translation(@NotNull Object3D object, @NotNull Face face) {
		float x = translateX(face.getPosition().x + face.getWidth() / 2);
		float y = translateY(face.getPosition().y + face.getHeight() / 2);
		object.setScreenCoordinates(x, y, mCurrentViewportWidth, mCurrentViewportHeight, CAMERA_Z);
	}

	/**
	 * Adjusts a horizontal value of the supplied value from the preview scale to the view
	 * scale.
	 */
	public float scaleX(float horizontal) {
		return horizontal * mWidthScaleFactor;
	}

	/**
	 * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
	 */
	public float scaleY(float vertical) {
		return vertical * mHeightScaleFactor;
	}
	/**
	 * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
	 * system.
	 */
	private float translateY(float y) {
		if (mFacing == CameraSource.CAMERA_FACING_FRONT)
			return mCurrentViewportHeight - scaleY(y);
		else
			return scaleY(y);
	}
	/**
	 * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
	 * system.
	 */
	private float translateX(float x) {
		if (mFacing == CameraSource.CAMERA_FACING_FRONT) {
			return mCurrentViewportWidth - scaleX(x);
		} else {
			return scaleX(x);
		}
	}

	/**
	 * Sets the camera attributes for size and facing direction, which informs how to transform
	 * image coordinates later. If this is not set, image coordinates/loaded objects are not properly
	 * shown on the face. This sets the preview width/height of the rendering surface to be the same
	 * as the camera surface so their sizes will match when overlapped
	 */
	public void setCameraInfo(Size size,int facing){
		int min = Math.min(size.getWidth(), size.getHeight());
		int max = Math.max(size.getWidth(), size.getHeight());
		if (Utils.isPortraitMode(mContext)) {
			// Swap width and height sizes when in portrait, since it will be rotated by
			// 90 degrees
			setCameraInfo(min,max,facing);
		} else {
			setCameraInfo(max,min,facing);
		}
	}
	private void setCameraInfo(int previewWidth, int previewHeight, int facing) {
		synchronized (mLock) {
			mPreviewWidth = previewWidth;
			mPreviewHeight = previewHeight;
			mFacing = facing;
		}
	}

	@Override
	public void onTouchEvent(MotionEvent event) {

	}

	@Override
	public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

	}
}
