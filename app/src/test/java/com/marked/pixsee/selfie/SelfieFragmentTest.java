package com.marked.pixsee.selfie;

import android.Manifest;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.PixseeTest;
import com.marked.pixsee.di.components.DaggerFakeActivityComponent;
import com.marked.pixsee.di.components.FakeActivityComponent;
import com.marked.pixsee.di.components.FakeAppComponent;
import com.marked.pixsee.di.modules.FakeActivityModule;
import com.marked.pixsee.main.MainActivity;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import rx.Observable;

import static org.mockito.Mockito.doReturn;

/**
 * Created by tudor on 10.08.2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,sdk = 23,application = PixseeTest.class)
public class SelfieFragmentTest extends SelfieFragment {
	// Robolectric Controllers
	private SupportFragmentController<SelfieFragmentTest> fragmentController;
	String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
	@Mock
	RxPermissions rxPermissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		// Robolectric Controllers
		fragmentController = SupportFragmentController.of(this, MainActivity.class).attach();
	}

	@Test(expected = ClassCastException.class)
	public void onAttachException() throws Exception {
		SupportFragmentController.of(SelfieFragment.newInstance(), FragmentActivity.class).attach().create();
	}

	@Test
	public void havingPermissions_shouldInjectPresenter() throws Exception {
		//build
		doReturn(Observable.just(true)).when(rxPermissions).request(permissions);
		// operate
		fragmentController.create();
		// check
		Assert.assertNotNull("Presenter is not injected", fragmentController.get().mFacePresenter);
	}

	@Override
	public void injectComponent() {
		FakeActivityComponent activityComponent = DaggerFakeActivityComponent.builder()
				                                          .fakeAppComponent((FakeAppComponent) ((PixseeTest) getActivity().getApplication())
						                                                                               .getAppComponent())
				.fakeActivityModule(new FakeActivityModule((AppCompatActivity) getActivity()))
				.build();

		DaggerFakeSelfieComponent.builder()
				.fakeActivityComponent(activityComponent)
				.fakeSelfieModule(new FakeSelfieModule())
				.build()
				.inject(this);
	}

	@Override
	public RxPermissions RxPermissions() {
		return rxPermissions;
	}
}