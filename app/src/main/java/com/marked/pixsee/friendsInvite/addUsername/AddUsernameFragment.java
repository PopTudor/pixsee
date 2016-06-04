package com.marked.pixsee.friendsInvite.addUsername;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.marked.pixsee.R;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.friendsInvite.addUsername.di.AddUserModule;
import com.marked.pixsee.friendsInvite.addUsername.di.DaggerAddUserComponent;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.injection.modules.ActivityModule;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddUsernameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddUsernameFragment extends Fragment implements MenuItemCompat.OnActionExpandListener, AddUsernameContract.View {
	@Inject
	AddUsernameContract.Presenter mPresenter;
	private RecyclerView mUsersRecyclerview;
	private UsersAdapter mUsersAdapter;
	private Subscription subscription;

	public static AddUsernameFragment newInstance() {
		AddUsernameFragment fragment = new AddUsernameFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		ActivityComponent component =  DaggerActivityComponent.builder()
				.activityModule(new ActivityModule((AppCompatActivity) getActivity())).build();
		DaggerAddUserComponent.builder()
				.activityComponent(component)
				.addUserModule(new AddUserModule(this))
				.build()
				.inject(this);
	}

	public AddUsernameFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_add_username, container, false);
		mUsersRecyclerview = (RecyclerView) view.findViewById(R.id.usersRecyclerview);
		mUsersRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

		mUsersAdapter = new UsersAdapter(mPresenter);
		mUsersRecyclerview.setAdapter(mUsersAdapter);

		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_add_username, menu);
		MenuItem menuItem = menu.findItem(R.id.action_search);
		MenuItemCompat.setOnActionExpandListener(menuItem, this);
		menuItem.expandActionView();
		SearchView searchView = ((SearchView) menuItem.getActionView());
		searchView.setQueryHint("Username or email");
		subscription = RxSearchView.queryTextChanges(searchView)
				// check if it’s not empty (user removed text), if it is
				// observable chain will stop here until user enters something.
				.filter(new Func1<CharSequence, Boolean>() {
					@Override
					public Boolean call(CharSequence charSequence) {
						if (charSequence != null && charSequence.length() >= 2)
							return true;
						mUsersAdapter.getUsersList().clear();
						mUsersAdapter.notifyDataSetChanged();
						return false;
					}
				})
				// to prevent making requests too fast (as user may type fast),
				// throttleLast will emit last item during 100ms from the time
				// first item is emitted
				.throttleLast(300, TimeUnit.MILLISECONDS)
				// debounce will emit item only 200ms after last item is emitted
				// (after user types in last character)
				.debounce(500, TimeUnit.MILLISECONDS)
				.subscribe(new Action1<CharSequence>() {
					           @Override
					           public void call(CharSequence charSequence) {
						           mPresenter.search(charSequence.toString());
					           }
				           });
	}


	@Override
	public boolean onMenuItemActionExpand(MenuItem item) {
		return true;
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem item) {
		subscription.unsubscribe();
		/* when searchview is collapsed, exit this fragment and return to original invite screen*/
		getActivity().onBackPressed();
		return false;
	}

	@Override
	public void setPresenter(AddUsernameContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void showUsers(List<User> users) {
		mUsersAdapter.getUsersList().clear();
		mUsersAdapter.getUsersList().addAll(users);
		mUsersAdapter.notifyDataSetChanged();
	}

	@Override
	public void showNoInternetConnection() {
		Toast.makeText(getActivity(), R.string.alert_internet_connection, Toast.LENGTH_SHORT).show();
	}
}
