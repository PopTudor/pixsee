package com.marked.pixsee.face;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.vision.face.Face;
import com.marked.pixsee.R;

import org.rajawali3d.debug.DebugBoundingBox;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
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

    public FaceRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(30);
    }

    public void setMFace(Face mFace) {
        this.mFace = mFace;
    }

    /************************************ DEBUGGING  VARIABLES ******************************/
    /* These sphere are used to see where are they mapped on coordinates xy, widthHeight,width/2Height/2 */
    Plane xy = new Plane(0.5f, 0.5f, 1,1);
    Plane wh = new Plane(0.5f, 0.5f, 1,1);
    Plane wh2 = new Plane(0.5f, 0.5f,1,1);

    DebugBoundingBox debugBoundingBox = new DebugBoundingBox(); // I don't know how to use this to replace the above spheres
    /******************************************************************************************/

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
        earthSphere = new Sphere(0.5f, 16, 16);
        earthSphere.setMaterial(material);

        /* every object needs a material else an exception is thrown*/
        xy.setMaterial(material);
        wh.setMaterial(material);
        wh2.setMaterial(material);

        getCurrentScene().addChild(earthSphere);
        getCurrentScene().addChild(xy);
        getCurrentScene().addChild(wh);
        getCurrentScene().addChild(wh2);

        getCurrentCamera().setPosition(0, 0, 10);
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
//            first();
            second();
        }
    }
    /**
     * Second attempt to keep the mapped object on the face when tilting the phone
     * */
    private void second() {
        float x = mFace.getPosition().x + mFace.getWidth();
        float y = mFace.getPosition().y + mFace.getHeight();
        earthSphere.setScreenCoordinates(viewportWidth - x, viewportHeight - y, viewportWidth, viewportHeight, 10);
    }


    /**
     * First attempt to keep the mapped object on the face when tilting the phone
     * */
    private void first() {
        float x = mFace.getPosition().x + mFace.getWidth() /2;
        float y = mFace.getPosition().y + mFace.getHeight()/2;

        float xOffset = mFace.getWidth() / 2.0f;
        float yOffset = mFace.getHeight() / 2.0f;
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;

        moveSelectedObject(viewportWidth - x, viewportHeight - y);
        xy.setScreenCoordinates(
                viewportWidth - mFace.getPosition().x ,
                viewportHeight - mFace.getPosition().y,
                viewportWidth, viewportHeight, 10);
        wh2.setScreenCoordinates(
                viewportWidth - mFace.getWidth() /2,
                viewportHeight -  mFace.getHeight()/2 ,
                viewportWidth, viewportHeight, 10);
        wh.setScreenCoordinates(
                viewportWidth - mFace.getWidth(),
                viewportHeight - mFace.getHeight(),
                viewportWidth, viewportHeight, 10);
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

    @Override
    public void onTouchEvent(MotionEvent event) {

    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }
}
