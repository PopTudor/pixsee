/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.marked.pixsee.face;

import com.google.android.gms.vision.face.Face;
import com.qualcomm.vuforia.Vec2F;


public class FreeFrame {
	private static final String LOGTAG = "FreeFrame***";
	STATUS curStatus;

	// / Current color of the target finder. This changes color
	// / depending on frame quality.
	float  colorFrame[];
	// / Half of the screen size, used often in the rendering pipeline
	Vec2F  halfScreenSize;
	// / Keep track of the time between frames for color transitions
	long   lastFrameTime;
	long   lastSuccessTime;
	// The latest trackable source to be extracted from the Target Builder
	Face   mFace;
	Selfie mActivity;

	public FreeFrame(Selfie activity) {
		mActivity = activity;
		colorFrame = new float[4];
		curStatus = STATUS.STATUS_IDLE;
		lastSuccessTime = 0;
		colorFrame[0] = 0.0f;
		colorFrame[1] = 0.0f;
		colorFrame[2] = 0.0f;
		colorFrame[3] = 0.75f;

		halfScreenSize = new Vec2F();
	}

	// Function used to transition in the range [0, 1]
	float transition(float v0, float inc, float a, float b) {
		float vOut = v0 + inc;
		return (vOut < a ? a :(vOut > b ? b :vOut));
	}


	float transition(float v0, float inc) {
		return transition(v0, inc, 0.0f, 1.0f);
	}


	void reset() {
		curStatus = STATUS.STATUS_IDLE;
	}


	// Some helper functions
	enum STATUS {
		STATUS_IDLE, STATUS_SCANNING, STATUS_CREATING, STATUS_SUCCESS
	}
}
