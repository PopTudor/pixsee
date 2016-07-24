package com.marked.pixsee.main;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.injection.modules.FakeActivityModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

/**
 * Created by Tudor on 21-Jul-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class MainActivityTest {
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
		Robolectric.reset();
	}

	@Test
	public void testOnCreate() throws Exception {
		ActivityController<MainActivity> mainActivity = Robolectric.buildActivity(MainActivity.class);
		mainActivity.create();
		Mockito.verify(mainActivity.get().mPresenter).attach();
	}

	@Test
	public void testOnCreateRxBusShouldStartMainActivity() throws Exception {

//		RxBus.getInstance().post(new FriendRequestEvent());
		
	}


	@Test
	public void testOnStartFriendRequestShouldSendToPresenterAUser() throws Exception {

	}

	private static final class MainActivity extends com.marked.pixsee.main.MainActivity {
		@Override
		protected void injectComponent() {
			DaggerFakeMainComponent.builder()
					.fakeActivityModule(new FakeActivityModule(this))
					.fakeMainModule(new FakeMainModule())
					.build()
					.inject(this);
		}
	}
}