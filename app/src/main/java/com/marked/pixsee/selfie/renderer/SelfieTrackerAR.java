package com.marked.pixsee.selfie.renderer;

import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

/**
 * Created by Tudor on 5/6/2016.
 */
public class SelfieTrackerAR extends Tracker<Face> {
	private static final String TAG = "FACE_TRACKER_***";
	private TrackerCallback trackerCallback;

	public SelfieTrackerAR(TrackerCallback trackerCallback) {
		this.trackerCallback = trackerCallback;
	}

	public SelfieTrackerAR() {
	}

	public void setTrackerCallback(TrackerCallback trackerCallback) {
		this.trackerCallback = trackerCallback;
	}

	@Override
	public void onNewItem(int id, Face item) {
		super.onNewItem(id, item);
		trackerCallback.onNewItem(id, item);
		Log.i(TAG, "Awesome person detected.  Hello!");
	}

	@Override
	public void onUpdate(Detector.Detections<Face> detections, Face item) {
		super.onUpdate(detections, item);
		trackerCallback.onUpdate(detections, item);
	}

	@Override
	public void onMissing(Detector.Detections<Face> detections) {
		super.onMissing(detections);
	}

	@Override
	public void onDone() {
		super.onDone();
		trackerCallback.onDone();
		Log.i(TAG, "Elvis has left the building.");
	}

	interface TrackerCallback {
		void onNewItem(int id, Face face);

		void onUpdate(Detector.Detections<Face> detections, Face face);

		void onDone();
	}
}
