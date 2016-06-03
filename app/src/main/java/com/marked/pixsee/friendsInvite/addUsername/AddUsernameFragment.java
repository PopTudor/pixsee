package com.marked.pixsee.friendsInvite.addUsername;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.friends.data.User;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddUsernameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddUsernameFragment extends Fragment implements MenuItemCompat.OnActionExpandListener, AddUsernameContract.View {
	private AddUsernameContract.Presenter mPresenter;

	public static AddUsernameFragment newInstance() {
		AddUsernameFragment fragment = new AddUsernameFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mPresenter = new Presenter(this);
	}

	public AddUsernameFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_add_username, container, false);
		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_add_username, menu);
		MenuItem menuItem = menu.findItem(R.id.action_search);
		MenuItemCompat.setOnActionExpandListener(menuItem,this);
		menuItem.expandActionView();
		SearchView searchView = ((SearchView) menuItem.getActionView());
		searchView.setQueryHint("Username or email");
	}


	@Override
	public boolean onMenuItemActionExpand(MenuItem item) {
		return true;
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem item) {
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

	}
}
