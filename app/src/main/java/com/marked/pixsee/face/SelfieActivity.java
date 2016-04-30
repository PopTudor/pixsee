package com.marked.pixsee.face;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.marked.pixsee.R;
import com.marked.pixsee.facedetail.FaceDetail;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.rajawali3d.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SelfieActivity extends AppCompatActivity {
	private static final String TAG = SelfieActivity.class + "***";
	public static final String PHOTO_EXTRA = "PHOTO";
	public static final String PHOTO_RENDERER_EXTRA = "PHOTO_RENDERER";
	private CameraSourcePixsee mCameraSource;
	private CameraSourcePreview mCameraSourcePreview;
	private FaceRenderer mFaceRenderer;

	private static final int RC_HANDLE_GMS = 9001;
	// permission request codes need to be < 256
	private static final int RC_HANDLE_CAMERA_PERM = 2;

	interface OnFavoritesListener {
		void onFavoriteClicked(FaceObject object);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


		setContentView(R.layout.activity_face);
		mFaceRenderer = new FaceRenderer(this);

		mCameraSourcePreview = (CameraSourcePreview) findViewById(R.id.preview);
		final SurfaceView mFaceTextureView = (SurfaceView) findViewById(R.id.texture_view);
		mFaceTextureView.setTransparent(true);
		mFaceTextureView.setEGLContextClientVersion(2);
		mFaceTextureView.setSurfaceRenderer(mFaceRenderer);

		findViewById(R.id.favorite1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FaceObject faceObject = new FaceObject(mFaceRenderer);
				faceObject.setTexture(R.drawable.mlg, false);
				mFaceRenderer.onFavoriteClicked(faceObject);
			}
		});
		findViewById(R.id.favorite2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FaceObject faceObject = new FaceObject(mFaceRenderer);
				faceObject.setTexture(R.drawable.hearts, false);
				mFaceRenderer.onFavoriteClicked(faceObject);
			}
		});
		findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCameraSource.takePicture(new CameraSourcePixsee.ShutterCallback() {
					@Override
					public void onShutter() {
						mFaceRenderer.takeScreenshot();
					}
				}, new CameraSourcePixsee.PictureCallback() {
					@Override
					public void onPictureTaken(final byte[] bytes) {
						mCameraSourcePreview.stop();
						while (mFaceRenderer.mLastPictureLocation == null) ;
						Observable.create(new Observable.OnSubscribe<Bitmap>() {
							@Override
							public void call(Subscriber<? super Bitmap> subscriber) {
								Bitmap b1 = BitmapUtils.getBitmapFromFile(mFaceRenderer.mLastPictureLocation.getPath(),
										mFaceRenderer.getDefaultViewportWidth(),
										mFaceRenderer.getDefaultViewportHeight());
								Bitmap b2 = BitmapUtils.getBitmapFromBytes(BitmapUtils.flipHorizontal(bytes));
								Bitmap b3 = BitmapUtils.combineImagesToSameSize(b2, b1);
								subscriber.onNext(b3);
								subscriber.onCompleted();
							}
						}).subscribeOn(Schedulers.computation())
								.map(new Func1<Bitmap, String>() {
									@Override
									public String call(Bitmap bitmap) {
										String folderName = "Pixsee";
										File privateFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName);
										String filename = "picture.jpg";
										String path = privateFolder.getPath() + "/";
										try {
											FileOutputStream stream = new FileOutputStream(path + filename);
											stream.write(BitmapUtils.getBytesFromBitmap(bitmap));
											stream.flush();
											stream.close();
										} catch (java.io.IOException e) {
											Log.e("PictureDemo", "Exception in photoCallback", e);
										}
										return path + filename;
									}
								})
								.subscribeOn(Schedulers.io())
								.observeOn(AndroidSchedulers.mainThread())
								.subscribe(new Action1<String>() {
									@Override
									public void call(String s) {
										Intent intent = new Intent(SelfieActivity.this, FaceDetail.class);
										intent.putExtra(PHOTO_EXTRA, s);
										startActivity(intent);
									}
								});
					}
				});
			}
		});
		// Must be done during an initialization phase like onCreate
		RxPermissions.getInstance(this)
				.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
				.subscribe(new Action1<Boolean>() {
					@Override
					public void call(Boolean granted) {
						if (granted) { // Always true pre-M
							createCameraSource();
							// I can control the camera now
						} else {
							// Oups permission denied
							finish();
						}
					}
				});
	}

	/**
	 * Handles the requesting of the camera permission.  This includes
	 * showing a "Snackbar" message of why the permission is needed then
	 * sending the request.
	 */
	private void requestCameraPermission() {
		Log.w(TAG, "Camera permission is not granted. Requesting permission");

		final String[] permissions = new String[]{Manifest.permission.CAMERA};

		if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
			ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
			return;
		}

		final Activity thisActivity = this;

		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
			}
		};

