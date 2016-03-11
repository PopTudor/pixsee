package com.marked.pixsee.frienddetail;

import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.marked.pixsee.R
import com.marked.pixsee.utility.add
import com.marked.pixsee.data.message.MessageConstants
import kotlinx.android.synthetic.main.activity_contact_detail.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * An activity representing a single Contact detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ContactListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ContactDetailFragment}.
 */
class ChatDetailActivity : AppCompatActivity(), FriendDetailFragment.ContactDetailFragmentInteraction {
	companion object {
		const val EXTRA_CONTACT = "com.marked.vifo.ui.activity.EXTRA_CONTACT";
	}

	final val mFragment by lazy {
		FriendDetailFragment.newInstance(intent.getParcelableExtra(EXTRA_CONTACT))
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_contact_detail)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.allowEnterTransitionOverlap = false
		}
		setSupportActionBar(toolbar)


		/*send the clicked contact to the fragment*/
		supportFragmentManager.add(R.id.fragmentContainer, mFragment, "contactDetailFragment")
		// Show the Up button in the action bar.
		supportActionBar?.setDisplayHomeAsUpEnabled(true);

		var upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
		upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
		supportActionBar?.setHomeAsUpIndicator(upArrow);

		messageEditText.addTextChangedListener(object : TextWatcher {
			var mTyping = false;
			override fun afterTextChanged(s: Editable?) {
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				if (!mTyping && count > 0) mTyping = true
				if (mTyping && count == 0) mTyping = false
				mFragment.onTyping(mTyping);
			}
		});

	}

	fun sendMessage(view: View) {
		val message = messageEditText.text.toString();
		messageEditText.setText("");
		if (!message.isNullOrBlank())
			mFragment.sendMessage(message, MessageConstants.MessageType.YOU_MESSAGE);
	}

	fun sendPixsee(view: View) {
		mFragment.sendMessage("http://www.ghacks.net/wp-content/themes/magatheme/img/mozilla-firefox.png", MessageConstants.MessageType.YOU_IMAGE)
		//		TODO("This operation should launch the camera to take a photo")
	}

	override fun onStop() {
		//		Must be called when your application is done using GCM, to release internal resources.
		GoogleCloudMessaging.getInstance(this).close();
		super.onStop();
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
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
