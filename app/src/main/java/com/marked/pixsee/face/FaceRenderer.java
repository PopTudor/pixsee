package com.marked.pixsee.face;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.Face;
import com.marked.pixsee.R;
import com.marked.pixsee.face.CameraSource.CameraCallback;
import com.marked.pixsee.face.SelfieActivity.OnFavoritesListener;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.ALoader;
import org.rajawali3d.loader.AMeshLoader;
import org.rajawali3d.loader.async.IAsyncLoaderCallback;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ASingleTexture;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.AnimatedGIFTexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.ScreenQuad;
import org.rajawali3d.renderer.Renderer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Tudor on 4/8/2016.
 */
class FaceRenderer extends Renderer implements IAsyncLoaderCallback, OnFavoritesListener, CameraCallback {
	private static final String TAG = "***********";
	private final Object mLock = new Object();
	private DirectionalLight directionalLight;

	private int mFacing = CameraSource.CAMERA_FACING_FRONT;
	private int mPreviewWidth;
	private int mPreviewHeight;
	private float mWidthScaleFactor = 1.0f;
	private float mHeightScaleFactor = 1.0f;

	private Object3D loadedObject = null;
	private Face mFace;
	ASingleTexture aSingleTexture;
	private Handler handler;
	/******************
	 * Camera preview *
	 ******************/
	private ScreenQuad screenQuad;
	private StreamingTexture mCameraStreamingTexture;

	public FaceRenderer(Context context) {
		super(context);
		setFrameRate(60);
		handler = new Handler();

	}

	@Override
	protected void initScene() {
		screenQuad = new ScreenQuad();
		screenQuad.rotate(Vector3.Axis.Z, -90);

		directionalLight = new DirectionalLight(0f, 0f, -1.0f);
		directionalLight.setColor(1.0f, 1.0f, 1.0f);
		directionalLight.setPower(2);

		getCurrentScene().addLight(directionalLight);
		getCurrentCamera().setPosition(0, 0, 10);
	}

	boolean streamingReady;

	@Override
	public void cameraCreated(Camera camera) {
		mCameraStreamingTexture = new StreamingTexture("Preview", camera, null);
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					Material material = new Material();
					material.setColorInfluence(0);
					material.addTexture(mCameraStreamingTexture);

					screenQuad.setMaterial(material);
					getCurrentScene().addChild(screenQuad);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		streamingReady = true;
	}


	@Override
	public void onModelLoadComplete(ALoader loader) {
		Log.d(TAG, "onModelLoadComplete: ");
		final AMeshLoader obj = (AMeshLoader) loader;
		final Object3D parsedObject = obj.getParsedObject();
		parsedObject.setPosition(Vector3.ZERO);
		if (loadedObject != null)
			getCurrentScene().removeChild(loadedObject);
		loadedObject = parsedObject;

//        loadedObject = testLoadedObject();
		getCurrentScene().addChild(loadedObject);
	}


	@Override
	public void onFavoriteClicked(final FaceObject object) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (object.animatedTexture)
					aSingleTexture = object.texture;
				loadModel(object.getLoader(), FaceRenderer.this, object.getTag());
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {

		getTextureManager().taskReset();
		getTextureManager().reset();
		super.onPause();

	}

	@Override
	public void onRenderFrame(GL10 gl) {
		super.onRenderFrame(gl);
		if (mCameraStreamingTexture != null) {
			try {
				mCameraStreamingTexture.update();
			}catch (RuntimeException e){
				e.printStackTrace(); //
			}
		}
	}

	/**********************
	 * TEST METHOD
	 ****************************/
	Object3D testLoadedObject() {
		Object3D object3D = new Cube(3.5f);
		object3D.rotate(Vector3.Axis.Y, 180);
		Material material = new Material();
		material.setColor(Color.WHITE);
		material.enableLighting(true);
		material.setDiffuseMethod(new DiffuseMethod.Lambert());
		try {
			Texture mlgTexture = new Texture("mlg_png", R.drawable.mlg);
			material.addTexture(mlgTexture);
		} catch (ATexture.TextureException error) {
			error.printStackTrace();
		}
		object3D.setMaterial(material);
		object3D.setTransparent(true);
		return object3D;
	}

	/****************************************************************************************************/

	@Override
	public void onModelLoadFailed(ALoader loader) {
		Log.d(TAG, "onModelLoadFailed: ");
		onDone();
	}

	public void isSmiling() {

	}

	@Override
	protected void onRender(long ellapsedRealtime, double deltaTime) {
		super.onRender(ellapsedRealtime, deltaTime);
		if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
			mWidthScaleFactor = (float) mCurrentViewportWidth / (float) mPreviewWidth;
			mHeightScaleFactor = (float) mCurrentViewportHeight / (float) mPreviewHeight;
		}
		if (mFace != null && loadedObject != null) {
			try { // FIXME: 4/28/2016 why is this throwing null pointer exception when clearly I check for null
				scale(loadedObject);
				rotate(loadedObject);
				translation(loadedObject);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		if (aSingleTexture != null) {
			try {
				((AnimatedGIFTexture) aSingleTexture).update();
			} catch (ATexture.TextureException e) {
				e.printStackTrace();
			}
		}
	}

	private void scale(Object3D object3D) {
		if (object3D == null || mFace == null)
			return;
		float x1 = mFace.getPosition().x;
		float y1 = mFace.getPosition().y;
		float x2 = mFace.getWidth();
		float y2 = mFace.getHeight();
		double dist = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
		double scaleValue = dist / mDefaultViewportWidth + 0.4;
		loadedObject.setScale(scaleValue);
	}

	private void rotate(Object3D object3D) {
		if (object3D == null || mFace == null)
			return;
		float eulerZ = mFace.getEulerZ();
		object3D.rotateAround(Vector3.Z, eulerZ, false);
	}

	/**
	 * Second attempt to keep the mapped object on the face when tilting the phone
	 */
	private void translation(Object3D object) {
		if (object == null)
			return;
		float x = translateX(mFace.getPosition().x + mFace.getWidth() / 2);
		float y = translateY(mFace.getPosition().y + mFace.getHeight() / 2);
		object.setScreenCoordinates(x, y, mCurrentViewportWidth, mCurrentViewportHeight, 10);
	}

	public void onNewItem(Face face) {
		this.mFace = face;
		if (loadedObject != null)
			loadedObject.setVisible(true);
	}

	public void onUpdate(Face face) {
		this.mFace = face;
	}

	public void onDone() {
		this.mFace = null;
		if (loadedObject != null)
			loadedObject.setVisible(false);
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
	 * image coordinates later.
	 */
	public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
		synchronized (mLock) {
			mPreviewWidth = previewWidth;
			mPreviewHeight = previewHeight;
			mFacing = facing;
		}
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

	@Override
	public void onTouchEvent(MotionEvent event) {

	}

	@Override
	public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

	}

}
