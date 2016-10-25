package com.marked.pixsee.ui.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.Pixsee;
import com.marked.pixsee.UserUtilTest;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.injection.components.SessionComponent;
import com.marked.pixsee.injection.modules.ActivityModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentController;

/**
 * Created by Tudor on 13-Oct-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, application = Pixsee.class)
public class ChatFragmentTest extends ChatFragment {
	// Robolectric Controllers
	private FragmentController<ChatFragmentTest> fragmentController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		// Robolectric Controllers
		fragmentController = FragmentController.of(this, ChatActivity.class);
		Intent intent = new Intent();
		intent.putExtra(ChatActivity.EXTRA_CONTACT, UserUtilTest.getUserTest());
		fragmentController.withIntent(intent);
	}

	@Test
	public void testHasIntent() throws Exception {


	}

	@Override
	public void injectComponent() {
		DaggerChatComponent.builder().activityComponent(((ChatActivity) getActivity()).getActivityComponent())
				.chatModule(new ChatModule(this, UserUtilTest.getUserTest()))
				.build()
				.inject(this);
		SessionComponent appComponent = Pixsee.getSessionComponent();
		ActivityComponent activityComponent = DaggerActivityComponent.builder()
				                                      .sessionComponent(appComponent)
				                                      .activityModule(new ActivityModule((AppCompatActivity) getActivity()))
				                                          .build();
		DaggerChatComponent.builder()
				.activityComponent(activityComponent)
				.chatModule(new ChatModule(this, UserUtilTest.getUserTest()))
				.build();
	}
}
