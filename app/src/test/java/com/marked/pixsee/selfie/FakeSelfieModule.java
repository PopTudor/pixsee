package com.marked.pixsee.selfie;

import android.content.Context;
import android.graphics.SurfaceTexture;

import com.google.android.gms.vision.face.FaceDetector;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.selfie.custom.CameraSource;
import com.marked.pixsee.selfie.custom.SelfieTrackerAR;

import org.mockito.Mockito;
import org.rajawali3d.renderer.Renderer;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tudor on 10.08.2016.
 */
@Module
class FakeSelfieModule {

    @Provides
    @FragmentScope
    Renderer provideFaceRenderer(Context context, SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener, CameraSource source, SelfieTrackerAR selfieTrackerAR) {
        return Mockito.mock(Renderer.class);
    }

    @Provides
    @FragmentScope
    SurfaceTexture.OnFrameAvailableListener provideOnFrameAvailableListener() {
        return Mockito.mock(SurfaceTexture.OnFrameAvailableListener.class);
    }

    @Provides
    @FragmentScope
    CameraSource provideCameraSource(Context context, FaceDetector faceDetector) {
        return Mockito.mock(CameraSource.class);
    }

    @Provides
    @FragmentScope
    SelfieContract.Presenter provideFacePresenter(Renderer renderer, CameraSource source) {
        return Mockito.mock(SelfieContract.Presenter.class);
    }

    @Provides
    @FragmentScope
    FaceDetector provideFaceDetector(Context context, SelfieTrackerAR selfieTrackerAR) {
        return Mockito.mock(FaceDetector.class);
    }

    @Provides
    @FragmentScope
    SelfieTrackerAR provideSelfieTrackerAR() {
        return Mockito.mock(SelfieTrackerAR.class);
    }
}