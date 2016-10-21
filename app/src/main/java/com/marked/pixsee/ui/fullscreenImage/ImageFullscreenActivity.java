package com.marked.pixsee.ui.fullscreenImage;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.marked.pixsee.R;

/**
 * An example full-screen activity that shows and hides the UI with user interaction.
 */
public class ImageFullscreenActivity extends AppCompatActivity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
	 */
	private static boolean AUTO_HIDE = true;
	/**
	 * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static int AUTO_HIDE_DELAY_MILLIS = 5000;
	/**
	 * Some older devices needs a small delay between UI widget updates
	 * and a change of the status and navigation bar.
	 */
	private static int UI_ANIMATION_DELAY = 100;
	private Handler mHideHandler = new Handler();
	private boolean mVisible = false;
	private SubsamplingScaleImageView fullscreenContent;
	private LinearLayout fullscreenContentControls;
	private AppBarLayout fullscreenAppBar;
	private Runnable mHidePart2Runnable = new Runnable() {
		@Override
		public void run() {
			// Note that some of these constants are new as of API 16 (Jelly Bean)
			// and API 19 (KitKat). It is safe to use them, as they are inlined
			// at compile-time and do nothing on earlier devices.
			if (fullscreenContent!=null)
				fullscreenContent.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
					View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
			hideAnimation();
		}
	};
	private Runnable mShowPart2Runnable = new Runnable () {
		@Override
		public void run() {showAnimation();}
    };
	private Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			hideAnimation();
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
			if (AUTO_HIDE) delayedHide(AUTO_HIDE_DELAY_MILLIS);
			return false;
		}
	};

	void showAnimation() {
		mVisible = true;
		fullscreenContentControls
				.animate()
				.alpha(1.0f)
				.setDuration(300L)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						super.onAnimationStart(animation);
						if (fullscreenContentControls!=null)
							fullscreenContentControls.setVisibility(View.VISIBLE); 					}
				})
				.start();

		fullscreenAppBar
				.animate()
				.alpha(1.0f)
				.setDuration(300L)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						super.onAnimationStart(animation);
						if (fullscreenAppBar != null)
							fullscreenAppBar.setVisibility(View.VISIBLE);
					}
				})
				.start();
	}

    void hideAnimation() {
	    mVisible = false;
	    fullscreenContentControls
			    .animate()
			    .alpha(0.0f)
			    .setDuration(300L)
			    .setListener(new AnimatorListenerAdapter() {
				    @Override
				    public void onAnimationEnd(Animator animation) {
					    super.onAnimationEnd(animation);
					    if (fullscreenContentControls != null)
						    fullscreenContentControls.setVisibility(View.GONE);
				    }
			    })
			    .start();

	    fullscreenAppBar
			    .animate()
			    .alpha(0.0f)
			    .setDuration(300L)
			    .setListener(new AnimatorListenerAdapter() {
				    @Override
				    public void onAnimationEnd(Animator animation) {
					    super.onAnimationEnd(animation);
					    if (fullscreenContentControls != null)
						    fullscreenContentControls.setVisibility(View.GONE);
				    }
			    })
			    .start();
    }

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.semi_transparent_black));
		}
		fullscreenContentControls = (LinearLayout) findViewById(R.id.fullscreenContentControls);
		fullscreenAppBar = (AppBarLayout) findViewById(R.id.fullscreenAppBar);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed() ;
			}
		});
		toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

		{
			Uri imagePath = getIntent().getParcelableExtra("URI");

			fullscreenContent = (SubsamplingScaleImageView) findViewById(R.id.fullscreenContent);
			// Set up the user interaction to manually show or hide the system UI.
			fullscreenContent.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					toggle();
				}
			});
			fullscreenContent.setOnTouchListener(mDelayHideTouchListener);
			fullscreenContent.setImage(ImageSource.uri(imagePath));
			fullscreenContent.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
			fullscreenContent.setMinimumDpi(100);
			fullscreenContent.setDoubleTapZoomDpi(100 + 20);
			fullscreenContent.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
		}
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

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
        // Schedule a runnable to remove the status and navigation bar after a delay
	    mHideHandler.removeCallbacks(mShowPart2Runnable);
	    mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        // Schedule a runnable to display UI elements after a delay
	    mHideHandler.removeCallbacks(mHidePart2Runnable);
	    mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(long delayMillis) {
	    mHideHandler.removeCallbacks(mHideRunnable);
	    mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
