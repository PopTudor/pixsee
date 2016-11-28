package com.marked.pixsee.ui.selfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.marked.pixsee.Pixsee;
import com.marked.pixsee.R;
import com.marked.pixsee.injection.Injectable;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.injection.modules.ActivityModule;

import org.pixsee.CameraFragment;
import org.pixsee.CameraView;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

import static com.marked.pixsee.ui.selfie.PictureDetailShareFragment.OnPictureDetailShareListener;

public class SelfieFragment extends CameraFragment implements OnPictureDetailShareListener, SelfieContract.View, Injectable {
	private static final String TAG = SelfieFragment.class + "***";
	private ViewGroup mBottomLayout;
	private OnSelfieInteractionListener mOnSelfieInteractionListener;
	@Inject
	SelfieContract.Presenter mFacePresenter;
	@Inject
	@Named(value = "cameraTexture")
	TextureView.SurfaceTextureListener mCameraTextureAvailable;

	public SelfieFragment() {
		super();
	}

	public static SelfieFragment newInstance() {
		Bundle args = new Bundle();
		SelfieFragment fragment = new SelfieFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		injectComponent();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_face, container, false);
		mBottomLayout = (ViewGroup) view.findViewById(R.id.bottomLayout);
//		FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.previewContainer);
//		frameLayout.addView(mCameraView);
		mCameraView = (CameraView) view.findViewById(R.id.cameraView);

		return view;
	}

	/**
	 * Restarts the camera.
	 */
	@Override
	public void onResume() {
		super.onResume();
//		setTextureIfAvailable();
		mFacePresenter.resumeSelfie();
		/* if we resume and the user had the PictureAction screen, make him take another picture*/
		if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
			getActivity().getSupportFragmentManager().popBackStack();
	}


	//	/**
//	 * if screen was locked cameraTextureview will not be recreated since we have one
//	 */
//	private void setTextureIfAvailable() {
//		if (mCameraTextureview.isAvailable())
//			mCameraTextureAvailable.onSurfaceTextureAvailable(mCameraTextureview.getSurfaceTexture(), mCameraTextureview.getWidth(), mCameraTextureview.getHeight());
//	}

	@Override
	public void showTakenPictureActions() {
		mOnSelfieInteractionListener.showTakenPictureActions();
	}

	@Override
	public void displayEmojiActions(boolean showSelfieActions) {
		if (showSelfieActions)
			mBottomLayout.setVisibility(View.VISIBLE);
		else
			mBottomLayout.setVisibility(View.GONE);
	}

	/**
	 * Releases the resources associated with the camera source, the associated detector, and the
	 * rest of the processing pipeline.
	 */
	@Override
	public void onPause() {
		super.onPause();

		mFacePresenter.release();
		Log.d(TAG, "onPause: ");
	}

	@Override
	public void setPresenter(SelfieContract.Presenter presenter) {
		this.mFacePresenter = presenter;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mOnSelfieInteractionListener.selfieFragmentDesroyed();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mOnSelfieInteractionListener = (OnSelfieInteractionListener) context;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mOnSelfieInteractionListener = null;
	}

	public Observable<Bitmap> getPicture() {
//		return Observable.just(mCameraView.getDrawingCache()).subscribeOn(Schedulers.computation());
		return Observable.empty();
	}

	//==============================================================================================
	// Camera Source Preview
	//==============================================================================================

	@Override
	public void resumeSelfie() {
//		if (mCameraTextureview.isAvailable())
//			mFacePresenter.resumeSelfie();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setupListeners(view);
	}

	private void setupListeners(@NonNull View view) {
		view.findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View cameraButton) {
				mFacePresenter.takePicture();
			}
		});
	}

	@Override
	public void showCameraErrorDialog() {
		new AlertDialog.Builder(getActivity())
				.setCancelable(true)
				.setTitle("Camera Error")
				.setMessage("Camera could not be opened. Please restart your phone and try again !")
				.show();
	}

	@Override
	public void injectComponent() {
		ActivityComponent daggerActivityComponent = DaggerActivityComponent.builder()
				                                            .sessionComponent(Pixsee.getSessionComponent())
				                                            .activityModule(new ActivityModule((AppCompatActivity) getActivity()))
				                                            .build();
		SelfieComponent mSelfieComponent = DaggerSelfieComponent.builder()
				                                   .selfieModule(new SelfieModule(this))
				                                   .activityComponent(daggerActivityComponent)
				                                   .build();
		mSelfieComponent.inject(this);
	}

	public interface OnSelfieInteractionListener {
		void showTakenPictureActions();

		void selfieFragmentDesroyed();

		Observable<Bitmap> getPicture();
	}

	public static class CameraTextureAvailable implements TextureView.SurfaceTextureListener {
		private SelfieContract.Presenter mFacePresenter;

		CameraTextureAvailable(SelfieContract.Presenter facePresenter) {
			mFacePresenter = facePresenter;
		}

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			mFacePresenter.onAvailableCameraSurfaceTexture(surface, width, height);
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			return false;
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {

		}
	}
}