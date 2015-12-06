package com.marked.vifo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.vifo.R;
import com.marked.vifo.adapter.ContactsAdapter;
import com.marked.vifo.model.Contact;
import com.marked.vifo.model.Contacts;

/**
 * A list fragment representing a list of Contacts. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ContactDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ContactListFragment extends Fragment {
	private Callbacks mCallbacks;
	private RecyclerView mRecyclerView;
	private ContactsAdapter mContactsAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private Context mContext;

	private Contacts mContacts;


	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */

	public ContactListFragment() {
	}

	public static ContactListFragment newInstance() {
		return new ContactListFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mContacts = Contacts.getInstance(getActivity());
		for (int i = 0; i < 10; i++) {
			mContacts.addContact(new Contact(i + " bacon", Math.random() + "", i + ""));
		}


		mContactsAdapter = new ContactsAdapter(mContext, mContacts.getContacts());
		mLayoutManager = new LinearLayoutManager(mContext);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.contactRecyclerView);
		mRecyclerView.setAdapter(mContactsAdapter);
		mRecyclerView.setLayoutManager(mLayoutManager);

		return rootView;
	}

	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = null;
	}

	public void sendMessage(View view) {
		Log.d("***", "sendMessage ");
	}

	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(String id);
	}
}
