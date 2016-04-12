package com.marked.pixsee.face;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.vision.face.Face;
import com.marked.pixsee.R;

import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.GLU;

import javax.microedition.khronos.opengles.GL10;

import rx.Subscription;

/**
 * Created by Tudor on 4/8/2016.
 */
public class FaceRenderer extends Renderer {
    private static final String TAG = "***********";
    private Context context;
    private DirectionalLight directionalLight;
    private Sphere earthSphere;


    private int[] mViewport;
    private double[] mNearPos4;
    private double[] mFarPos4;
    private Vector3 mNearPos;
    private Vector3 mFarPos;
    private Vector3 mNewObjPos;
    private Matrix4 mViewMatrix;
    private Matrix4 mProjectionMatrix;

    private Face mFace;
    int viewportWidth, viewportHeight;

    float zAcc, yAcc, xAcc;
    Subscription subscription;

    public FaceRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(30);
    }


    public void setMFace(Face mFace) {
        this.mFace = mFace;
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


//        final RxSensor rxSensor = new RxSensor(mContext);
//        subscription = rxSensor.observe(Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL)
//                .subscribe(new Subscriber<RxSensorEvent>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//
//                    @Override
//                    public void onNext(RxSensorEvent sensorEvent) {
//                        xAcc = sensorEvent.getValues()[0];
//                        yAcc = sensorEvent.getValues()[1];
//                        zAcc = sensorEvent.getValues()[2];
////                        float norm_Of_g = (float) Math.sqrt(xAcc * xAcc + yAcc * yAcc + zAcc * zAcc); /*normalize the values*/
////                        xAcc /= norm_Of_g;
////                        yAcc /= norm_Of_g;
////                        zAcc /= norm_Of_g;
//
//                    }
//                });

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
        earthSphere = new Sphere(0.5f, 24, 24);
        earthSphere.setMaterial(material);
//        earthSphere.setVisible();

        getCurrentScene().addChild(earthSphere);
        getCurrentCamera().setPosition(0, 0, 5);
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
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);
        earthSphere.rotate(Vector3.Axis.Y, 1.0);
        if (mFace != null) {
//            firstAttempt();
//            secondAttempt();
//            third();
            forth();
//            fifth();
        }
    }

    private void fifth() {
        float x = mFace.getPosition().x + mFace.getWidth() ;
        float y = mFace.getPosition().y + mFace.getHeight();
        earthSphere.setScreenCoordinates(viewportWidth-x,viewportHeight- y, viewportWidth, viewportHeight, 4);
    }
    private void forth() {
        float x = mFace.getPosition().x + mFace.getWidth() ;
        float y = mFace.getPosition().y + mFace.getHeight();
        moveSelectedObject(viewportWidth - x, viewportHeight - y);
    }

    public void moveSelectedObject(float x, float y) {
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

        double factor = (Math.abs(earthSphere.getZ()) + mNearPos.z) / (getCurrentCamera().getFarPlane() - getCurrentCamera()
                .getNearPlane());

        mNewObjPos.setAll(mFarPos);
        mNewObjPos.subtract(mNearPos);
        mNewObjPos.multiply(factor);
        mNewObjPos.add(mNearPos);

       earthSphere.setX(mNewObjPos.x);
       earthSphere.setY(mNewObjPos.y);
    }


    private void firstAttempt() {
        PointF point = mFace.getPosition();
        double widthNormalized = mFace.getWidth() / 2 / viewportWidth;
        double heightNormalized = mFace.getHeight() / 2 / viewportHeight;
        double xNormalized = point.x / viewportWidth;
        double yNormalized = point.y / viewportHeight;

        double x = -xAcc * xNormalized + widthNormalized;
        double y = -yAcc * yNormalized + heightNormalized;

        Log.d(TAG, "onRender: " + x + "/" + y);
        earthSphere.setPosition(x, y, 0);
    }

    private void secondAttempt() {
        PointF point = mFace.getPosition();
        double xNormalized = (mFace.getWidth() / 2) / viewportWidth;
        double yNormalized = (mFace.getHeight() / 2) / viewportHeight;

        double x = xNormalized - xAcc;
        double y = yNormalized - yAcc;

        Log.d(TAG, "onRender: " + xNormalized + "/" + yNormalized);
        earthSphere.setPosition(x, y, 0);
    }

    private void third() {
        float x = mFace.getPosition().x + mFace.getWidth() / 2;
        float y = mFace.getPosition().y + mFace.getHeight() / 2;
        float xOffset = mFace.getWidth() / 2.0f;
        float yOffset = mFace.getHeight() / 2.0f;
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;


//        cube.setPosition(x /viewportWidth, y/viewportHeight, 0);
//        cube.setScreenCoordinates(x, y,(int) viewportWidth,(int) viewportHeight, -2f);
//        cube.setScreenCoordinates(viewportWidth - x, viewportHeight- y, (int) viewportWidth, (int) viewportHeight, getCurrentCamera().getZ()/2);

        Vector3 vector3 = unProject(x, y, 0);
//        cube.setPosition(unProject(x, y, 0));
        earthSphere.setScreenCoordinates(viewportWidth - vector3.x, viewportHeight - vector3.y, (int) viewportWidth, (int) viewportHeight, 5);
//        double objCoords[] = new double[4];
//        int viewCoord[] = {0, 0, (int) viewportWidth, (int) viewportHeight};
//        GLU.gluUnProject(x,  y, 0,
//                cube.getModelMatrix().getDoubleValues(), 0,
//                getCurrentCamera().getProjectionMatrix().getDoubleValues(), 0,
//                viewCoord, 0,
//                objCoords, 0
//        );
//        GLU.gluUnProject(x, y, 1,
//                cube.getModelMatrix().getDoubleValues(), 0,
//                getCurrentCamera().getProjectionMatrix().getDoubleValues(), 0,
//                viewCoord, 0,
//                objCoords, 0
//        );
//        cube.setPosition(objCoords[0], objCoords[1], objCoords[2]);
    }


    int[] floatToInt(float[] array) {
        int tmp[] = new int[array.length];
        for (int it = 0; it < array.length; ++it) {
            tmp[it] = (int) array[it];
        }
        return tmp;
    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }
}
