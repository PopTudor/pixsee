package com.marked.pixsee.ui.main.strategy;

import android.support.v4.app.FragmentActivity;

import com.marked.pixsee.R;
import com.marked.pixsee.ui.selfie.PictureDetailShareFragment;

/**
 * Created by Tudor on 20-Jul-16.
 */
public class ShareStrategy implements PictureActionStrategy {
	@Override
	public void showAction(FragmentActivity activity) {
		activity.getSupportFragmentManager().beginTransaction()
				.replace(R.id.mainContainer2, PictureDetailShareFragment.newInstance())
				.addToBackStack(null)
				.commit();
	}
}
