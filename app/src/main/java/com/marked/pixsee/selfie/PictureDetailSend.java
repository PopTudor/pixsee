package com.marked.pixsee.selfie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marked.pixsee.R;
import com.marked.pixsee.utility.BitmapUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Tudor on 07-Jun-16.
 */
public class PictureDetailSend extends Fragment {
	public static PictureDetailSend newInstance() {

		Bundle args = new Bundle();

		PictureDetailSend fragment = new PictureDetailSend();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile_picture_detail, container, false);
		rootView.findViewById(R.id.saveFabButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPictureDetailListener.onButtonClicked()
						.map(new Func1<Bitmap, File>() {
							@Override
							public File call(Bitmap bitmap) {
								return saveBitmap(bitmap);
							}
						})
						.doOnCompleted(new Action0() {
							@Override
							public void call() {
								getActivity().getSupportFragmentManager().popBackStack();
							}
						})
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Action1<File>() {
							@Override
							public void call(File file) {
								mPictureDetailListener.pictureTaken(file);
							}
						}, new Action1<Throwable>() {
							@Override
							public void call(Throwable throwable) {
								Toast.makeText(getActivity(), "Picture could not be taken!", Toast.LENGTH_SHORT).show();
								getActivity().getSupportFragmentManager().popBackStack();
							}
						});
			}
		});
		return rootView;
	}

	private File saveBitmap(Bitmap bitmap) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date now = new Date();
		String prefix = "PX_IMG_" + formatter.format(now);
		String filename = prefix + ".jpg";
		File file = BitmapUtils.saveFile(bitmap, Bitmap.CompressFormat.JPEG, 80, filename);
		galleryAddPic(file.getPath());
		return file;
	}

	private void galleryAddPic(String photoPath) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(photoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		getActivity().sendBroadcast(mediaScanIntent);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mPictureDetailListener = (OnPictureDetailSendListener) context;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mPictureDetailListener = null;
	}

	@Override
	public void onStop() {
		super.onStop();
		mPictureDetailListener.hiddenProfilePictureDetailActions();
	}

	public interface OnPictureDetailSendListener {
		/*
		* When the user clicks the fab button for saving, this method gets triggered
		* */
		Observable<Bitmap> onButtonClicked();

		/**
		 * This should get start when the image is frozen and {@link DetailFragment} is active to show the user what
		 * actions can he take with the taken picture.
		 * When the user hits the back button, this will notify the activity that it should
		 * start/unfreeze the preview fragment
		 */
		void hiddenProfilePictureDetailActions();

		void pictureTaken(File picture);
	}

	private OnPictureDetailSendListener mPictureDetailListener;

}
