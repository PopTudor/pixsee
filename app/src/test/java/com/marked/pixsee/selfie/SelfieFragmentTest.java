package com.marked.pixsee.selfie;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.PixseeTest;
import com.marked.pixsee.injection.components.AppComponentFake;
import com.marked.pixsee.injection.components.DaggerFakeActivityComponent;
import com.marked.pixsee.injection.components.FakeActivityComponent;
import com.marked.pixsee.injection.modules.FakeActivityModule;
import com.marked.pixsee.main.MainActivity;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.tbruyelle.rxpermissions.ShadowActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import rx.Observable;

import static org.mockito.Matchers.anyString;

/**
 * Created by tudor on 10.08.2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,sdk = 23,application = PixseeTest.class)
public class SelfieFragmentTest extends SelfieFragment {
	String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
	// Robolectric Controllers
	private SupportFragmentController<SelfieFragment> fragmentController;
	@Mock
	Context mContext;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ShadowActivity shadowActivity = new ShadowActivity();
				shadowActivity.onRequestPermissionsResult(0,permissions, new int[]{PackageManager.PERMISSION_DENIED});
				return null;
			}
		}).when(mContext).startActivity(Mockito.any(Intent.class));
		Mockito.doReturn(mContext).when(mContext).getApplicationContext();
		RxPermissions.getInstance(mContext);
		Mockito.doReturn(false).when(Mockito.spy(RxPermissions.getInstance(mContext))).isGranted(anyString());
		Mockito.doReturn(Observable.just(false)).when(Mockito.spy(RxPermissions.getInstance(mContext))).request(anyString());

		SelfieFragment selfieFragment = new SelfieFragmentTest();
		// Robolectric Controllers
		fragmentController = SupportFragmentController.of(selfieFragment, MainActivity.class).attach();
	}

	@Test
	public void havingPermissions_shouldInjectPresenter() throws Exception {
		// operate
		fragmentController.create();
		// check
		Assert.assertNotNull("Presenter is not injected", fragmentController.get().mFacePresenter);
	}

	@Test(expected = ClassCastException.class)
	public void onAttachException() throws Exception {
		SupportFragmentController.of(SelfieFragment.newInstance(), FragmentActivity.class).attach().create();
	}

	@Override
	public void injectComponent() {
		FakeActivityComponent activityComponent = DaggerFakeActivityComponent.builder()
				.appComponentFake((AppComponentFake) ((PixseeTest) getActivity().getApplication()).getAppComponent())
				.fakeActivityModule(new FakeActivityModule((AppCompatActivity) getActivity()))
				.build();

		DaggerFakeSelfieComponent.builder()
				.fakeActivityComponent(activityComponent)
				.fakeSelfieModule(new FakeSelfieModule())
				.build()
				.inject(this);
	}

}