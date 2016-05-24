package com.marked.pixsee.face;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.marked.pixsee.R;
import com.marked.pixsee.face.commands.FavOneClick;
import com.marked.pixsee.face.commands.FavThreeClick;
import com.marked.pixsee.face.commands.FavTwoClick;
import com.marked.pixsee.face.custom.CameraPreview;
import com.marked.pixsee.face.custom.CameraSource;
import com.marked.pixsee.face.custom.FaceRenderer;
import com.marked.pixsee.face.data.FaceObject;
import com.marked.pixsee.face.di.DaggerSelfieComponent;
import com.marked.pixsee.face.di.SelfieComponent;
import com.marked.pixsee.face.di.SelfieModule;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.rajawali3d.view.TextureView;

import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.marked.pixsee.face.DetailFragment.OnFragmentInteractionListener;

public class SelfieActivity extends AppCompatActivity implements OnFragmentInteractionListener,FaceContract.View {
	private static final String TAG = SelfieActivity.class + "***";
	public static final String PHOTO_EXTRA = "PHOTO";
	public static final String PHOTO_RENDERER_EXTRA = "PHOTO_RENDERER";
	private static final int RC_HANDLE_CAMERA_PERM = 2;

	@Inject
	FaceContract.Presenter mFacePresenter;

	@Inject
	CameraSource mCameraSource;
	private CameraPreview mCameraPreview;

	@Inject
	FaceRenderer mFaceRenderer;
	private TextureView mFaceTextureView;
	private ViewGroup mBottomLayout;

	private ImageButton mCameraButton;

	private SelfieComponent mSelfieComponent;
	private FaceRenderer.FaceRendererCallback faceRendererCallback = new FaceRenderer.FaceRendererCallback() {
		@Override
		public void onTextureAvailable(SurfaceTexture texture) {
			mCameraPreview.setSurfaceTexture(texture);
			startCameraSource();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_face);
		// DI
		mSelfieComponent = DaggerSelfieComponent.builder()
				                   .selfieModule(new SelfieModule(this))
				                   .build();
		mSelfieComponent.inject(this);

		mCameraPreview = (CameraPreview) findViewById(R.id.preview);
		mBottomLayout = (ViewGroup) findViewById(R.id.bottomLayout);

		mFaceTextureView = (TextureView) findViewById(R.id.texture_view);
		mFaceTextureView.setEGLContextClientVersion(2);
		mFaceTextureView.setSurfaceRenderer(mFaceRenderer);
		mFaceRenderer.setCallback(faceRendererCallback);

		findViewById(R.id.favorite1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFacePresenter.execute(new FavOneClick(SelfieActivity.this,mFaceRenderer));
			}
		});
		findViewById(R.id.favorite2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFacePresenter.execute(new FavTwoClick(SelfieActivity.this,mFaceRenderer));
			}
		});
		findViewById(R.id.favorite3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFacePresenter.execute(new FavThreeClick(SelfieActivity.this,mFaceRenderer));
			}
		});
//		findViewById(R.id.shopImageButton).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mFacePresenter.execute(new ShopImageButtonClick(SelfieActivity.this));
//			}
//		});

		mCameraButton = (ImageButton) findViewById(R.id.camera_button);
		mCameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View cameraButton) {
				cameraButton.setEnabled(false); /* disable the button because if the user double tapps the camera button(impatience), it would crash the app*/
				mCameraSource.takePicture(mFacePresenter, new CameraSource.PictureCallback() {
					@Override
					public void onPictureTaken(final byte[] bytes) {
						mCameraPreview.stop(); /* camera needs to be frozen after it took the picture*/
					}
				});
			}
		});
		getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				switch (getSupportFragmentManager().getBackStackEntryCount()) {
					case 0:
						mBottomLayout.setVisibility(View.VISIBLE);
						mCameraButton.setEnabled(true);
						startCameraSource();
						break;
					case 1:

						mBottomLayout.setVisibility(View.GONE);
						break;
				}
			}
		});
		// Must be done during an initialization phase like onCreate
		RxPermissions.getInstance(this)
				.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
				.subscribe(new Action1<Boolean>() {
					@Override
					public void call(Boolean granted) {
						if (granted) { // Always true pre-M
							mSelfieComponent.inject(mCameraSource);
							// I can control the camera now
						} else {
							// Oups permission denied
							finish();
						}
					}
				});
	}

	/**
	 * Restarts the camera.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		startCameraSource();
		/* if we resume and the user had the fragment screen, make him take another picture*/
		if (getSupportFragmentManager().getBackStackEntryCount() > 0)
			getSupportFragmentManager().popBackStack();
	}

	/**
	 * Releases the resources associated with the camera source, the associated detector, and the
	 * rest of the processing pipeline.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mCameraPreview.stop();
		Log.d(TAG, "onPause: ");
	}

	@Override
	public void onShutter() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragmentContainer, DetailFragment.newInstance())
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void setPresenter(FaceContract.Presenter presenter) {
		this.mFacePresenter = presenter;
	}

	public interface OnFavoritesListener {
		void onFavoriteClicked(FaceObject object);
	}

	@NonNull
	@Override
	public Observable<Bitmap> onButtonClicked() {
		return Observable.just(mFaceTextureView.getBitmap()).subscribeOn(Schedulers.computation());
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
			mCameraPreview.start(mCameraSource, mFaceRenderer);
		} catch (IOException e) {
			Log.e(TAG, "Unable to start camera source.", e);
			mCameraPreview.stop();
			mCameraSource = null;
		}
	}
}