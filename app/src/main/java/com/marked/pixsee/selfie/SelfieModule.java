package com.marked.pixsee.selfie;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.selfie.custom.CameraSource;
import com.marked.pixsee.selfie.custom.SelfieRenderer;
import com.marked.pixsee.selfie.custom.SelfieTrackerAR;

import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.renderer.Renderer;

import java.lang.ref.WeakReference;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 4/24/2016.
 */
@Module
@FragmentScope
class SelfieModule {
    private WeakReference<SelfieContract.View> mSelfieFragment;

    SelfieModule(SelfieContract.View selfieFragment) {
        this.mSelfieFragment = new WeakReference<>(selfieFragment);
    }

    @Provides
    @FragmentScope
    Renderer provideFaceRenderer(Context context, SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener,
                                 CameraSource source, SelfieTrackerAR selfieTrackerAR) {
        StreamingTexture streamingTexture = new StreamingTexture("stream", source.getCamera(), onFrameAvailableListener);
        SelfieRenderer selfieRenderer = new SelfieRenderer(context, streamingTexture);
        selfieTrackerAR.setTrackerCallback(selfieRenderer);
        return selfieRenderer;
    }

    @Provides
    @FragmentScope
    SurfaceTexture.OnFrameAvailableListener provideOnFrameAvailableListener() {
        return new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                mSelfieFragment.get().setSurfaceTexture(surfaceTexture);
            }
        };
    }

    @Provides
    @FragmentScope
    CameraSource provideCameraSource(Context context, FaceDetector faceDetector) {
        return new CameraSource.Builder(context, faceDetector).setRequestedFps(60.0f)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO)
                .setFacing(com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT)
                .build();
    }

    @Provides
    @FragmentScope
    SelfieContract.Presenter provideFacePresenter(Renderer renderer, CameraSource source) {
        return new FacePresenter(mSelfieFragment.get(), renderer, source);
    }

    @Provides
    @FragmentScope
    FaceDetector provideFaceDetector(Context context, SelfieTrackerAR selfieTrackerAR) {
        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setTrackingEnabled(true)
                .setProminentFaceOnly(true)
                .setClassificationType(FaceDetector.FAST_MODE)
                .setLandmarkType(0)
                .build();

        faceDetector.setProcessor(new LargestFaceFocusingProcessor.Builder(faceDetector, selfieTrackerAR).build());
        if (!faceDetector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The faceDetector will automatically become operational once the library
            // download completes on device.
        }
        return faceDetector;
    }

    @Provides
    @FragmentScope
    SelfieTrackerAR provideSelfieTrackerAR() {
        return new SelfieTrackerAR();
    }
}
