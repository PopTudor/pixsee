package com.marked.pixsee.frienddetail;


import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marked.pixsee.R;
import com.marked.pixsee.friends.cards.data.MessageConstants;

/**
 * An activity representing a single Contact detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link com.marked.pixsee.friends.FriendsActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link FriendDetailFragment}.
 */
public class FriendDetailActivity extends AppCompatActivity implements FriendDetailFragment.ContactDetailFragmentInteraction {
	public static final String EXTRA_CONTACT = "com.marked.vifo.ui.activity.EXTRA_CONTACT";

	private FriendDetailFragment mFragment;
	private EditText messageEditText;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_detail);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setAllowEnterTransitionOverlap(false);
		}
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		mFragment = FriendDetailFragment.newInstance(getIntent().getParcelableExtra(EXTRA_CONTACT));

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
		messageEditText = (EditText) findViewById(R.id.messageEditText);
		messageEditText.addTextChangedListener(new  TextWatcher (){
			boolean mTyping = false;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!mTyping && count > 0) mTyping = true;
				if (mTyping && count == 0) mTyping = false;
				mFragment.onTyping(mTyping);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

		});
	}

	void sendMessage(View view) {
		String message = messageEditText.getText().toString();
		messageEditText.setText("");
		if (!message.isEmpty())
			mFragment.sendMessage(message, MessageConstants.MessageType.YOU_MESSAGE);
	}

	void sendPixsee(View view) {
		mFragment.sendMessage("http://www.ghacks.net/wp-content/themes/magatheme/img/mozilla-firefox.png", MessageConstants.MessageType.YOU_IMAGE);
		//		TODO("This operation should launch the camera to take a photo")
	}

	@Override
	protected void onStop() {
		//		Must be called when your application is done using GCM, to release internal resources.
		GoogleCloudMessaging.getInstance(this).close();
		super.onStop();
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
