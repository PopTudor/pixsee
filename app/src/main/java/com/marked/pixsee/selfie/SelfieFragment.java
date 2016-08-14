package com.marked.pixsee.selfie;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseIntArray;
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
import com.marked.pixsee.main.MainActivity;
import com.marked.pixsee.selfie.commands.FavOneClick;
import com.marked.pixsee.selfie.commands.FavThreeClick;
import com.marked.pixsee.selfie.commands.FavTwoClick;
import com.marked.pixsee.selfie.renderer.RenderSurfaceView;
import com.marked.pixsee.selfie.data.SelfieObject;
import com.marked.pixsee.utility.Permissions;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.functions.Action1;

import static com.marked.pixsee.selfie.PictureDetailShareFragment.OnPictureDetailShareListener;

public class SelfieFragment extends Fragment implements OnPictureDetailShareListener, SelfieContract.View, Injectable, Permissions {
	public static final String PHOTO_EXTRA = "PHOTO";
	public static final String PHOTO_RENDERER_EXTRA = "PHOTO_RENDERER";
	private static final String TAG = SelfieFragment.class + "***";
	private static final int RC_HANDLE_CAMERA_PERM = 2;
	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

	@Inject
	SelfieContract.Presenter mFacePresenter;

	@Inject
	@Named(value = "cameraTexture")
	TextureView.SurfaceTextureListener mCameraTextureAvailable;


	private RenderSurfaceView mRendererSurfaceView;

	private TextureView mCameraTextureview;

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
				.toBlocking()
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
		View rootView = inflater.inflate(R.layout.activity_face, container, false);

		mBottomLayout = (ViewGroup) rootView.findViewById(R.id.bottomLayout);
		mCameraTextureview = (TextureView) rootView.findViewById(R.id.camera_texture);
		mRendererSurfaceView = (RenderSurfaceView) rootView.findViewById(R.id.renderer_texture);
		mRendererSurfaceView.setTransparent(true);


		mCameraTextureview.setSurfaceTextureListener(mCameraTextureAvailable);
		mRendererSurfaceView.setSurfaceRenderer(mFacePresenter.getRenderer());
		return rootView;
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

	private void setupListeners(@NotNull View view) {
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

	public static class CameraTextureAvailable implements TextureView.SurfaceTextureListener {
		private SelfieContract.Presenter mFacePresenter;

		CameraTextureAvailable(SelfieContract.Presenter facePresenter) {
			mFacePresenter = facePresenter;
		}

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			mFacePresenter.onAvailableCameraSurfaceTexture(surface,width,height);
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
}