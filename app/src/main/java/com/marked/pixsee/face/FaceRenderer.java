package com.marked.pixsee.face;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.Face;
import com.marked.pixsee.face.CameraSource.CameraCallback;
import com.marked.pixsee.face.SelfieActivity.OnFavoritesListener;

import org.jetbrains.annotations.NotNull;
import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.ALoader;
import org.rajawali3d.loader.AMeshLoader;
import org.rajawali3d.loader.Loader3DSMax;
import org.rajawali3d.loader.async.IAsyncLoaderCallback;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ASingleTexture;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.AnimatedGIFTexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.ScreenQuad;
import org.rajawali3d.renderer.Renderer;

import javax.microedition.khronos.opengles.GL10;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 4/8/2016.
 * Used to render models onto a @{StreamingTexture} using the render thread
 * The renderer should only be created once we have a running camera
 */
public class FaceRenderer extends Renderer implements IAsyncLoaderCallback, OnFavoritesListener, CameraCallback {
	private static final String TAG = "***********";
	private static final int CAMERA_Z = 10;
	private final Object mLock = new Object();
	private DirectionalLight directionalLight;


	private int mFacing = CameraSource.CAMERA_FACING_FRONT;
	private int mPreviewWidth;
	private int mPreviewHeight;
	private float mWidthScaleFactor = 1.0f;
	private float mHeightScaleFactor = 1.0f;

	private Object3D loadedObject = null;
	private Face mFace;
	private ASingleTexture aSingleTexture; /* GIF */
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
		getCurrentCamera().setPosition(0, 0, CAMERA_Z);
	}

	boolean streamingReady;

	/**
	 * When we have a camera, start rendering
	 *
	 * @param camera
	 */
	@Override
	public void cameraCreated(Camera camera) {
		mCameraStreamingTexture = new StreamingTexture("Preview", camera, null);
		mCameraStreamingTexture.shouldRecycle(true);
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					Material material = new Material();
					material.setColorInfluence(0);
					material.addTexture(mCameraStreamingTexture);

					screenQuad.setMaterial(material);
//					getCurrentScene().replaceChild(screenQuad, screenQuad);
					getCurrentScene().addChild(screenQuad);
					streamingReady = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	@Override
	public void onModelLoadComplete(ALoader loader) {
		Log.d(TAG, "onModelLoadComplete: ");
		final AMeshLoader obj;
		if (loader instanceof Loader3DSMax) {
			Log.d(TAG, "onModelLoadComplete: ");
			obj = ((Loader3DSMax) loader);
			try {
				((Loader3DSMax) obj).build();
			} catch (ATexture.TextureException e) {
				e.printStackTrace();
			}
		} else
			obj = ((AMeshLoader) loader);

		final Object3D parsedObject = obj.getParsedObject();
		parsedObject.setPosition(Vector3.ZERO);

		if (loadedObject != null) {
			getCurrentScene().removeChild(loadedObject);
//				loadedObject.destroy();
//				loadedObject = null;
		}
		loadedObject = parsedObject;

		getCurrentScene().addChild(loadedObject);
	}


	@Override
	public void onFavoriteClicked(final FaceObject object) {
		Observable.create(new Observable.OnSubscribe<ALoader>() {
			@Override
			public void call(Subscriber<? super ALoader> subscriber) {
				loadModel(object.getLoader(), FaceRenderer.this, object.getResId());
			}
		})
				.subscribeOn(Schedulers.trampoline())
				.subscribe();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		try {
			getTextureManager().reset();
		} catch (Exception e) {/* if texManager != null is not working ? */}
		super.onPause();

	}

	@Override
	public void onRenderFrame(GL10 gl) {
		super.onRenderFrame(gl);
		try {
			if (streamingReady)
				mCameraStreamingTexture.update();
		} catch (RuntimeException e) {
			e.printStackTrace(); //
		}
	}

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
			scale(loadedObject, mFace);
			rotate(loadedObject, mFace);
			translation(loadedObject, mFace);
		}
		if (aSingleTexture != null) {
			try {
				((AnimatedGIFTexture) aSingleTexture).update();
			} catch (ATexture.TextureException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Scale the object based on face size
	 *
	 * @param object3D the object to scale
	 * @param face     the face to scale upon
	 */
	private void scale(@NotNull Object3D object3D, @NotNull Face face) {
		float x1 = face.getPosition().x;
		float y1 = face.getPosition().y;
		float x2 = face.getWidth();
		float y2 = face.getHeight();
		double dist = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
		double scaleValue = dist / mDefaultViewportWidth + 0.4;
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
		float eulerY = face.getEulerY();
		object3D.rotateAround(Vector3.Y, eulerZ, false);
		object3D.rotateAround(Vector3.Z, eulerZ, false);
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
		try {
			object.setScreenCoordinates(x, y, mCurrentViewportWidth, mCurrentViewportHeight, CAMERA_Z);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
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
