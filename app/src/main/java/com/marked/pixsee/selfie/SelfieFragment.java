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
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.marked.pixsee.Pixsee;
import com.marked.pixsee.R;
import com.marked.pixsee.injection.Injectable;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.main.MainActivity;
import com.marked.pixsee.selfie.custom.AutofitTextureView;
import com.marked.pixsee.selfie.custom.CameraPreview;
import com.marked.pixsee.selfie.custom.CameraSource;
import com.marked.pixsee.selfie.data.SelfieObject;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.rajawali3d.renderer.Renderer;

import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

import static com.marked.pixsee.selfie.PictureDetailShareFragment.OnPictureDetailShareListener;

public class SelfieFragment extends Fragment implements OnPictureDetailShareListener, SelfieContract.View, Injectable {
	public static final String PHOTO_EXTRA = "PHOTO";
	public static final String PHOTO_RENDERER_EXTRA = "PHOTO_RENDERER";
	private static final String TAG = SelfieFragment.class + "***";
	private static final int RC_HANDLE_CAMERA_PERM = 2;
	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

	static {
		ORIENTATIONS.append(Surface.ROTATION_0, 90);
		ORIENTATIONS.append(Surface.ROTATION_90, 0);
		ORIENTATIONS.append(Surface.ROTATION_180, 270);
		ORIENTATIONS.append(Surface.ROTATION_270, 180);
	}

	@Inject
	SelfieContract.Presenter mFacePresenter;

	CameraPreview mCameraPreview;

	private AutofitTextureView mFaceTextureView;
	private ViewGroup mBottomLayout;

	private ImageButton mCameraButton;
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
		injectComponent();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_face, container, false);

		mCameraPreview = (CameraPreview) rootView.findViewById(R.id.preview);
		mBottomLayout = (ViewGroup) rootView.findViewById(R.id.bottomLayout);
//		mFaceTextureView = (AutofitTextureView) rootView.findViewById(R.id.texture_view);
		mCameraButton = (ImageButton) rootView.findViewById(R.id.camera_button);


//		mFaceTextureView.setEGLContextClientVersion(2);
//		mFaceTextureView.setSurfaceRenderer(mSelfieRenderer); /* the surface where the renderer should display it's scene*/

		rootView.findViewById(R.id.favorite1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				mFacePresenter.execute(new FavOneClick(getContext()));
			}
		});
		rootView.findViewById(R.id.favorite2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				mFacePresenter.execute(new FavTwoClick(getContext()));
			}
		});
		rootView.findViewById(R.id.favorite3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				mFacePresenter.execute(new FavThreeClick(getContext()));
			}
		});

		mCameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View cameraButton) {
				mFacePresenter.takePicture();
			}
		});
		return rootView;
	}

	@Override
	public void stopCamera() {
		mCameraPreview.stop();
	}

	/**
	 * Restarts the camera.
	 */
	@Override
	public void onResume() {
		super.onResume();

		if (mFacePresenter != null)
			mFacePresenter.resumeSelfie();
		/* if we resume and the user had the PictureAction screen, make him take another picture*/
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

	@Override
	public void setSurfaceTexture(SurfaceTexture texture) {
		mCameraPreview.setSurfaceTexture(texture);
	}

	/**
	 * Releases the resources associated with the camera source, the associated detector, and the
	 * rest of the processing pipeline.
	 */
	@Override
	public void onPause() {
		super.onPause();
		if (mCameraPreview != null)
			mCameraPreview.release();
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

	public Observable<Bitmap> getPicture() {
//		return Observable.just(mFaceTextureView.getBitmap()).subscribeOn(Schedulers.computation());
		return Observable.empty();
	}

	//==============================================================================================
	// Camera Source Preview
	//==============================================================================================

	@Override
	public void resumeSelfie() {
		mFacePresenter.resumeSelfie();
	}

	/**
	 * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
	 * (e.g., because onResume was called before the camera source was created), this will be called
	 * again when the camera source is created.
	 */
	@Override
	public void startCamera(CameraSource source, Renderer renderer) {
		try {
			mCameraPreview.start(source, renderer);
		} catch (IOException e) {
			Log.e(TAG, "Unable to start camera source.", e);
			mCameraPreview.stop();
		}
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
	// ===============================================================================================
	// Interaction listener interfaces
	// ===============================================================================================
	public interface OnFavoritesListener {
		void onFavoriteClicked(SelfieObject object);
	}
	public interface OnSelfieInteractionListener{
		void showTakenPictureActions();

		void selfieFragmentDesroyed();

		Observable<Bitmap> getPicture();
	}
}