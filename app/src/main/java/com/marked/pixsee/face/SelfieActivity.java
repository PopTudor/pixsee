package com.marked.pixsee.face;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
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

import org.rajawali3d.view.TextureView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SelfieActivity extends AppCompatActivity implements android.view.TextureView.SurfaceTextureListener {
	private static final String TAG = SelfieActivity.class + "***";

	private CameraSourcePixsee mCameraSource;
	private CameraSourcePreview mCameraSourcePreview;
	private FaceRenderer mFaceRenderer;

	private static final int RC_HANDLE_GMS = 9001;
	// permission request codes need to be < 256
	private static final int RC_HANDLE_CAMERA_PERM = 2;

	interface OnFavoritesListener {
		void onFavoriteClicked(FaceObject object);
	}

	TextureView mFaceTextureView;

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		mCameraSourcePreview.setSurfaceTexture(surface);
		startCameraSource();
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		mCameraSourcePreview.setSurfaceTexture(surface);
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_preview);
		mFaceRenderer = new FaceRenderer(this);

		mCameraSourcePreview = (CameraSourcePreview) findViewById(R.id.preview);
		mFaceTextureView = (TextureView) findViewById(R.id.faceSurfaceView);
//		faceSurfaceView.setTransparent(true);
		mFaceTextureView.setEGLContextClientVersion(2);
		mFaceTextureView.setSurfaceRenderer(mFaceRenderer);
		mFaceTextureView.setSurfaceTextureListener(this);

		findViewById(R.id.favorite1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FaceObject faceObject = new FaceObject(mFaceRenderer);
				faceObject.setTexture(R.drawable.mlg);
				mFaceRenderer.onFavoriteClicked(faceObject);
			}
		});
		findViewById(R.id.favorite2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FaceObject faceObject = new FaceObject(mFaceRenderer);
				faceObject.setTexture(R.drawable.hearts);
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
					public void onPictureTaken(byte[] bytes) {
						if (Utils.isExternalStorageWritable()) {
							File photo = Utils.getPublicPixseeDir();
							ByteArrayOutputStream streamArray = new ByteArrayOutputStream();
							try {
								FileOutputStream fos = new FileOutputStream(photo.getPath()
										                                            + "/photo.png");
								Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
								Bitmap rotatedImage = Utils.flip(bmp);
								bmp = null;
								BufferedOutputStream stream = new BufferedOutputStream(fos);

								rotatedImage.compress(Bitmap.CompressFormat.PNG, 70, streamArray);

								Bitmap result = Utils.combineImages(BitmapFactory.decodeByteArray
										                                                  (streamArray
												                                                   .toByteArray(), 0, streamArray.size()),
										mFaceRenderer.getLastScreenshot());
								streamArray.reset();
								result.compress(Bitmap.CompressFormat.PNG, 100, streamArray);
								stream.write(streamArray.toByteArray());
								stream.close();
							} catch (java.io.IOException e) {
								Log.e("PictureDemo", "Exception in photoCallback", e);
							}

						}

					}
				});
			}
		});


		// Check for the camera permission before accessing the camera.  If the
		// permission is not granted yet, request permission.
		int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
		if (rc == PackageManager.PERMISSION_GRANTED) {
			createCameraSource();
		} else {
			requestCameraPermission();
		}

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
				                            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
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

//		startCameraSource();
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
			mFaceRenderer.setMFace(item);
			Log.i(TAG, "Awesome person detected.  Hello!");
		}

		@Override
		public void onUpdate(Detector.Detections<Face> detections, Face item) {
			super.onUpdate(detections, item);
			if (item.getIsSmilingProbability() > 0.75) {
				Log.i(TAG, "I see a smile.  They must really enjoy your app.");
			}
			mFaceRenderer.setMFace(item);
		}

		@Override
		public void onMissing(Detector.Detections<Face> detections) {
			super.onMissing(detections);
		}

		@Override
		public void onDone() {
			super.onDone();
//			mFaceRenderer.removeMFace();
			Log.i(TAG, "Elvis has left the building.");
		}
	}

	/**
	 * Face tracker for each detected individual. This maintains a face graphic within the app's
	 * associated face overlay.
	 */
	private class FaceTrackerSquare extends Tracker<Face> {
		private GraphicOverlay mOverlay;
		private FaceGraphic mFaceGraphic;

		FaceTrackerSquare(GraphicOverlay overlay) {
			mOverlay = overlay;
			mFaceGraphic = new FaceGraphic(overlay);
		}

		/**
		 * Start tracking the detected face instance within the face overlay.
		 */
		@Override
		public void onNewItem(int faceId, Face item) {
			mFaceGraphic.setId(faceId);
		}

		/**
		 * Update the position/characteristics of the face within the overlay.
		 */
		@Override
		public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
			mOverlay.add(mFaceGraphic);
			mFaceGraphic.updateFace(face);
		}

		/**
		 * Hide the graphic when the corresponding face was not detected.  This can happen for
		 * intermediate frames temporarily (e.g., if the face was momentarily blocked from
		 * view).
		 */
		@Override
		public void onMissing(FaceDetector.Detections<Face> detectionResults) {
			mOverlay.remove(mFaceGraphic);
		}

		/**
		 * Called when the face is assumed to be gone for good. Remove the graphic annotation from
		 * the overlay.
		 */
		@Override
		public void onDone() {
			mOverlay.remove(mFaceGraphic);
		}
	}
}
