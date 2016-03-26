package com.marked.pixsee.friends;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.marked.pixsee.R;
import com.marked.pixsee.di.components.ActivityComponent;
import com.marked.pixsee.di.components.DaggerActivityComponent;
import com.marked.pixsee.di.modules.ActivityModule;
import com.marked.pixsee.utility.UtilsFragmentKt;


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
	private FragmentManager mFragmentManager;

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane = false;

	private ActivityComponent mComponent;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_master);
		mFragmentManager = getSupportFragmentManager();
		mComponent = DaggerActivityComponent.builder().activityModule(new ActivityModule(this)).build();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (toolbar != null) {
			toolbar.setTitle(getTitle());
		}
		UtilsFragmentKt.add(mFragmentManager, R.id.fragmentContainer, FriendFragment.newInstance());
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
