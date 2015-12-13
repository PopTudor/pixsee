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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marked.vifo.R;
import com.marked.vifo.extra.GCMConstants;
import com.marked.vifo.fragment.ContactDetailFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

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
	private Socket mSocket;
	{
		try {
			mSocket = IO.socket(GCMConstants.SERVER);
		} catch (URISyntaxException e) {}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    mSocket.on("new message", onNewMessage);
	    mSocket.connect();

	    setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
	    mMesageEditText = (EditText) findViewById(R.id.messageEditText);

	    mFragmentManager = getSupportFragmentManager();
	    mFragmentManager.beginTransaction().add(R.id.fragmentContainer, ContactDetailFragment.newInstance(getIntent().getParcelableExtra(EXTRA_CONTACT)),"contactDetailFragment").commit();

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    final Drawable upArrow = ContextCompat.getDrawable(this,R.drawable.abc_ic_ab_back_mtrl_am_alpha);
	    upArrow.setColorFilter(ContextCompat.getColor(this,R.color.white), PorterDuff.Mode.SRC_ATOP);
	    getSupportActionBar().setHomeAsUpIndicator(upArrow);

    }
	private Emitter.Listener onNewMessage = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			ContactDetailActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					JSONObject data = (JSONObject) args[0];
					String username;
					String message;
					try {
						username = data.getString("username");
						message = data.getString("message");
					} catch (JSONException e) {
						return;
					}

					// add the message to view
//					addMessage(username, message);
					Log.d("***", "run " + username + "/" + message);
				}
			});
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSocket.disconnect();
		mSocket.off("new message", onNewMessage);
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

	public void sendMessage(View view){
		String message = mMesageEditText.getText().toString();
		mMesageEditText.setText("");
		((ContactDetailFragment) mFragmentManager.findFragmentById(R.id.fragmentContainer)).sendMessage(message);
		mSocket.emit("new message", message);
	}

}
