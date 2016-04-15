package com.marked.pixsee.face;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.Face;
import com.marked.pixsee.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.renderer.Renderer;

/**
 * Created by Tudor on 4/8/2016.
 */
public class FaceRenderer extends Renderer {
    private static final String TAG = "***********";
    private Context context;
    private DirectionalLight directionalLight;
    private Object3D loadedObject;

    private int mFacing = CameraSource.CAMERA_FACING_BACK;

    private int mPreviewWidth;
    private int mPreviewHeight;
    private float mWidthScaleFactor = 1.0f;
    private float mHeightScaleFactor = 1.0f;

    private Face mFace;
    private int viewportWidth, viewportHeight;

    public FaceRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(30);
    }

    private final Object mLock = new Object();

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
    public float translateX(float x) {
        if (mFacing == CameraSource.CAMERA_FACING_FRONT) {
            return viewportWidth - scaleX(x);
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
    public float translateY(float y) {
        if (mFacing == CameraSource.CAMERA_FACING_FRONT)
            return viewportHeight - scaleY(y);
        else
            return scaleY(y);
    }

    @Override
    protected void initScene() {
        viewportWidth = getViewportWidth();
        viewportHeight = getViewportHeight();

        directionalLight = new DirectionalLight(0f, 0f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);
        getCurrentScene().addLight(directionalLight);

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setColor(0);

        { // load custom object
            Texture mlgTexture = new Texture("mlg", R.drawable.mlg);

            try {
                material.addTexture(mlgTexture);
//                loadedObject = new Loader3DSMax(this,R.raw.mlg).parse().getParsedObject();
                loadedObject = new Plane(4f, 4f, 1, 1);

                loadedObject.setTransparent(true);
                loadedObject.setMaterial(material);
            } catch (ATexture.TextureException error) {
                Log.d("DEBUG", "TEXTURE ERROR");
            }
        }
        getCurrentScene().addChild(loadedObject);
        getCurrentCamera().setPosition(0, 0, 10);
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);
        if (mFace != null) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) viewportWidth / (float) mPreviewWidth;
                mHeightScaleFactor = (float) viewportHeight / (float) mPreviewHeight;
            }
            translation();
        }
    }

    /**
     * Second attempt to keep the mapped object on the face when tilting the phone
     */
    private void translation() {
        float x = translateX(mFace.getPosition().x + mFace.getWidth() / 2);
        float y = translateY(mFace.getPosition().y + mFace.getHeight() / 2);

        loadedObject.setScreenCoordinates(x, y, viewportWidth, viewportHeight, 10);
    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }
}
