package com.marked.pixsee.facedetail;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.marked.pixsee.R;
import com.marked.pixsee.face.SelfieActivity;

public class FaceDetail extends AppCompatActivity {
	ImageView photoImageView;
	ImageView photoRendererImageView;
	Button button;
	RelativeLayout pictureContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_face_detail);
		photoImageView = (ImageView) findViewById(R.id.photoImageView);
		photoRendererImageView = (ImageView) findViewById(R.id.photoRendererImageView);
		pictureContainer = (RelativeLayout) findViewById(R.id.pictureContainer);

		photoImageView.setImageURI(Uri.parse(getIntent().getStringExtra(SelfieActivity.PHOTO_EXTRA)));
		photoRendererImageView.setImageURI(Uri.parse(getIntent().getStringExtra(SelfieActivity.PHOTO_RENDERER_EXTRA)));

		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	public static Bitmap getActivitySnapshot(ViewGroup view) {
		view.setDrawingCacheEnabled(true);
		Bitmap bmap = view.getDrawingCache();
		Bitmap snapshot = Bitmap.createBitmap(bmap, 0, 0, bmap.getWidth(), bmap.getHeight(), null, true);
		view.setDrawingCacheEnabled(false);
		return snapshot;
	}

}
