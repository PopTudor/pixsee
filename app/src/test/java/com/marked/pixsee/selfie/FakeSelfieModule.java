package com.marked.pixsee.selfie;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.SparseArray;
import android.view.TextureView;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.selfie.camerasource.CameraSource;
import com.marked.pixsee.selfie.renderer.SelfieRenderer;
import com.marked.pixsee.selfie.renderer.SelfieTrackerAR;

import org.mockito.Mockito;
import org.rajawali3d.renderer.Renderer;

import javax.inject.Named;

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
        return Mockito.mock(SelfieRenderer.class);
    }
    @Provides
    @FragmentScope
    @Named(value = "cameraTexture")
    TextureView.SurfaceTextureListener provideCameraTexture(SelfieContract.Presenter presenter){
        return Mockito.mock(TextureView.SurfaceTextureListener.class);
    }

    @Provides
    @FragmentScope
    @Named(value = "renderTexture")
    TextureView.SurfaceTextureListener provideRenderTexture(SelfieContract.Presenter presenter){
        return Mockito.mock(TextureView.SurfaceTextureListener.class);
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
        return Mockito.mock(FacePresenter.class);
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