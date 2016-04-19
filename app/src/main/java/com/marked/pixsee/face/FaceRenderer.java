package com.marked.pixsee.face;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.Face;
import com.marked.pixsee.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.ALoader;
import org.rajawali3d.loader.AMeshLoader;
import org.rajawali3d.loader.async.IAsyncLoaderCallback;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.renderer.Renderer;

/**
 * Created by Tudor on 4/8/2016.
 */
public class FaceRenderer extends Renderer implements IAsyncLoaderCallback, SelfieActivity.OnFavoritesListener {
	private static final String TAG = "***********";
	private final Object mLock = new Object();
	private Context context;
	private DirectionalLight directionalLight;

	private int mFacing = CameraSource.CAMERA_FACING_BACK;
	private int mPreviewWidth;
	private int mPreviewHeight;
	private float mWidthScaleFactor = 1.0f;
	private float mHeightScaleFactor = 1.0f;

	private Face mFace;

	public FaceRenderer(Context context) {
		super(context);
		this.context = context;
		setFrameRate(30);
	}

	@Override
	protected void initScene() {
		directionalLight = new DirectionalLight(0f, 0f, -1.0f);
		directionalLight.setColor(1.0f, 1.0f, 1.0f);
		directionalLight.setPower(2);

		getCurrentScene().addLight(directionalLight);
		getCurrentCamera().setPosition(0, 0, 10);
	}

//    @Override
//    public void onRenderSurfaceCreated(EGLConfig config, GL10 gl, int width, int height) {
//        super.onRenderSurfaceCreated(config, gl, width, height);
//        addObject(new FaceObject(0,this));
//    }

	@Override
	public void onModelLoadComplete(ALoader loader) {
		Log.d(TAG, "onModelLoadComplete: ");
		final AMeshLoader obj = (AMeshLoader) loader;
		final Object3D parsedObject = obj.getParsedObject();
		parsedObject.setPosition(Vector3.ZERO);
		loadedObject = parsedObject;

//        loadedObject = testLoadedObject();
		getCurrentScene().clearChildren();
		getCurrentScene().addChild(loadedObject);
	}

	private Object3D loadedObject = null;

	@Override
	public void onFavoriteClicked(FaceObject object) {
		FaceObject faceObject = new FaceObject(this);
		faceObject.setTexture(object.getTag());
		loadModel(object.getLoader(), this, faceObject.getTag());
	}

	/**********************
	 * TEST METHOD
	 ****************************/
	Object3D testLoadedObject() {
		Object3D object3D = new Cube(3.5f);
		object3D.rotate(Vector3.Axis.Y, 180);
		Material material = new Material();
		material.setColor(0);
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
	}

	@Override
	protected void onRender(long ellapsedRealtime, double deltaTime) {
		super.onRender(ellapsedRealtime, deltaTime);
		if (mFace != null && loadedObject != null) {
			if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
				mWidthScaleFactor = (float) mCurrentViewportWidth / (float) mPreviewWidth;
				mHeightScaleFactor = (float) mCurrentViewportHeight / (float) mPreviewHeight;
			}
			translation(loadedObject);
		}
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

	public void setMFace(Face mFace) {
		this.mFace = mFace;
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
