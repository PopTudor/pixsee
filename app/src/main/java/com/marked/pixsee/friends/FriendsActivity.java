package com.marked.pixsee.friends;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.R;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.injection.modules.ActivityModule;


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
public class FriendsActivity extends AppCompatActivity {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane = false;

	private ActivityComponent mComponent;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_master);
		mComponent = DaggerActivityComponent.builder().activityModule(new ActivityModule(this)).build();

		getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, FriendFragment.newInstance()).commit();
	}

	public ActivityComponent getComponent() {
		return mComponent;
	}
	/* // uncomment this when you add FloatingActionMenu
	override fun onBackPressed() {
		if (!((mFragmentManager.findFragmentById(R.id.fragmentContainer) as ContactListFragment)).closeFAM()) // == true if it was already closed
			super.onBackPressed()

	}*/
}
