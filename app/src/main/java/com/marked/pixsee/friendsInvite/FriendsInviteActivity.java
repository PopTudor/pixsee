package com.marked.pixsee.friendsInvite;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.marked.pixsee.R;
import com.marked.pixsee.friendsInvite.addUsername.AddUsernameFragment;
import com.marked.pixsee.friendsInvite.commands.ClickAddUser;

public class FriendsInviteActivity extends AppCompatActivity implements FriendsInviteContract.View,View.OnClickListener {
	private FriendsInviteContract.Presenter mPresenter;
	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_friends);
		mPresenter = new Presenter(this);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Add friends");
		toolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(this,R.color.teal), PorterDuff.Mode.SRC_ATOP);
		toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.teal));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.teal_dark));
		}

		findViewById(R.id.addUsername).setOnClickListener(this);
		findViewById(R.id.shareUsername).setOnClickListener(this);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setPresenter(FriendsInviteContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.addUsername:
				mPresenter.execute(new ClickAddUser(this,R.id.fragmentContainer, AddUsernameFragment.newInstance()));
				break;
			case R.id.shareUsername:
				mPresenter.execute(new ClickAddUser(this,R.id.fragmentContainer, AddUsernameFragment.newInstance()));
				break;
			default:
				break;
		}

	}
}
