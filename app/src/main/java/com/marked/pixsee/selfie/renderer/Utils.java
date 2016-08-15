package com.marked.pixsee.selfie.renderer;

import android.content.Context;
import android.content.res.Configuration;

import com.google.android.gms.vision.face.Face;

/**
 * Created by Tudor on 14-Aug-16.
 */

public class Utils {
	static double calculateFaceCenter(Face face) {
		float x1 = face.getPosition().x;
		float y1 = face.getPosition().y;
		float x2 = face.getWidth();
		float y2 = face.getHeight();
		return Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0));
	}

	public static boolean isPortraitMode(Context context) {
		int orientation = context.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return false;
		}
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			return true;
		}

		return false;
	}
}
