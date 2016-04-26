package com.marked.pixsee.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

import org.rajawali3d.view.SurfaceView;

/**
 * Created by Tudor on 4/19/2016.
 */
public class FaceSurfaceView extends SurfaceView {
    private boolean screenshot;
    private int mViewportWidth;
    private int mViewportHeight;
	private Bitmap lastScreenshot;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public FaceSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }




}
