package com.marked.pixsee.friends;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.marked.pixsee.R;
import com.marked.pixsee.data.User;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.friends.cards.CardFragment;
import com.marked.pixsee.friends.cards.CardPresenter;
import com.marked.pixsee.data.message.MessageRepository;
import com.marked.pixsee.friends.commands.FabCommand;
import com.marked.pixsee.friends.di.DaggerFriendsComponent;
import com.marked.pixsee.friends.di.FriendModule;
import com.marked.pixsee.friends.friends.FriendFragment;
import com.marked.pixsee.friends.friends.FriendPresenter;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.injection.modules.ActivityModule;

import javax.inject.Inject;


/**
 * An activity representing a list of Contacts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ChatDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * [FriendFragment] and the item details
 * (if present) is a [ContactDetailFragment].
 * <p/>
 * <p/>
 * This activity also implements the required
 * [FriendFragment.Callbacks] interface
 * to listen for item selections.
 */
public class FriendsActivity extends AppCompatActivity implements FriendFragment.FriendFragmentInteractionListener, CardFragment
		                                                                                                                    .OnFragmentInteractionListener {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane = false;
	@Inject
	FriendPresenter friendPresenter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_master);
		FriendFragment friendFragment = FriendFragment.newInstance();

		ActivityComponent activityComponent = DaggerActivityComponent.builder().activityModule(new ActivityModule(this)).build();
		DaggerFriendsComponent.builder()
				.activityComponent(activityComponent)
				.friendModule(new FriendModule(friendFragment))
				.build().inject(this);

		getSupportFragmentManager().beginTransaction().add(R.id.friendFragmentContainer, friendFragment).commit();
//		getSupportFragmentManager().beginTransaction().add(R.id.messageFragmentContainer, cardFragment).commit();
		findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				friendPresenter.execute(new FabCommand(FriendsActivity.this));
			}
		});
	}


	@Override
	public void onFragmentInteraction(Uri uri) {

	}

	@Override
	public void onFriendClick(User friend) {
		CardFragment fragment = CardFragment.newInstance(friend);
		fragment.setPresenter(new CardPresenter(fragment, new MessageRepository(PixyDatabase.getInstance(this))));
		getSupportFragmentManager().beginTransaction().replace(R.id.friendFragmentContainer, fragment).commit();
	}
	/* // uncomment this when you add FloatingActionMenu
	override fun onBackPressed() {
		if (!((mFragmentManager.findFragmentById(R.id.fragmentContainer) as ContactListFragment)).closeFAM()) // == true if it was already closed
			super.onBackPressed()

	}*/
}
