package com.marked.pixsee.selfie;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.marked.pixsee.R;
import com.marked.pixsee.selfie.commands.FavOneClick;
import com.marked.pixsee.selfie.commands.FavThreeClick;
import com.marked.pixsee.selfie.commands.FavTwoClick;
import com.marked.pixsee.selfie.custom.CameraPreview;
import com.marked.pixsee.selfie.custom.CameraSource;
import com.marked.pixsee.selfie.custom.SelfieRenderer;
import com.marked.pixsee.selfie.data.SelfieObject;
import com.marked.pixsee.selfie.di.DaggerSelfieComponent;
import com.marked.pixsee.selfie.di.SelfieComponent;
import com.marked.pixsee.selfie.di.SelfieModule;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.rajawali3d.view.TextureView;

import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.marked.pixsee.selfie.DetailFragment.OnDetailInteractionListener;

public class SelfieFragment extends Fragment implements OnDetailInteractionListener,FaceContract.View {
	private static final String TAG = SelfieFragment.class + "***";
	public static final String PHOTO_EXTRA = "PHOTO";
	public static final String PHOTO_RENDERER_EXTRA = "PHOTO_RENDERER";
	private static final int RC_HANDLE_CAMERA_PERM = 2;

	@Inject
	FaceContract.Presenter mFacePresenter;

	@Inject
	CameraSource mCameraSource;
	private CameraPreview mCameraPreview;

	@Inject
	SelfieRenderer mSelfieRenderer;
	private TextureView mFaceTextureView;
	private ViewGroup mBottomLayout;

	private ImageButton mCameraButton;

	private SelfieComponent mSelfieComponent;
	private SelfieRenderer.FaceRendererCallback faceRendererCallback = new SelfieRenderer.FaceRendererCallback() {
		@Override
		public void onTextureAvailable(SurfaceTexture texture) {
			mCameraPreview.setSurfaceTexture(texture);
			startCameraSource();
		}
	};

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
		// DI
		mSelfieComponent = DaggerSelfieComponent.builder()
				.selfieModule(new SelfieModule(this))
				.build();
		mSelfieComponent.inject(this);

		// Must be done during an initialization phase like onCreate
		RxPermissions.getInstance(getActivity())
				.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
				.subscribe(new Action1<Boolean>() {
					@Override
					public void call(Boolean granted) {
						if (granted) { // Always true pre-M
							// I can control the camera now
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
		mCameraPreview = (CameraPreview) rootView.findViewById(R.id.preview);
		mBottomLayout = (ViewGroup) rootView.findViewById(R.id.bottomLayout);

		mFaceTextureView = (TextureView) rootView.findViewById(R.id.texture_view);
		mFaceTextureView.setEGLContextClientVersion(2);
		mFaceTextureView.setSurfaceRenderer(mSelfieRenderer); /* the surface where the renderer should display it's scene*/
		mSelfieRenderer.setCallback(faceRendererCallback);
		mFaceTextureView.setFrameRate(60);

		rootView.findViewById(R.id.favorite1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFacePresenter.execute(new FavOneClick(getContext(), mSelfieRenderer));
			}
		});
		rootView.findViewById(R.id.favorite2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFacePresenter.execute(new FavTwoClick(getContext(), mSelfieRenderer));
			}
		});
		rootView.findViewById(R.id.favorite3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFacePresenter.execute(new FavThreeClick(getContext(), mSelfieRenderer));
			}
		});

		mCameraButton = (ImageButton) rootView.findViewById(R.id.camera_button);
		mCameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View cameraButton) {
				mFacePresenter.displayActions(false);
				mCameraSource.takePicture(mFacePresenter, new CameraSource.PictureCallback() {
					@Override
					public void onPictureTaken(final byte[] bytes) {
						mCameraPreview.stop(); /* camera needs to be frozen after it took the picture*/
					}
				});
			}
		});
		return rootView;
	}

	/**
	 * Restarts the camera.
	 */
	@Override
	public void onResume() {
		super.onResume();
		mSelfieComponent.inject(mCameraSource);

		startCameraSource();
		/* if we resume and the user had the fragment screen, make him take another picture*/
		if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
			getActivity().getSupportFragmentManager().popBackStack();
	}

	@Override
	public void showTakenPictureActions() {
		mOnSelfieInteractionListener.showTakenPictureActions();
	}

	@Override
	public void displayActions(boolean showSelfieActions) {
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
		mCameraPreview.release();
		Log.d(TAG, "onPause: ");
	}

	@Override
	public void setPresenter(FaceContract.Presenter presenter) {
		this.mFacePresenter = presenter;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mOnSelfieInteractionListener.selfieFragmentDesroyed();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mOnSelfieInteractionListener = (OnSelfieInteractionListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString() + " must implement OnArticleSelectedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mOnSelfieInteractionListener = null;
	}


	@NonNull
	@Override
	public Observable<Bitmap> onButtonClicked() {
		return Observable.just(mFaceTextureView.getBitmap()).subscribeOn(Schedulers.computation());
	}

	@Override
	public void hiddenDetailPictureActions() {
		mFacePresenter.displayActions(true);
		startCameraSource();
	}

	//==============================================================================================
	// Camera Source Preview
	//==============================================================================================

	/**
	 * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
	 * (e.g., because onResume was called before the camera source was created), this will be called
	 * again when the camera source is created.
	 */
	private void startCameraSource() {
		try {
			mCameraPreview.start(mCameraSource, mSelfieRenderer);
		} catch (IOException e) {
			Log.e(TAG, "Unable to start camera source.", e);
			mCameraPreview.stop();
			mCameraSource = null;
		}
	}
	// ===============================================================================================
	// Interaction listener interfaces
	// ===============================================================================================
	public interface OnFavoritesListener {
		void onFavoriteClicked(SelfieObject object);
	}
	public interface OnSelfieInteractionListener{
		void showTakenPictureActions();

		void selfieFragmentDesroyed();
	}
	private OnSelfieInteractionListener mOnSelfieInteractionListener;

	public interface SelfieTakePicture {
		void onCameraClick();
	}
}