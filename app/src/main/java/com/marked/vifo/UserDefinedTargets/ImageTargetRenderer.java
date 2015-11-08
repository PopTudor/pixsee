/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.marked.vifo.UserDefinedTargets;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.marked.vifo.VuforiaApplication.ApplicationSession;
import com.marked.vifo.VuforiaApplication.utils.SampleMath;
import com.marked.vifo.VuforiaApplication.utils.SampleUtils;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.threed.jpct.Camera;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// The renderer class for the ImageTargetsBuilder sample.
public class ImageTargetRenderer implements GLSurfaceView.Renderer {
    // Constants:
    static final float kObjectScale = 2.f;
    private static final String LOGTAG = "ImageTargetRenderer";
    public boolean mIsActive = false;
    private ApplicationSession mVuforiaAppSession;

    // Reference to main activity
    private ImageTargets mActivity;
    private float[] modelViewMat;

    private TextureManager mTextureManager;
    private FrameBuffer fb;
    private World mWorld;
    private Light mSun;
    private Camera cam;
    private float fov;
    private float fovy;

    public ImageTargetRenderer(ImageTargets activity, ApplicationSession session) {
        this.mActivity = activity;
        this.mVuforiaAppSession = session;
        this.mTextureManager = TextureManager.getInstance();
        this.mWorld = new World();
        this.mSun = new Light(mWorld);
        mWorld.setAmbientLight(25, 25, 25);
        mSun.setIntensity(250, 250, 250);
        try {
            // keep the texture small(ideal under 1mb; if it's too big won't keep up with the program because the texture is loaded
            // on an background thread so it must be easy to load the texture) or compress any texture online
            // it's also recommended to be a power of 2. 2^x width/height
            Texture texture;
            if (!mTextureManager.containsTexture("bourak")) {
                texture = new Texture(mActivity.getAssets().open("bourak_3ds/bourak.jpg"));
                mTextureManager.addTexture("bourak", texture);
            }
            if (!mTextureManager.containsTexture("box")) {
                texture = new Texture(64, 64, RGBColor.WHITE);
                mTextureManager.addTexture("box",texture);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Object3D loadOBJ;

        try {
            loadOBJ = loadModel(mActivity.getAssets().open("bourak_3ds/bourak.3DS"), (float) 0.008);
            loadOBJ.setTexture("bourak");
            loadOBJ.build();

            mWorld.addObject(loadOBJ);
            cam = mWorld.getCamera();
            cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
            cam.lookAt(loadOBJ.getTransformedCenter());

            SimpleVector sv = new SimpleVector();
            sv.set(loadOBJ.getTransformedCenter());
            sv.y += 100;
            sv.z += 100;
            mSun.setPosition(sv);

        } catch (Exception e) {
            Log.e("**", e.getStackTrace().toString());

            Object3D box = Primitives.getCube(10.0f);
            box.calcTextureWrapSpherical();
            box.setTexture("box");
            box.strip();
            box.build();

            mWorld.addObject(box);
            cam = mWorld.getCamera();
            cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
            cam.lookAt(box.getTransformedCenter());

            SimpleVector sv = new SimpleVector();
            sv.set(box.getTransformedCenter());
            sv.y += 100;
            sv.z += 100;
            mSun.setPosition(sv);
        }


        MemoryHelper.compact();
    }

    // Called when the surface is created or recreated.
    @SuppressLint("LongLogTag")
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");
        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        mVuforiaAppSession.onSurfaceCreated();
    }

    // Called when the surface changed size.
    @SuppressLint("LongLogTag")
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Call function to update rendering when render surface
        // parameters have changed:

        // Call Vuforia function to handle render surface size changes:
        mActivity.updateRendering();
        mVuforiaAppSession.onSurfaceChanged(width, height);

        if (fb != null) {
            fb.dispose();
        }
        fb = new com.threed.jpct.FrameBuffer(width, height);
        Config.viewportOffsetAffectsRenderTarget = true;

    }

    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mIsActive)
            return;
        // Call our function to render content
        renderFrame();
        updateCamera();
    }

    public void drawWorld() {
        try {
            mWorld.renderScene(fb);
            mWorld.draw(fb);
            fb.display();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderFrame() {
        // Clear color and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Get the state from Vuforia and mark the beginning of a rendering
        // section
        State state = Renderer.getInstance().begin();
        // Explicitly render the Video Background
        Renderer.getInstance().drawVideoBackground();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() ==
                VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
            GLES20.glFrontFace(GLES20.GL_CW); // Front camera
        else
            GLES20.glFrontFace(GLES20.GL_CCW); // Back camera

        // Render the RefFree UI elements depending on the current state
        mActivity.refFreeFrame.render();
        // Did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
            // Get the trackable:
            TrackableResult trackableResult = state.getTrackableResult(tIdx);
            Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(trackableResult.getPose());
            float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

            float angle = 90;
            float[] modelViewProjection = new float[16];
            Matrix.translateM(modelViewMatrix, 0, 0, 0, kObjectScale);
            Matrix.rotateM(modelViewMatrix, 0, angle, 0, 0, kObjectScale);
            Matrix.scaleM(modelViewMatrix, 0, kObjectScale, kObjectScale, kObjectScale);
            Matrix.multiplyMM(modelViewProjection, 0, mVuforiaAppSession.getProjectionMatrix().getData(), 0, modelViewMatrix,
                    0);
            modelViewMatrix_Vuforia.setData(modelViewMatrix);

            Matrix44F inverseMV = SampleMath.Matrix44FInverse(modelViewMatrix_Vuforia);
            Matrix44F invTranspMV = SampleMath.Matrix44FTranspose(inverseMV);
            updateModelviewMatrix(invTranspMV.getData());

            // hide the objects when the targets are not detected
            if (state.getNumTrackableResults() == 0) {
                float m[] = {
                        1, 0, 0, 0,
                        0, 1, 0, 0,
                        0, 0, 1, 0,
                        0, 0, -10000, 1
                };
                updateModelviewMatrix(m);
            }

            drawWorld();
            SampleUtils.checkGLError("UserDefinedTargets renderFrame");
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        Renderer.getInstance().end();
    }

    public void updateCamera() {
        if (modelViewMat != null) {
            float[] m = modelViewMat;
            final SimpleVector camUp;

            if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                camUp = new SimpleVector(-m[0], -m[1], -m[2]);
            else
                camUp = new SimpleVector(-m[4], -m[5], -m[6]);

            final SimpleVector camDirection = new SimpleVector(m[8], m[9], m[10]);
            final SimpleVector camPosition = new SimpleVector(m[12], m[13], m[14]);

            cam.setOrientation(camDirection, camUp);
            cam.setPosition(camPosition);

            cam.setFOV(fov);
            cam.setYFOV(fovy);
        }
    }

    public void updateModelviewMatrix(float mat[]) {
        modelViewMat = mat;
    }

    private Object3D loadModel(InputStream filename, float scale) throws FileNotFoundException {
        Object3D[] model = Loader.load3DS(new BufferedInputStream(filename), scale);
        Object3D o3d = new Object3D(0);
        Object3D temp = null;
        for (int i = 0; i < model.length; i++) {
            temp = model[i];
            temp.setCenter(SimpleVector.ORIGIN);
            temp.rotateX((float) (.4 * Math.PI));
            temp.rotateY((float) (.1 * Math.PI));
            temp.rotateMesh();
            temp.setRotationMatrix(new com.threed.jpct.Matrix());
            o3d = Object3D.mergeObjects(o3d, temp);
            o3d.build();
        }
        return o3d;
    }
}
