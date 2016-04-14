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
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.GLU;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Tudor on 4/8/2016.
 */
public class FaceRenderer extends Renderer {
    private static final String TAG = "***********";
    private Context context;
    private DirectionalLight directionalLight;
    private Sphere sphereEarth;

    private int mFacing = CameraSource.CAMERA_FACING_BACK;
    private int[] mViewport;
    private double[] mNearPos4;
    private double[] mFarPos4;
    private Vector3 mNearPos;
    private Vector3 mFarPos;
    private Vector3 mNewObjPos;
    private Matrix4 mViewMatrix;
    private Matrix4 mProjectionMatrix;

    private int mPreviewWidth;
    private int mPreviewHeight;
    private float mWidthScaleFactor = 1.0f;
    private float mHeightScaleFactor = 1.0f;

    private Face mFace;
    int viewportWidth, viewportHeight;

    public FaceRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(30);
    }

    public void setmPreviewWidth(int mPreviewWidth) {
        this.mPreviewWidth = mPreviewWidth;
    }

    public void setmPreviewHeight(int mPreviewHeight) {
        this.mPreviewHeight = mPreviewHeight;
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
        {
            mViewport = new int[]{0, 0, viewportWidth, viewportHeight};
            mNearPos4 = new double[4];
            mFarPos4 = new double[4];
            mNearPos = new Vector3();
            mFarPos = new Vector3();
            mNewObjPos = new Vector3();
            mViewMatrix = getCurrentCamera().getViewMatrix();
            mProjectionMatrix = getCurrentCamera().getProjectionMatrix();
        }

        directionalLight = new DirectionalLight(1f, .2f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);
        getCurrentScene().addLight(directionalLight);

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setColor(0);

        Texture earthTexture = new Texture("Earth", R.drawable.earth);
        try {
            material.addTexture(earthTexture);
        } catch (ATexture.TextureException error) {
            Log.d("DEBUG", "TEXTURE ERROR");
        }
        sphereEarth = new Sphere(0.5f, 16, 16);
        sphereEarth.setMaterial(material);
        Quaternion quaternion = new Quaternion().fromAngleAxis(Vector3.Axis.X, 90.0);
        sphereEarth.setOrientation(quaternion);
        getCurrentScene().addChild(sphereEarth);
        getCurrentCamera().setPosition(0, 0, 10);
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);
//        sphereEarth.rotate(Vector3.Axis.Y, 1.0);
        if (mFace != null) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) viewportWidth / (float) mPreviewWidth;
                mHeightScaleFactor = (float) viewportHeight / (float) mPreviewHeight;
            }
//            first();
            second();
        }
    }

    /**
     * Second attempt to keep the mapped object on the face when tilting the phone
     */
    private void second() {
        float x = translateX(mFace.getPosition().x + mFace.getWidth() / 2);
        float y = translateY(mFace.getPosition().y + mFace.getHeight() / 2);

        sphereEarth.setScreenCoordinates(x, y, viewportWidth, viewportHeight, 10);
    }

    /**
     * First attempt to keep the mapped object on the face when tilting the phone
     */
    private void first() {
        float x = translateX(mFace.getPosition().x + mFace.getWidth() / 2);
        float y = translateY(mFace.getPosition().y + mFace.getHeight() / 2);

        moveSelectedObject(sphereEarth, x, y);
    }
    private void moveSelectedObject(Object3D object3D, float x, float y) {
        // -- unproject the screen coordinate (2D) to the camera's near plane
        GLU.gluUnProject(x, y, 0, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mNearPos4, 0);

        //
        // -- unproject the screen coordinate (2D) to the camera's far plane
        //

        GLU.gluUnProject(x, y, 1.f, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mFarPos4, 0);

        //
        // -- transform 4D coordinates (x, y, z, w) to 3D (x, y, z) by dividing
        // each coordinate (x, y, z) by w.
        //

        mNearPos.setAll(mNearPos4[0] / mNearPos4[3], mNearPos4[1] / mNearPos4[3], mNearPos4[2] / mNearPos4[3]);
        mFarPos.setAll(mFarPos4[0] / mFarPos4[3], mFarPos4[1] / mFarPos4[3], mFarPos4[2] / mFarPos4[3]);

        //
        // -- now get the coordinates for the selected object
        //

        double factor = (Math.abs(sphereEarth.getZ()) + mNearPos.z) /
                (getCurrentCamera().getFarPlane() - getCurrentCamera().getNearPlane());

        mNewObjPos.setAll(mFarPos);
        mNewObjPos.subtract(mNearPos);
        mNewObjPos.multiply(factor);
        mNewObjPos.multiply(-1);
        mNewObjPos.add(mNearPos);

        object3D.setX(mNewObjPos.x);
        object3D.setY(mNewObjPos.y);
    }

    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        mViewport[2] = getViewportWidth();
        mViewport[3] = getViewportHeight();
        mViewMatrix = getCurrentCamera().getViewMatrix();
        mProjectionMatrix = getCurrentCamera().getProjectionMatrix();
    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }
}
