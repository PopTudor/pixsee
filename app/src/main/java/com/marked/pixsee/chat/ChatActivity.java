package com.marked.pixsee.chat;


import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.marked.pixsee.R;
import com.marked.pixsee.friends.data.User;

/**
 * An activity representing a single Contact detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link com.marked.pixsee.main.MainActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ChatFragment}.
 */
public class ChatActivity extends AppCompatActivity {
	public static final String EXTRA_CONTACT = "com.marked.vifo.ui.activity.EXTRA_CONTACT";

	private ChatFragment mFragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setAllowEnterTransitionOverlap(false);
		}
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
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
}
