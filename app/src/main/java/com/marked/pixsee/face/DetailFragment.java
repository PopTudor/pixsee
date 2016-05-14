package com.marked.pixsee.face;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.marked.pixsee.R;
import com.marked.pixsee.utility.BitmapUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
public class DetailFragment extends Fragment {
	private Handler mHideHandler = new Handler();
	private Boolean mVisible = false;
	private Runnable mShowPart2Runnable = new Runnable() {
		@Override
		public void run() {
			// Delayed display of UI elements
			showAnimation();
		}
	};
	private Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			hide();
		}
	};
	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	private View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (DetailFragment.AUTO_HIDE) {
				delayedHide(DetailFragment.AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	private OnFragmentInteractionListener mListener;
	private AppBarLayout mAppbar;

	private FacebookCallback<Sharer.Result> facebookCallback = new FacebookCallback<Sharer.Result>() {
		@Override
		public void onSuccess(Sharer.Result result) {
			Toast.makeText(getActivity(), "Posted...", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getActivity(), "Cancel...", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(FacebookException error) {
			Toast.makeText(getActivity(), "Error...", Toast.LENGTH_SHORT).show();
		}
	};
	private CallbackManager callbackManager;
	private ShareDialog shareDialog;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_face_detail, container, false);
		shareDialog.registerCallback(callbackManager, facebookCallback);

		mAppbar = (AppBarLayout) rootView.findViewById(R.id.app_bar);

		ImageButton saveImageButton = (ImageButton) rootView.findViewById(R.id.saveImageButton);
		saveImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onButtonClicked()
						.map(new Func1<Bitmap, Void>() {
							@Override
							public Void call(Bitmap bitmap) {
								SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
								Date now = new Date();
								String prefix = "PX_IMG_" + formatter.format(now);
								String filename = prefix + ".jpg";
								BitmapUtils.saveFile(bitmap, Bitmap.CompressFormat.JPEG, 100, filename);
								return null;
							}
						}).observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<Void>() {
							@Override
							public void onCompleted() {
								Toast.makeText(getActivity(), "Image saved !", Toast.LENGTH_SHORT).show();

							}

							@Override
							public void onError(Throwable e) {
								Toast.makeText(getActivity(), "Error...", Toast.LENGTH_SHORT).show();

							}

							@Override
							public void onNext(Void aVoid) {
							}
						});

			}
		});
		ImageButton shareFacebookImageButton = (ImageButton) rootView.findViewById(R.id.shareFacebookImageButton);
		shareFacebookImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ShareDialog.canShow(SharePhotoContent.class)) {
					mListener.onButtonClicked()
							.map(new Func1<Bitmap, SharePhotoContent>() {
								@Override
								public SharePhotoContent call(Bitmap bitmap) {
									SharePhoto photo = new SharePhoto.Builder()
											                   .setCaption("Pixsee")
											                   .setUserGenerated(true)
											                   .setBitmap(bitmap)
											                   .build();
									return new SharePhotoContent.Builder()
											       .addPhoto(photo)
											       .build();
								}
							}).observeOn(AndroidSchedulers.mainThread())
							.subscribe(new Subscriber<SharePhotoContent>() {
								@Override
								public void onCompleted() {
									Toast.makeText(getActivity(), "Image shared !", Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onError(Throwable e) {
									Toast.makeText(getActivity(), "Image could not be shared !", Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onNext(SharePhotoContent sharePhotoContent) {
									shareDialog.show(sharePhotoContent);
								}
							});
				}
			}
		});
		ImageButton sendFacebookImageButton = (ImageButton) rootView.findViewById(R.id.sendFacebookImageButton);

		sendFacebookImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onButtonClicked()
						.map(new Func1<Bitmap, SharePhotoContent>() {
							@Override
							public SharePhotoContent call(Bitmap bitmap) {
								SharePhoto photo = new SharePhoto.Builder()
										                   .setCaption("Pixsee")
										                   .setUserGenerated(true)
										                   .setBitmap(bitmap)
										                   .build();
								return new SharePhotoContent.Builder()
										       .addPhoto(photo)
										       .build();
							}
						})
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<SharePhotoContent>() {
							@Override
							public void onCompleted() {

							}

							@Override
							public void onError(Throwable e) {

							}

							@Override
							public void onNext(SharePhotoContent sharePhotoContent) {
								MessageDialog.show(getActivity(), sharePhotoContent);
							}
						});
			}
		});
		return rootView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (DetailFragment.OnFragmentInteractionListener) context;
			callbackManager = com.facebook.CallbackManager.Factory.create();
			shareDialog = new ShareDialog(this);
		} else {
			throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * <p/>
	 * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
	 */
	interface OnFragmentInteractionListener {
		Observable<Bitmap> onButtonClicked();
	}

	void showAnimation() {
		mAppbar.animate()
				.alpha(1.0f)
				.setDuration(300L)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						super.onAnimationStart(animation);
						mAppbar.setVisibility(View.VISIBLE);
					}
				})
				.start();
	}

	void hideAnimation() {
		mAppbar.animate()
				.alpha(0.0f)
				.setDuration(300L)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						super.onAnimationStart(animation);
						mAppbar.setVisibility(View.GONE);
					}
				})
				.start();
	}

	@Override
	public void onStart() {
		super.onStart();
		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(3000);
	}

	private void toggle() {
		if (mVisible) hide();
		else show();
	}

	private void hide() {
		// Hide UI first
		hideAnimation();

		mVisible = false;

		// Schedule a runnable to remove the status and navigation bar after a delay
		mHideHandler.removeCallbacks(mShowPart2Runnable);
	}

	@SuppressLint("InlinedApi")
	private void show() {
		// Show the system bar
		mVisible = true;

		// Schedule a runnable to display UI elements after a delay
		mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
	}

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	public static DetailFragment newInstance() {
		return new DetailFragment();
	}

	/**
	 * Whether or not the system UI should be auto-hidden after
	 * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
	 */
	private static boolean AUTO_HIDE = true;

	/**
	 * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static int AUTO_HIDE_DELAY_MILLIS = 8000;

	/**
	 * Some older devices needs a small delay between UI widget updates
	 * and a change of the status and navigation bar.
	 */
	private int UI_ANIMATION_DELAY = 100;
}// Required empty public constructor
