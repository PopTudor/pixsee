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
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.async.IAsyncLoaderCallback;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 4/8/2016.
 */
public class FaceRenderer extends Renderer implements IAsyncLoaderCallback {
    private static final String TAG = "***********";
    private Context context;
    private DirectionalLight directionalLight;
    private List<FaceObject> mObjectList = new ArrayList<>(1);

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

    private final Object mLock = new Object();


    @Override
    protected void initScene() {
        directionalLight = new DirectionalLight(0f, 0f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);

        getCurrentScene().addLight(directionalLight);
        getCurrentCamera().setPosition(0, 0, 10);
            final LoaderOBJ loaderOBJ = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.rock);
            loadModel(loaderOBJ,this,R.raw.rock);
    }

    void addObject(FaceObject object3D) {
        synchronized (mLock) {
            mObjectList.add(object3D);
        }
    }

    void removeObject(Object3D object3D) {
        synchronized (mLock) {
            if (mObjectList.contains(object3D)) {
                mObjectList.remove(object3D);
            }
        }
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);
        if (mFace != null) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) mCurrentViewportWidth / (float) mPreviewWidth;
                mHeightScaleFactor = (float) mCurrentViewportHeight / (float) mPreviewHeight;
            }
//            for (FaceObject object : mObjectList) {
//                switch (object.getDrawingPosition()){
//                    case Landmark.NOSE_BASE:
//
//                }
            if (mObjectList.size()>0){
                getCurrentScene().addChild(mObjectList.get(mObjectList.size() - 1).getObject3D());
            }
                translation(mObjectList.get(0).getObject3D());
//            }
        }
    }

    /**
     * Second attempt to keep the mapped object on the face when tilting the phone
     */
    private void translation(Object3D object) {
        if (object==null)
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
    public float translateX(float x) {
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
    public float translateY(float y) {
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

    @Override
    public void onModelLoadComplete(ALoader loader) {
        Log.d(TAG, "onModelLoadComplete: ");
        final LoaderOBJ obj = (LoaderOBJ) loader;
        final Object3D parsedObject = obj.getParsedObject();
        parsedObject.setPosition(Vector3.ZERO);
//        Material material = new Material();
//        material.enableLighting(true);
//        material.setColor(Color.RED);
//        material.setDiffuseMethod(new DiffuseMethod.Lambert());
//
//        Plane plane = new Plane();
//        plane.setMaterial(material);

        getCurrentScene().addChild(parsedObject);
    }

    @Override
    public void onModelLoadFailed(ALoader loader) {

    }
}
