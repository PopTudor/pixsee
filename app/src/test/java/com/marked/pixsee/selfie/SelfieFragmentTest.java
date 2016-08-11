package com.marked.pixsee.selfie;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.TestSchedulerProxy;
import com.marked.pixsee.injection.components.FakeActivityComponent;
import com.marked.pixsee.main.MainActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

/**
 * Created by tudor on 10.08.2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SelfieFragmentTest extends SelfieFragment {
    private SelfieFragment selfieFragment;
    private SupportFragmentController<SelfieFragment> fragmentController;


    @Before
    public void setUp() throws Exception {
        TestSchedulerProxy.get();
        selfieFragment = SelfieFragment.newInstance();
        fragmentController = SupportFragmentController.of(selfieFragment, MainActivity.class).create();
    }

    @Test
    public void presenterNotNull() throws Exception {
        Assert.assertNotNull("Presenter is not injected", fragmentController.get().mFacePresenter);
    }

    @Test
    public void cameraPreviewNotNull() throws Exception {
        fragmentController.attach().visible().start();
        Assert.assertNotNull("CameraPreview is not injected", selfieFragment.mCameraPreview);
    }

    @Override
    public void injectComponent() {
        DaggerFakeSelfieComponent.builder().fakeActivityComponent(Mockito.mock(FakeActivityComponent.class))
                .fakeSelfieModule(new FakeSelfieModule())
                .build()
                .inject(this);
    }
}