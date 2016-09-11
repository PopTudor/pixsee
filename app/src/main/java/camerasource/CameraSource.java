package camerasource;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import java.io.IOException;
import java.lang.Thread.State;
import java.nio.ByteBuffer;

/**
 * Created by Tudor on 4/23/2016.
 */
@SuppressWarnings("deprecation")
public class CameraSource extends PixseeCamera {
	private static final String TAG = "OpenCameraSource";

	/**
	 * Dedicated thread and associated runnable for calling into the detector with frames, as the
	 * frames become available from the camera.
	 */
	private Thread mProcessingThread;
	private FrameProcessingRunnable mFrameProcessor;

	/**
	 * Only allow creation via the builder class.
	 */
	private CameraSource(Context context) {
		super(context);
	}

	/**
	 * Opens the camera and starts sending preview frames to the underlying detector.  The preview
	 * frames are not displayed.
	 *
	 * @throws IOException if the camera's preview texture or display could not be initialized
	 */
	@Override
	public PixseeCamera start(SurfaceTexture surfaceTexture) throws IOException {
		synchronized (mCameraLock) {
			super.start(surfaceTexture);

			mProcessingThread = new Thread(mFrameProcessor);
			mFrameProcessor.setActive(true);
			mProcessingThread.start();
		}
		return this;
	}

	/**
	 * Closes the camera and stops sending frames to the underlying frame detector.
	 * <p/>
	 * This camera source may be restarted again by calling
	 * {@link #start(SurfaceTexture surfaceTexture)} or
	 * <p/>
	 * Call {@link #release()} instead to completely shut down this camera source and release the
	 * resources of the underlying detector.
	 */
	@Override
	public void stop() {
		synchronized (mCameraLock) {
			mFrameProcessor.setActive(false);
			if (mProcessingThread != null) {
				try {
					// Wait for the thread to complete to ensure that we can't have multiple threads
					// executing at the same time (i.e., which would happen if we called start too
					// quickly after stop).
					mProcessingThread.join();
				} catch (InterruptedException e) {
					Log.d(TAG, "Frame processing thread interrupted on release.");
				}
				mProcessingThread = null;
			}
			// clear the buffer to prevent oom exceptions
			mBytesToByteBuffer.clear();
			super.stop();
		}
	}

	@Override
	protected Camera.PreviewCallback createPreviewCallback() {
		return new CameraSource.CameraPreviewCallback();
	}

	/**
	 * Builder for configuring and creating an associated camera source.
	 */
	public static class Builder {
		private final Detector<?> mDetector;
		private CameraSource mCameraSource;

		/**
		 * Creates a camera source builder with the supplied context and detector.  Camera preview
		 * images will be streamed to the associated detector upon starting the camera source.
		 */
		public Builder(@NonNull Context context, @NonNull Detector<?> detector) {
			mDetector = detector;
			mCameraSource = new CameraSource(context);
		}

		/**
		 * Sets the requested frame rate in frames per second.  If the exact requested value is not
		 * not available, the best matching available value is selected.   Default: 30.
		 */
		public Builder setRequestedFps(float fps) {
			if (fps <= 0) {
				throw new IllegalArgumentException("Invalid fps: " + fps);
			}
			mCameraSource.mRequestedFps = fps;
			return this;
		}

		public Builder setFocusMode(@FocusMode String mode) {
			mCameraSource.mFocusMode = mode;
			return this;
		}

		public Builder setFlashMode(@FlashMode String mode) {
			mCameraSource.mFlashMode = mode;
			return this;
		}

		/**
		 * Sets the desired width and height of the camera frames in pixels.  If the exact desired
		 * values are not available options, the best matching available options are selected.
		 * Also, we try to select a preview size which corresponds to the aspect ratio of an
		 * associated full picture size, if applicable.  Default: 1024x768.
		 */
		public Builder setRequestedPreviewSize(int width, int height) {
			// Restrict the requested range to something within the realm of possibility.  The
			// choice of 1000000 is a bit arbitrary -- intended to be well beyond resolutions that
			// devices can support.  We bound this to avoid int overflow in the code later.
			final int MAX = 1000000;
			if ((width <= 0) || (width > MAX) || (height <= 0) || (height > MAX)) {
				throw new IllegalArgumentException("Invalid preview size: " + width + "x" + height);
			}
			mCameraSource.mRequestedPreviewWidth = width;
			mCameraSource.mRequestedPreviewHeight = height;
			return this;
		}

		/**
		 * Sets the camera to use (either {@link #CAMERA_FACING_BACK} or
		 * {@link #CAMERA_FACING_FRONT}). Default: front facing.
		 */
		public Builder setFacing(int facing) {
			if ((facing != CAMERA_FACING_BACK) && (facing != CAMERA_FACING_FRONT)) {
				throw new IllegalArgumentException("Invalid camera: " + facing);
			}
			mCameraSource.mFacing = facing;
			return this;
		}

		/**
		 * Creates an instance of the camera source.
		 */
		public CameraSource build() {
			mCameraSource.mFrameProcessor = mCameraSource.new FrameProcessingRunnable(mDetector);
			return mCameraSource;
		}
	}
	//==============================================================================================
	// Frame processing
	//==============================================================================================

