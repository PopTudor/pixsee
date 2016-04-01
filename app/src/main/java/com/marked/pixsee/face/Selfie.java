package com.marked.pixsee.face;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.vision.face.FaceDetector;
import com.marked.pixsee.R;

public class Selfie extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);

		FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(true)
		                                                                             .setProminentFaceOnly(true)
		                                                                             .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
		                                                                             .build();
	}
}
