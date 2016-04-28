package com.marked.pixsee.face;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Surface;

import org.rajawali3d.view.SurfaceView;

/**
 * Created by Tudor on 4/19/2016.
 */
public class FaceSurfaceView extends SurfaceView {
    Surface surface;
    Canvas mCanvas;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        mCanvas = surface.lockCanvas(null);
//        mCanvas.drawPaint();
    }

    public FaceSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }




}
