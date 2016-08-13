package com.marked.pixsee.selfie;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.SparseArray;
import android.view.TextureView;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
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
    TextureView.SurfaceTextureListener provideCameraTexture(SelfieContract.Presenter presenter){
        return new SelfieFragment.CameraAvailable(presenter);
    }

    @Provides
    @FragmentScope
    SurfaceTexture.OnFrameAvailableListener provideOnFrameAvailableListener() {
        return Mockito.mock(SurfaceTexture.OnFrameAvailableListener.class);
    }

    @Provides
    @FragmentScope
    CameraSource provideCameraSource(Context context, Detector<Face> faceDetector) {
        return Mockito.mock(CameraSource.class);
    }

    @Provides
    @FragmentScope
    SelfieContract.Presenter provideFacePresenter(Renderer renderer, CameraSource source) {
        return Mockito.mock(SelfieContract.Presenter.class);
    }

    @Provides
    @FragmentScope
    Detector<Face> provideFaceDetector(Context context, SelfieTrackerAR selfieTrackerAR) {
        return Mockito.mock(FakeFaceDetector.class);
    }
    private class FakeFaceDetector extends Detector<Face>{
        @Override
        public SparseArray<Face> detect(Frame frame) {
            return null;
        }
    }

    @Provides
    @FragmentScope
    SelfieTrackerAR provideSelfieTrackerAR() {
        return Mockito.mock(SelfieTrackerAR.class);
    }
}