	/**
	 * Called when the camera has a new preview frame.
	 */
	private class CameraPreviewCallback implements Camera.PreviewCallback {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			mFrameProcessor.setNextFrame(data, camera);
		}
	}

	/**
	 * This runnable controls access to the underlying receiver, calling it to process frames when
	 * available from the camera.  This is designed to run detection on frames as fast as possible
	 * (i.e., without unnecessary context switching or waiting on the next frame).
	 * <p/>
	 * While detection is running on a frame, new frames may be received from the camera.  As these
	 * frames come in, the most recent frame is held onto as pending.  As soon as detection and its
	 * associated processing are done for the previous frame, detection on the mostly recently
	 * received frame will immediately start on the same thread.
	 */
	private class FrameProcessingRunnable implements Runnable {
		// This lock guards all of the member variables below.
		private final Object mLock = new Object();
		private Detector<?> mDetector;
		private long mStartTimeMillis = SystemClock.elapsedRealtime();
		private boolean mActive = true;

		// These pending variables hold the state associated with the new frame awaiting processing.
		private long mPendingTimeMillis;
		private int mPendingFrameId = 0;
		private ByteBuffer mPendingFrameData;

		FrameProcessingRunnable(Detector<?> detector) {
			mDetector = detector;
		}

		/**
		 * Releases the underlying receiver.  This is only safe to do after the associated thread
		 * has completed, which is managed in camera source's release method above.
		 */
		@SuppressLint("Assert")
		void release() {
			assert (mProcessingThread.getState() == State.TERMINATED);
			mDetector.release();
			mDetector = null;
		}

		/**
		 * Marks the runnable as active/not active.  Signals any blocked threads to continue.
		 */
		void setActive(boolean active) {
			synchronized (mLock) {
				mActive = active;
				mLock.notifyAll();
			}
		}

		/**
		 * Sets the frame data received from the camera.  This adds the previous unused frame buffer
		 * (if present) back to the camera, and keeps a pending reference to the frame data for
		 * future use.
		 */
		void setNextFrame(byte[] data, Camera camera) {
			synchronized (mLock) {
				if (mPendingFrameData != null) {
					camera.addCallbackBuffer(mPendingFrameData.array());
					mPendingFrameData = null;
				}

				if (!mBytesToByteBuffer.containsKey(data)) {
					Log.d(TAG,
							"Skipping frame.  Could not find ByteBuffer associated with the image " +
									"data from the camera.");
					return;
				}

				// Timestamp and frame ID are maintained here, which will give downstream code some
				// idea of the timing of frames received and when frames were dropped along the way.
				mPendingTimeMillis = SystemClock.elapsedRealtime() - mStartTimeMillis;
				mPendingFrameId++;
				mPendingFrameData = mBytesToByteBuffer.get(data);

				// Notify the processor thread if it is waiting on the next frame (see below).
				mLock.notifyAll();
			}
		}

		/**
		 * As long as the processing thread is active, this executes detection on frames
		 * continuously.  The next pending frame is either immediately available or hasn't been
		 * received yet.  Once it is available, we transfer the frame info to local variables and
		 * run detection on that frame.  It immediately loops back for the next frame without
		 * pausing.
		 * <p/>
		 * If detection takes longer than the time in between new frames from the camera, this will
		 * mean that this loop will run without ever waiting on a frame, avoiding any context
		 * switching or frame acquisition time latency.
		 * <p/>
		 * If you find that this is using more CPU than you'd like, you should probably decrease the
		 * FPS setting above to allow for some idle time in between frames.
		 */
		@Override
		public void run() {
			Frame outputFrame;
			ByteBuffer data;

			while (true) {
				synchronized (mLock) {
					while (mActive && (mPendingFrameData == null)) {
						try {
							// Wait for the next frame to be received from the camera, since we
							// don't have it yet.
							mLock.wait();
						} catch (InterruptedException e) {
							Log.d(TAG, "Frame processing loop terminated.", e);
							return;
						}
					}

					if (!mActive) {
						// Exit the loop once this camera source is stopped or released.  We check
						// this here, immediately after the wait() above, to handle the case where
						// setActive(false) had been called, triggering the termination of this
						// loop.
						return;
					}

					outputFrame = new Frame.Builder()
							              .setImageData(mPendingFrameData, mPreviewSize.getWidth(),
									              mPreviewSize.getHeight(), ImageFormat.NV21)
							              .setId(mPendingFrameId)
							              .setTimestampMillis(mPendingTimeMillis)
							              .setRotation(mRotation)
							              .build();

					// Hold onto the frame data locally, so that we can use this for detection
					// below.  We need to clear mPendingFrameData to ensure that this buffer isn't
					// recycled back to the camera before we are done using that data.
					data = mPendingFrameData;
					mPendingFrameData = null;
				}

				// The code below needs to run outside of synchronization, because this will allow
				// the camera to add pending frame(s) while we are running detection on the current
				// frame.

				try {
					mDetector.receiveFrame(outputFrame);
				} catch (Throwable t) {
					Log.e(TAG, "Exception thrown from receiver.", t);
				} finally {
					mCamera.addCallbackBuffer(data.array());
				}
			}
		}
	}
}
