package com.marked.vifo.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marked.vifo.R;
import com.marked.vifo.fragment.ContactDetailFragment;

import org.json.JSONException;

/**
 * An activity representing a single Contact detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ContactListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ContactDetailFragment}.
 */
public class ContactDetailActivity extends AppCompatActivity implements ContactDetailFragment.ContactDetailFragmentInteraction {
	public static final String EXTRA_CONTACT="com.marked.vifo.activity.EXTRA_CONTACT";


	EditText mMesageEditText;
	FragmentManager mFragmentManager;
	ContactDetailFragment mFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_contact_detail);
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		    getWindow().setAllowEnterTransitionOverlap(true);
	    }
	    mFragmentManager = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
	    mMesageEditText = (EditText) findViewById(R.id.messageEditText);

	    mFragment = ContactDetailFragment.newInstance(getIntent().getParcelableExtra(EXTRA_CONTACT));
	    /*send the clicked contact to the fragment*/
	    mFragmentManager.beginTransaction().add(R.id.fragmentContainer,mFragment,"contactDetailFragment").commit();
	    // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    final Drawable upArrow = ContextCompat.getDrawable(this,R.drawable.abc_ic_ab_back_mtrl_am_alpha);
	    upArrow.setColorFilter(ContextCompat.getColor(this,R.color.white), PorterDuff.Mode.SRC_ATOP);
	    getSupportActionBar().setHomeAsUpIndicator(upArrow);
	    mMesageEditText.addTextChangedListener(new TextWatcher() {
		    private boolean mTyping;

		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		    }

		    @Override
		    public void onTextChanged(CharSequence s, int start, int before, int count) {
			    if (!mTyping && count>0) {
				    mTyping = true;
				    mFragment.onTyping(mTyping);
			    }
			    if (mTyping && count==0) {
				    mTyping = false;
				    mFragment.onTyping(mTyping);
			    }
		    }

		    @Override
		    public void afterTextChanged(Editable s) {

		    }
	    });

    }
	public void sendMessage(View view) throws JSONException {
		String message = mMesageEditText.getText().toString();
		mMesageEditText.setText("");
		if(!message.isEmpty())
			mFragment.sendMessage(message);
	}
	@Override
	protected void onStop() {
//		Must be called when your application is done using GCM, to release internal resources.
		GoogleCloudMessaging.getInstance(this).close();
		super.onStop();
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		        navigateUpTo(new Intent(this, ContactListActivity.class));
	        }
	        return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
