package com.marked.pixsee.ui.selfie.renderer;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by Tudor on 14-Aug-16.
 */

public class Utils {
	public static boolean isPortraitMode(Context context) {
		int orientation = context.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return false;
		}
		return orientation == Configuration.ORIENTATION_PORTRAIT;

	}
}