//		Snackbar.make(mCameraSourcePreview, "We need camera permission in order to take cool selfies !", Snackbar.LENGTH_INDEFINITE)
//		        .setAction("OK", listener)
//		        .show();
	}

	/**
	 * Creates and starts the camera.  Note that this uses a higher resolution in comparison
	 * to other detection examples to enable the barcode detector to detect small barcodes
	 * at long distances.
	 */
	private void createCameraSource() {
		FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(true)
				                            .setProminentFaceOnly(true)
				                            .setClassificationType(FaceDetector.ACCURATE_MODE)
				                            .setLandmarkType(0)
				                            .build();
//				FaceTrackerSquare faceTracker = new FaceTrackerSquare(mGraphicOverlay);
		FaceTrackerAR faceTracker = new FaceTrackerAR(mFaceRenderer);
		faceDetector.setProcessor(new LargestFaceFocusingProcessor.Builder(faceDetector, faceTracker).build());

		if (!faceDetector.isOperational()) {
			// Note: The first time that an app using face API is installed on a device, GMS will
			// download a native library to the device in order to do detection.  Usually this
			// completes before the app is run for the first time.  But if that download has not yet
			// completed, then the above call will not detect any faces.
			//
			// isOperational() can be used to check if the required native library is currently
			// available.  The detector will automatically become operational once the library
			// download completes on device.
			Log.w(TAG, "Face detector dependencies are not yet available.");
		}
		mCameraSource = new CameraSourcePixsee.Builder(this, faceDetector).setRequestedFps(30.0f)
				                .setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO)
//	             .setRequestedPreviewSize(640, 480)
				                .setFacing(CameraSource.CAMERA_FACING_FRONT)
				                .build();
	}

	/**
	 * Restarts the camera.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		startCameraSource();
	}

	/**
	 * Stops the camera.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mCameraSourcePreview.stop();
	}

	/**
	 * Releases the resources associated with the camera source, the associated detector, and the
	 * rest of the processing pipeline.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCameraSource != null) {
			mCameraSource.release();
		}
	}

	/**
	 * Callback for the result from requesting permissions. This method
	 * is invoked for every call on {@link #requestPermissions(String[], int)}.
	 * <p>
	 * <strong>Note:</strong> It is possible that the permissions request interaction
	 * with the user is interrupted. In this case you will receive empty permissions
	 * and results arrays which should be treated as a cancellation.
	 * </p>
	 *
	 * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
	 * @param permissions  The requested permissions. Never null.
	 * @param grantResults The grant results for the corresponding permissions
	 *                     which is either {@link PackageManager#PERMISSION_GRANTED}
	 *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
	 * @see #requestPermissions(String[], int)
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode != RC_HANDLE_CAMERA_PERM) {
			Log.d(TAG, "Got unexpected permission result: " + requestCode);
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
			return;
		}

		if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			Log.d(TAG, "Camera permission granted - initialize the camera source");
			// we have permission, so create the camerasource
			createCameraSource();
			return;
		}

		Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
				           " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Face Tracker sample")
				.setMessage("We don't have camera permission :(")
				.setPositiveButton("OK", listener)
				.show();
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

		// check that the device has play services available.
		int code = GoogleApiAvailability.getInstance()
				           .isGooglePlayServicesAvailable(getApplicationContext());
		if (code != ConnectionResult.SUCCESS) {
			Dialog dlg = GoogleApiAvailability.getInstance()
					             .getErrorDialog(this, code, RC_HANDLE_GMS);
			dlg.show();
		}

		if (mCameraSource != null) {
			try {
				mCameraSourcePreview.start(mCameraSource, mFaceRenderer);
			} catch (IOException e) {
				Log.e(TAG, "Unable to start camera source.", e);
				mCameraSource.release();
				mCameraSource = null;
			}
		}
	}

	class FaceTrackerAR extends Tracker<Face> {
		private FaceRenderer mFaceRenderer;

		public FaceTrackerAR(FaceRenderer faceRenderer) {
			mFaceRenderer = faceRenderer;
		}

		@Override
		public void onNewItem(int id, Face item) {
			super.onNewItem(id, item);
			mFaceRenderer.onNewItem(item);
			Log.i(TAG, "Awesome person detected.  Hello!");
		}

		@Override
		public void onUpdate(Detector.Detections<Face> detections, Face item) {
			super.onUpdate(detections, item);
			if (item.getIsSmilingProbability() > 0.75) {
				Log.i(TAG, "I see a smile.  They must really enjoy your app.");
				mFaceRenderer.isSmiling();
			}
			mFaceRenderer.onUpdate(item);
		}

		@Override
		public void onMissing(Detector.Detections<Face> detections) {
			super.onMissing(detections);
		}

		@Override
		public void onDone() {
			super.onDone();
			mFaceRenderer.onDone();
			Log.i(TAG, "Elvis has left the building.");
		}
	}
}
