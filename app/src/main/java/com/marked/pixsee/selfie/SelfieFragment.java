package com.marked.pixsee.selfie;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.gms.common.images.Size;
import com.marked.pixsee.Pixsee;
import com.marked.pixsee.R;
import com.marked.pixsee.main.MainActivity;
import com.marked.pixsee.selfie.commands.FavOneClick;
import com.marked.pixsee.selfie.commands.FavThreeClick;
import com.marked.pixsee.selfie.commands.FavTwoClick;
import com.marked.pixsee.selfie.data.SelfieObject;
import com.marked.pixsee.selfie.renderer.RenderSurfaceView;
import com.marked.pixsee.utility.Permissions;
import com.tbruyelle.rxpermissions.RxPermissions;

import javax.inject.Inject;
import javax.inject.Named;

import camerasource.CameraTextureView;
import dependencyInjection.Injectable;
import dependencyInjection.components.ActivityComponent;
import dependencyInjection.components.DaggerActivityComponent;
import rx.Observable;
import rx.functions.Action1;

import static com.marked.pixsee.selfie.PictureDetailShareFragment.OnPictureDetailShareListener;

public class SelfieFragment extends Fragment implements OnPictureDetailShareListener, SelfieContract.View, Injectable, Permissions {
	private static final String TAG = SelfieFragment.class + "***";

	@Inject
	SelfieContract.Presenter mFacePresenter;

	@Inject
	@Named(value = "cameraTexture")
	TextureView.SurfaceTextureListener mCameraTextureAvailable;


	private RenderSurfaceView mRendererSurfaceView;

	private CameraTextureView mCameraTextureview;

	private ViewGroup mBottomLayout;
	private OnSelfieInteractionListener mOnSelfieInteractionListener;

	public SelfieFragment() {
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

		// Must be done during an initialization phase like onCreate
		RxPermissions()
				.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
				.subscribe(new Action1<Boolean>() {
					@Override
					public void call(Boolean granted) {
						if (granted) { // Always true pre-M
							// I can control the camera now
							injectComponent();
						} else {
							// Oups permission denied
							getActivity().onBackPressed();
						}
					}
				});
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_face, container, false);
		mBottomLayout = (ViewGroup) view.findViewById(R.id.bottomLayout);
		mCameraTextureview = (CameraTextureView) view.findViewById(R.id.camera_texture);
		mCameraTextureview.setSurfaceTextureListener(mCameraTextureAvailable);
//
		mRendererSurfaceView = (RenderSurfaceView) view.findViewById(R.id.renderer_texture);
		mRendererSurfaceView.setTransparent(true);
		mRendererSurfaceView.setSurfaceRenderer(mFacePresenter.getRenderer());

		return view;
	}

	/**
	 * Restarts the camera.
	 */
	@Override
	public void onResume() {
		super.onResume();
		setTextureIfAvailable();
		mFacePresenter.resumeSelfie();
		/* if we resume and the user had the PictureAction screen, make him take another picture*/
		if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
			getActivity().getSupportFragmentManager().popBackStack();
	}

	/**
	 * if screen was locked cameraTextureview will not be recreated since we have one
	 */
	private void setTextureIfAvailable() {
		if (mCameraTextureview.isAvailable())
			mCameraTextureAvailable.onSurfaceTextureAvailable(mCameraTextureview.getSurfaceTexture(), mCameraTextureview.getWidth(), mCameraTextureview.getHeight());
	}

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
//		return Observable.just(mRendererSurfaceView.getBitmap()).subscribeOn(Schedulers.computation());
		return Observable.empty();
	}

	//==============================================================================================
	// Camera Source Preview
	//==============================================================================================

	@Override
	public void resumeSelfie() {
		if (mCameraTextureview.isAvailable())
			mFacePresenter.resumeSelfie();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setupListeners(view);
	}

	private void setupListeners(@NonNull View view) {
		view.findViewById(R.id.favorite1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFacePresenter.execute(new FavOneClick(getContext(), (OnFavoritesListener) mFacePresenter.getRenderer()));
			}
		});
		view.findViewById(R.id.favorite2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFacePresenter.execute(new FavTwoClick(getContext(), (OnFavoritesListener) mFacePresenter.getRenderer()));
			}
		});
		view.findViewById(R.id.favorite3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFacePresenter.execute(new FavThreeClick(getContext(), (OnFavoritesListener) mFacePresenter.getRenderer()));
			}
		});
		view.findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View cameraButton) {
				mFacePresenter.takePicture();
			}
		});
	}

	@Override
	public void setCameraTextureViewSize(Size size) {
		mCameraTextureview.setPreviewSize(size);
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
				                                            .appComponent(((Pixsee) getActivity().getApplication()).getAppComponent())
				                                            .activityModule(((MainActivity) getActivity()).activityModule).build();
		SelfieComponent mSelfieComponent = DaggerSelfieComponent.builder()
				                                   .selfieModule(new SelfieModule(this))
				                                   .activityComponent(daggerActivityComponent)
				                                   .build();
		mSelfieComponent.inject(this);
	}

	@Override
	public RxPermissions RxPermissions() {
		return RxPermissions.getInstance(getActivity());
	}

	// ===============================================================================================
	// Interaction listener interfaces
	// ===============================================================================================
	public interface OnFavoritesListener {
		void onFavoriteClicked(SelfieObject object);
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