package com.marked.pixsee.ui.entry;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.marked.pixsee.Pixsee;
import com.marked.pixsee.R;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.ui.authentification.AuthenticationActivity;
import com.marked.pixsee.ui.main.MainActivity;

import javax.inject.Inject;

import bolts.AppLinks;


public class EntryActivity extends AppCompatActivity {
	private Intent whatToStartIntent;
	@Inject
	UserManager mManager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
		if (targetUrl != null) {
			Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
		}
		DaggerActivityComponent
				.builder()
				.sessionComponent(Pixsee.getSessionComponent())
				.activityModule(new ActivityModule(this))
				.build()
				.inject(this);
	}

	public void onResume() {
		super.onResume();
		if (mManager.getAppUser().getId() != null) {
			whatToStartIntent = new Intent(this, MainActivity.class);
			whatToStartIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		} else {
			whatToStartIntent = new Intent(this, AuthenticationActivity.class);
		}
		prepareFriendRequest();
		startActivity(whatToStartIntent);
		finish();
	}

	private void prepareFriendRequest() {
		String action = getIntent().getAction();
		if (action!=null && action.equals(getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION))) {
			whatToStartIntent.putExtra(getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION), new User(getIntent().getExtras()));
		}
	}
}
