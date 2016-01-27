package com.marked.vifo.ui.activity;

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
import com.marked.vifo.R
import com.marked.vifo.helper.add
import com.marked.vifo.ui.fragment.ContactDetailFragment
import kotlinx.android.synthetic.main.activity_contact_detail.*

/**
 * An activity representing a single Contact detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ContactListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ContactDetailFragment}.
 */
public class ContactDetailActivity : AppCompatActivity(), ContactDetailFragment.ContactDetailFragmentInteraction {
    companion object {
	    public const val EXTRA_CONTACT = "com.marked.vifo.ui.activity.EXTRA_CONTACT";
    }

	final val mFragmentManager by lazy { supportFragmentManager };
	final val mFragment by lazy { ContactDetailFragment.newInstance(intent.getParcelableExtra(EXTRA_CONTACT)) }

	override
	protected fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_contact_detail)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
	        window.allowEnterTransitionOverlap = false
        }
		setSupportActionBar(detail_toolbar)

        /*send the clicked contact to the fragment*/
		mFragmentManager.add(R.id.fragmentContainer, mFragment, "contactDetailFragment")
        // Show the Up button in the action bar.
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        var upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        supportActionBar.setHomeAsUpIndicator(upArrow);

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

    public fun sendMessage(view: View) {
	    val message = messageEditText.text.toString();
	    messageEditText.setText("");
	    if (!message.isNullOrBlank())
		    mFragment.sendMessage(message);
    }

    override protected fun onStop() {
        //		Must be called when your application is done using GCM, to release internal resources.
        GoogleCloudMessaging.getInstance(this).close();
        super.onStop();
    }

    override public fun onOptionsItemSelected(item: MenuItem): Boolean {
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
