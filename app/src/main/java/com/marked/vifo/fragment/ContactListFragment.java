package com.marked.vifo.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.marked.vifo.R;
import com.marked.vifo.adapter.ContactsAdapter;
import com.marked.vifo.extra.GCMConstants;
import com.marked.vifo.model.Contacts;
import com.marked.vifo.model.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * A list fragment representing a list of Contacts. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ContactDetailFragment}.
 * <p>
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
	private RequestQueue mQueue;


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
		mQueue = RequestQueue.getInstance(mContext);
		requestListFriends();

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

	/**
	 * Use the token to send a request to the server for an array of friends for the user of the app
	 */
	private void requestListFriends() {
		String id = getDefaultSharedPreferences(mContext).getString(GCMConstants.USER_ID, null);
		if (id != null) {
			JsonRequest request = new JsonObjectRequest(Request.Method.GET,
					                                           GCMConstants.SERVER_USER_FRIENDS +
					                                           "?id=" +
					                                           id, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					try {
						JSONArray friends = response.getJSONArray("friends");
						if (friends.length() != mContacts.getContacts().size()) { // only add friends
							mContacts.setFriends(Contacts.fromJSONArray(friends));
							((Activity) mContext).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									mContactsAdapter
											.notifyItemRangeInserted(0, mContacts.getContacts().size() - 1);
								}
							});
						}else {
//							Log.d("***", "onCreateView "+mContacts.getContacts().get(0).toString());
//							Log.d("***", "onResponse " + mContacts.getContacts());
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO: 12-Dec-15 add empty view
				}
			});
			mQueue.add(request);
		}
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


	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(String id);
	}
}
