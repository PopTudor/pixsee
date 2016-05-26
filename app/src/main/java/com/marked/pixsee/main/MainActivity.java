package com.marked.pixsee.main;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.marked.pixsee.R;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.friends.cards.CardFragment;
import com.marked.pixsee.friends.cards.CardPresenter;
import com.marked.pixsee.friends.cards.data.CardLocalDatasource;
import com.marked.pixsee.friends.cards.data.CardRemoteDatasource;
import com.marked.pixsee.friends.cards.data.CardRepository;
import com.marked.pixsee.friends.friends.FriendFragment;
import com.marked.pixsee.friends.friends.data.User;
import com.marked.pixsee.main.commands.SelfieCommand;
import com.marked.pixsee.main.di.DaggerMainComponent;
import com.marked.pixsee.main.di.MainModule;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements MainContract.View,FriendFragment.FriendFragmentInteractionListener,CardFragment.OnFragmentInteractionListener {
	@Inject
	MainContract.Presenter mPresenter;

	private FrameLayout mainContainer;
	private ImageButton mChatImageButton;
	private ImageButton mSelfieImageButton;
	private ImageButton mProfileImageButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DaggerMainComponent.builder().mainModule(new MainModule(this)).build().inject(this);
		mainContainer = (FrameLayout) findViewById(R.id.mainContainer);
		mChatImageButton = (ImageButton) findViewById(R.id.chatImageButton);
		mSelfieImageButton = (ImageButton) findViewById(R.id.selfieImageButton);
		mProfileImageButton = (ImageButton) findViewById(R.id.profileImageButton);


		mChatImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.chatClicked();
			}
		});
		mSelfieImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.execute(new SelfieCommand(MainActivity.this));
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPresenter.start();
	}

	@Override
	public void setPresenter(MainContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void displayChat(boolean show) {
		Fragment fragment = FriendFragment.newInstance();
		getSupportFragmentManager().beginTransaction().add(R.id.mainContainer, fragment).commit();
	}

	@Override
	public void onFriendClick(User friend) {
		CardFragment fragment = CardFragment.newInstance(friend);
		CardLocalDatasource localDatasource = new CardLocalDatasource(PixyDatabase.getInstance(this));
		CardRemoteDatasource remoteDatasource = new CardRemoteDatasource(PreferenceManager.getDefaultSharedPreferences(this));
		fragment.setPresenter(new CardPresenter(fragment, new CardRepository(localDatasource,remoteDatasource)));
		getSupportFragmentManager().beginTransaction().replace(R.id.cardFragmentContainer, fragment).commit();
	}

	@Override
	public void onFragmentInteraction(Uri uri) {

	}
}
