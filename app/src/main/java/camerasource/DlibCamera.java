package camerasource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.images.Size;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.PeopleDet;
import com.tzutalin.dlib.VisionDetRet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Tudor on 18-Aug-16.
 */

public class DlibCamera extends PixseeCamera {
	private static final String TAG = "DlibCamera";
	private final Context mContext;
	private PeopleDet mPeopleDet;
	private int[] mRGBBytes = null;
	private Bitmap mRGBframeBitmap = null;
	private boolean mIsComputing = false;
	private DlibFaceCallback mDlibFaceCallback;
	/**
	 * Dedicated thread and associated runnable for calling into the detector with frames, as the
	 * frames become available from the camera.
	 */
	private Thread mProcessingThread;
	private FrameProcessingRunnable mFrameProcessor;

	public DlibCamera(Context context, DlibFaceCallback dlibFaceCallback) {
		super(context);
		mContext = context;
		mDlibFaceCallback = dlibFaceCallback;
		mPeopleDet = new PeopleDet();
		mFrameProcessor = new FrameProcessingRunnable(mPeopleDet);
	}

	/**
	 * Called when the camera has a new preview frame.
	 */
	@Override
	protected Camera.PreviewCallback createPreviewCallback() {
		return new Camera.PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				mFrameProcessor.setNextFrame(data, camera);
			}
		};
	}

	@Override
	public PixseeCamera start(SurfaceTexture surfaceTexture) throws IOException {
		synchronized (mCameraLock) {
			super.start(surfaceTexture);

			mPeopleDet.init();
			mProcessingThread = new Thread(mFrameProcessor);
			mFrameProcessor.setActive(true);
			mProcessingThread.start();

		}
		Log.d(TAG, "--------------------> STARTED");
		return this;
	}

	@Override
	public void stop() {
		synchronized (mCameraLock) {
			mFrameProcessor.setActive(false);
			stopProcessingThread();
			// clear the buffer to prevent oom exceptions
			mBytesToByteBuffer.clear();
			super.stop();
			Log.d(TAG, "--------------------> STOPED");
		}
	}

	@Override
	public void release() {
		super.release();
		synchronized (mCameraLock) {
			mFrameProcessor.setActive(false);
			mFrameProcessor.release();
			stopProcessingThread();
			super.stop();
		}
	}

	private void stopProcessingThread() {
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
	}

	public interface DlibFaceCallback {

		void onUpdate(VisionDetRet ret);
	}

	private class FrameProcessingRunnable implements Runnable {
		// This lock guards all of the member variables below.
		private final Object mLock = new Object();
		private long mStartTimeMillis = SystemClock.elapsedRealtime();
		private boolean mActive = true;

		// These pending variables hold the state associated with the new frame awaiting processing.
		private long mPendingTimeMillis;
		private int mPendingFrameId = 0;
		private ByteBuffer mPendingFrameData;
		private PeopleDet mPeopleDet;

		FrameProcessingRunnable(PeopleDet peopleDet) {
			mPeopleDet = peopleDet;
		}


		/**
		 * Releases the underlying receiver.  This is only safe to do after the associated thread
		 * has completed, which is managed in camera source's release method above.
		 */
		@SuppressLint("Assert")
		void release() {
			assert (mProcessingThread.getState() == Thread.State.TERMINATED);
			mPeopleDet.deInit();
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
					Size size = getPreviewSize();
					int mPreviewWdith = size.getWidth();
					int mPreviewHeight = size.getHeight();


					mRGBBytes = new int[mPreviewWdith * mPreviewHeight];
					mRGBframeBitmap = Bitmap.createBitmap(mPreviewWdith, mPreviewHeight, Bitmap.Config.ARGB_8888);

					ImageUtils.convertYUV420SPToARGB8888(mPendingFrameData.array(), mRGBBytes, mRequestedPreviewWidth,
							mPreviewHeight, false);

					mRGBframeBitmap.setPixels(mRGBBytes, 0, mPreviewWdith, 0, 0, mPreviewWdith, mPreviewHeight);

					// Hold onto the frame data locally, so that we can use this for detection
					// below.  We need to clear mPendingFrameData to ensure that this buffer isn't
					// recycled back to the camera before we are done using that data.
					data = mPendingFrameData;
					mPendingFrameData = null;
				}

				// The code below needs to run outside of synchronization, because this will allow
				// the camera to add pending frame(s) while we are running detection on the current
				// frame.
//
				try {
					List<VisionDetRet> results = mPeopleDet.detBitmapFace(mRGBframeBitmap, Constants.getFaceShapeModelPath());
					for (final VisionDetRet ret : results) {
						mDlibFaceCallback.onUpdate(ret);
//						ArrayList<Point> landmarks = ret.getFaceLandmarks();
//						for (Point point : landmarks) {
//							int pointX = (int) (point.x * resizeRatio);
//							int pointY = (int) (point.y * resizeRatio);
//							 Get the point of the face landmarks
//						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				} finally {
					mCamera.addCallbackBuffer(data.array());
				}
			}
		}
	}
}

