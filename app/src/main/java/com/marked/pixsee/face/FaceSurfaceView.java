package com.marked.pixsee.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import org.rajawali3d.view.TextureView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Tudor on 4/19/2016.
 */
public class FaceSurfaceView extends TextureView implements GLSurfaceView.Renderer {
    private boolean screenshot;
    private int mViewportWidth;
    private int mViewportHeight;
	private Bitmap lastScreenshot;

	public FaceSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if(screenshot){
            int screenshotSize = mViewportWidth * mViewportHeight;
            ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
            bb.order(ByteOrder.nativeOrder());
            gl.glReadPixels(0, 0, mViewportWidth, mViewportHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
            int pixelsBuffer[] = new int[screenshotSize];
            bb.asIntBuffer().get(pixelsBuffer);
            bb = null;
            Bitmap bitmap = Bitmap.createBitmap(mViewportWidth, mViewportHeight, Bitmap.Config.RGB_565);
            bitmap.setPixels(pixelsBuffer, screenshotSize-mViewportWidth, -mViewportWidth, 0, 0, mViewportWidth, mViewportHeight);
            pixelsBuffer = null;

            short sBuffer[] = new short[screenshotSize];
            ShortBuffer sb = ShortBuffer.wrap(sBuffer);
            bitmap.copyPixelsToBuffer(sb);

            //Making created bitmap (from OpenGL points) compatible with Android bitmap
            for (int i = 0; i < screenshotSize; ++i) {
                short v = sBuffer[i];
                sBuffer[i] = (short) (((v&0x1f) << 11) | (v&0x7e0) | ((v&0xf800) >> 11));
            }
            sb.rewind();
            bitmap.copyPixelsFromBuffer(sb);
            lastScreenshot = bitmap;
//            saveScreenshot(lastScreenshot);

            screenshot = false;
        }
    }
}
