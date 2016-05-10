package com.marked.pixsee.store;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.data.User;
import com.marked.pixsee.databinding.ItemFriendBinding;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.friends.ItemFriendViewModel;
import com.marked.pixsee.store.di.DaggerShopComponent;
import com.marked.pixsee.store.di.ShopModule;
import com.marked.pixsee.store.list.ShopListFragment;
import com.marked.pixsee.utility.UtilsFragmentKt;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.facebook.common.internal.Preconditions.checkNotNull;

public class ShopActivity extends AppCompatActivity {
	@Inject
	ShopPresenter shopPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);
		// Set up the toolbar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.ic_arrow_24dp);
		ab.setDisplayHomeAsUpEnabled(true);
		toolbar.setTitle("Emotions Market");

		ShopListFragment listFragment = (ShopListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
		if (listFragment == null) {
			listFragment = ShopListFragment.newInstance();
			UtilsFragmentKt.add(getSupportFragmentManager(), R.id.fragmentContainer, listFragment);
		}
		DaggerShopComponent.builder()
				.shopModule(new ShopModule(listFragment))
				.build()
				.inject(this);
	}

	class FriendsAdapter extends RecyclerView.Adapter<FriendVH> {
		private List<User> mDataSet;
		private FriendFragment.FriendItemListener friendItemListener;

		FriendsAdapter(@NotNull FriendFragment.FriendItemListener friendItemListener) {
			this.friendItemListener = checkNotNull(friendItemListener);
			mDataSet = new ArrayList<User>();
		}

		@Override
		public FriendVH onCreateViewHolder(ViewGroup parent, int viewType) {
			ItemFriendBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_friend, parent, false);
			return new FriendVH(binding);
		}

		public void setDataSet(List<User> dataSet) {
			mDataSet = dataSet;
		}

		@Override
		public void onBindViewHolder(FriendVH holder, int position) {
			final User friend = mDataSet.get(position);
			holder.bindContact(friend);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// itemView is a public member that points to the root element of the contactHolder (in the layout)
					// TODO: 03-Dec-15 send contact detail from here to ContactDetailActivity
					// send the clicked contact to detail activity
					friendItemListener.onFriendClick(friend);
				}
			});
		}

		// Return the size of your mDataSet (invoked by the layout manager)

		@Override
		public int getItemCount() {
			return mDataSet.size();
		}
	}

	class FriendVH extends RecyclerView.ViewHolder {
		private ItemFriendBinding mBinding;

		public FriendVH(@NotNull ItemFriendBinding binding) {
			super(binding.rootItemFriend);
			this.mBinding = binding;
		}


		// Define click listener for the ViewHolder's View.
		//            v.setOnClickListener(new View.OnClickListener() {
		//                @Override
		//                public void onClick(View v) {
		//	                Intent intent = new Intent(context, ContactDetailActivity.class);
		//	                context.startActivity(new Intent(context, ContactDetailActivity.class));
		//                }
		//            });


		public void bindContact(User friend) {
			//		val uri = Uri.parse(contact);
			//		mFriendIconImageView.setImageURI(uri, context);
			//todo get in parse a contact's profile image
			if (mBinding.getViewModel() == null)
				mBinding.setViewModel(new ItemFriendViewModel(friend));
			else
				mBinding.getViewModel().setUser(friend);
		}

	}
}
