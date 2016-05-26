package com.marked.pixsee.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.marked.pixsee.R;
import com.marked.pixsee.friends.FriendsActivity;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements MainContract.View {
	private FrameLayout mainContainer;
	@Inject
	MainContract.Presenter mPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainContainer = (FrameLayout) findViewById(R.id.mainContainer);
		findViewById(R.id.chatImageButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,FriendsActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void setPresenter(MainContract.Presenter presenter) {
		mPresenter = presenter;
	}
}
