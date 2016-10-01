package com.marked.pixsee.selfie.renderer;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.common.images.Size;
import com.marked.pixsee.selfie.SelfieFragment.OnFavoritesListener;
import com.marked.pixsee.selfie.data.SelfieObject;
import com.marked.pixsee.selfie.renderer.transformation.RotationTransform;
import com.marked.pixsee.selfie.renderer.transformation.ScaleTransform;
import com.marked.pixsee.selfie.renderer.transformation.Transform;
import com.marked.pixsee.selfie.renderer.transformation.TranslateTransform;
import com.pixsee.face.PixseeFace;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.ALoader;
import org.rajawali3d.loader.AMeshLoader;
import org.rajawali3d.loader.async.IAsyncLoaderCallback;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;

import java.util.ArrayList;


/**
 * Created by Tudor on 4/8/2016.
 * Used to render models onto a @{StreamingTexture} using the render thread
 * The renderer should only be created once we have a running camera
 */
public class SelfieRenderer extends Renderer implements IAsyncLoaderCallback, OnFavoritesListener, SelfieTrackerAR.TrackerCallback {
	private static final String TAG = "***********";
	private static final int CAMERA_Z = 6;
	private final Object mLock = new Object();
	private ArrayList<Transform> mTransforms = new ArrayList<>(3);
	private TranslateTransform mTranslateTransform;
	private int mPreviewWidth, mPreviewHeight;
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

		initTransforms();
	}

	private void initTransforms() {
		mTranslateTransform = new TranslateTransform(CAMERA_Z);
		ScaleTransform scaleTransform = new ScaleTransform();
		RotationTransform rotationTransform = new RotationTransform();

		mTransforms.add(scaleTransform);
		mTransforms.add(rotationTransform);
		mTransforms.add(mTranslateTransform);
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
			float widthScaleFactor = (float) mCurrentViewportWidth / (float) mPreviewWidth;
			float heightScaleFactor = (float) mCurrentViewportHeight / (float) mPreviewHeight;
			mTranslateTransform.setScaleFactor(widthScaleFactor, heightScaleFactor);
		}
	}

	@Override
	public void onNewItem(int id, PixseeFace pixseeFace) {
		if (loadedObject != null)
			loadedObject.setVisible(true);
	}

	@Override
	public void onUpdate(PixseeFace pixseeFace) {
		if (loadedObject != null && pixseeFace != null) {
			for (Transform it : mTransforms)
				it.transform(loadedObject, pixseeFace);
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

	@Override
	public void setViewPort(int width, int height) {
		super.setViewPort(width, height);
		Transform.setCurrentViewport(width, height);
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
			mTranslateTransform.setFacing(facing);
		}
	}

	@Override
	public void onTouchEvent(MotionEvent event) {

	}

	@Override
	public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

	}
}
