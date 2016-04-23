package com.marked.pixsee.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Tudor on 4/8/2016.
 */
public class FaceRenderer extends Renderer implements IAsyncLoaderCallback, SelfieActivity
		                                                                            .OnFavoritesListener {
	private static final String TAG = "***********";
	private final Object mLock = new Object();
	private Context context;
	private DirectionalLight directionalLight;

	private int mFacing = CameraSource.CAMERA_FACING_BACK;
	private int mPreviewWidth;
	private int mPreviewHeight;
	private float mWidthScaleFactor = 1.0f;
	private float mHeightScaleFactor = 1.0f;

	private Object3D loadedObject = null;
	private Face mFace;
	private boolean screenshot;
	private Bitmap lastScreenshot;


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

	public void takeScreenshot() {
		screenshot = true;
	}

	public void saveScreenshot(Bitmap screenshot) {
		try {
			File file = Utils.getPublicPixseeDir();
			FileOutputStream out = new FileOutputStream(file.getPath() + "/photo_renderer.png");
			screenshot.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

	@Override
	public void onFavoriteClicked(FaceObject object) {
		FaceObject faceObject = new FaceObject(this);
		faceObject.setTexture(object.getTag());
		loadModel(object.getLoader(), this, faceObject.getTag());
	}

	public Bitmap getLastScreenshot() {
		return lastScreenshot;
	}

	@Override
	public void onRenderFrame(GL10 gl) {
		super.onRenderFrame(gl);
		if (screenshot) {
			gl.glClearColor(1f, 1f, 1f, 1f);
			int screenshotSize = mDefaultViewportWidth * mDefaultViewportHeight;
			ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
			bb.order(ByteOrder.nativeOrder());
			gl.glReadPixels(0, 0, mDefaultViewportWidth, mDefaultViewportHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
			int pixelsBuffer[] = new int[screenshotSize];
			bb.asIntBuffer().get(pixelsBuffer);
			bb = null;
			Bitmap bitmap = Bitmap.createBitmap(mDefaultViewportWidth, mDefaultViewportHeight, Bitmap.Config.RGB_565);
			bitmap.setPixels(pixelsBuffer, screenshotSize - mDefaultViewportWidth, -mDefaultViewportWidth, 0, 0,
					mDefaultViewportWidth, mDefaultViewportHeight);
			pixelsBuffer = null;

			short sBuffer[] = new short[screenshotSize];
			ShortBuffer sb = ShortBuffer.wrap(sBuffer);
			bitmap.copyPixelsToBuffer(sb);

			//Making created bitmap (from OpenGL points) compatible with Android bitmap
			for (int i = 0; i < screenshotSize; ++i) {
				short v = sBuffer[i];
				sBuffer[i] = (short) (((v & 0x1f) << 11) | (v & 0x7e0) | ((v & 0xf800) >> 11));
			}
			sb.rewind();
			bitmap.copyPixelsFromBuffer(sb);

			lastScreenshot = bitmap;
			saveScreenshot(lastScreenshot);

			screenshot = false;
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
