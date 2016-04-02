package com.marked.pixsee.face;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by Tudor Pop on 02-Apr-16.
 */
public class FaceSurfaceView extends GLSurfaceView {


	public FaceSurfaceView(Context context) {
		super(context);
		init();
	}

	public FaceSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	void init(){
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
//		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
//		getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}
}
