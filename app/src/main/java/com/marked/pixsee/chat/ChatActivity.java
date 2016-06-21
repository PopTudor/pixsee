package com.marked.pixsee.chat;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.marked.pixsee.R;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.selfie.PictureDetailSendFragment;
import com.marked.pixsee.selfie.SelfieFragment;

import java.io.File;

import rx.Observable;

/**
 * An activity representing a single Contact detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link com.marked.pixsee.main.MainActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ChatFragment}.
 */
public class ChatActivity extends AppCompatActivity implements ChatFragment.ChatFragmentInteraction,SelfieFragment
		.OnSelfieInteractionListener,PictureDetailSendFragment.OnPictureDetailSendListener{
	public static final String EXTRA_CONTACT = "com.marked.vifo.ui.activity.EXTRA_CONTACT";

	private ChatFragment mFragment;
	private ActivityComponent mActivityComponent;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setAllowEnterTransitionOverlap(false);
		}
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		// as long as activity component != null, objects with @ActivityScope are in memory
		mActivityComponent = DaggerActivityComponent.builder()
				.activityModule(new ActivityModule(this))
				.build();

		User user = getIntent().getParcelableExtra(EXTRA_CONTACT);
		mFragment = ChatFragment.newInstance(user);
		getSupportActionBar().setTitle(user.getName());

		/*send the clicked contact to the fragment*/
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragmentContainer, mFragment, "contactDetailFragment").commit();
		// Show the Up button in the action bar.
		Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_24dp);
		if (getSupportActionBar()!=null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
			getSupportActionBar().setHomeAsUpIndicator(upArrow);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				onBackPressed();
				//		        navigateUpTo(new Intent(this, ContactListActivity.class));
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCameraClick() {
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		findViewById(R.id.toolbar).setVisibility(View.GONE);
		Fragment fragment = SelfieFragment.newInstance();
		getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment, "camera").addToBackStack(null).commit();
	}

	@Override
	public void showTakenPictureActions() {
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragmentContainer, PictureDetailSendFragment.newInstance())
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void selfieFragmentDesroyed() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
	}

	@Override
	public Observable<Bitmap> getPicture() {
		return ((SelfieFragment) getSupportFragmentManager().findFragmentByTag("camera")).getPicture();
	}

	@Override
	public void stop() {
		((SelfieFragment) getSupportFragmentManager().findFragmentByTag("camera")).resumeSelfie();
	}

	@Override
	public void pictureTaken(File picture) {
		getSupportFragmentManager().popBackStackImmediate();/* after the picture is saved on disk, we get it's location and continue
		resuming the camera preview; here we stop that and send the file to chat fragment*/
		((ChatFragment) getSupportFragmentManager().findFragmentByTag("contactDetailFragment")).pictureTaken(picture);
	}

	public ActivityComponent getActivityComponent() {
		return mActivityComponent;
	}
}
