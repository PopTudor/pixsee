package com.marked.pixsee.service;

import com.marked.pixsee.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowService;

/**
 * Created by Tudor on 03-Aug-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GCMInstanceIDListenerServiceTest {

	
	@Test
	public void testOnTokenRefresh() throws Exception {
		GCMInstanceIDListenerService gcmInstanceIDListenerService = Robolectric.setupService(GCMInstanceIDListenerService.class);
		ShadowService shadowService = Shadows.shadowOf(gcmInstanceIDListenerService);
	}
}