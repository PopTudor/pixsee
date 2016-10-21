package com.marked.pixsee.ui.main.strategy;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.ui.main.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Tudor on 27-Jul-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class ShareStrategyTest {
	
	@Test
	public void testShowAction() throws Exception {
		PictureActionStrategy pictureActionStrategy = new ShareStrategy();
		MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
		pictureActionStrategy.showAction(mainActivity);
	}
}