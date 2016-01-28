package com.marked.vifo.ui.fragment

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.marked.vifo.R
import com.marked.vifo.extra.GCMConstants
import com.marked.vifo.extra.ServerConstants
import com.marked.vifo.model.Contacts
import com.marked.vifo.model.contactListfromJSONArray
import com.marked.vifo.model.requestQueue
import com.marked.vifo.ui.adapter.ContactsAdapter
import kotlinx.android.synthetic.main.fragment_contact_list.view.*
import org.jetbrains.anko.support.v4.onUiThread
import org.json.JSONObject

/**
 * A list fragment representing a list of Contacts. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a [ContactDetailFragment].
 *
 *
 * Activities containing this fragment MUST implement the [Callbacks]
 * interface.
 */
class ContactListFragment : Fragment() {
	private val mContext by lazy { activity }

	private val mContactsInstance by lazy { Contacts.getInstance(mContext) }
	private val mContactsAdapter by lazy { ContactsAdapter(mContext, mContactsInstance.getContact()) }
	private val mLayoutManager by lazy { LinearLayoutManager(mContext) }

	private var mCallbacks: Callbacks? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		requestListFriends()

		//		attachListeners();
		//		mSocket.on("hi", onNewRoom);
		//		mSocket.emit("room",new JSONObject());
		//		mSocket.connect();
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val rootView = inflater.inflate(R.layout.fragment_contact_list, container, false)
		rootView.contactRecyclerView.adapter = mContactsAdapter
		rootView.contactRecyclerView.layoutManager = mLayoutManager
		return rootView
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		inflater?.inflate(R.menu.menu_contacts_activity, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override
	fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.refreshContacts -> {
				requestListFriends()
				return true
			}
			else -> return super.onOptionsItemSelected(item)
		}
	}

	/**
	 * Use the token to send a request to the server for an array of friends for the user of the app
	 */
	private fun requestListFriends() {
		val id = getDefaultSharedPreferences(mContext).getString(GCMConstants.USER_ID, null)
		if (id != null) {
			val request = JsonObjectRequest(Request.Method.GET,
					"${ServerConstants.SERVER_USER_FRIENDS}?id=$id",
					Listener<JSONObject> { response ->
						val friends = response.getJSONArray("friends")
						val friendsArray = friends.contactListfromJSONArray()

						mContactsInstance.addContact(friendsArray)
						onUiThread { mContactsAdapter.notifyDataSetChanged() }
					}, ErrorListener {

				Log.d("***", "Error")
			})// TODO: 12-Dec-15 add empty view)
			request.setRetryPolicy(DefaultRetryPolicy(50000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))

			mContext.requestQueue.add(request)
		}
	}

	override fun onAttach(activity: Context?) {
		super.onAttach(activity)
		// Activities containing this fragment must implement its callbacks.
		if (activity !is Callbacks) {
			throw IllegalStateException("Activity must implement fragment's callbacks.")
		}
		mCallbacks = activity
	}

	override fun onDetach() {
		super.onDetach()
		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = null
		//		dettachListeners();
	}

	//	public void attachListeners(){
	//		for (Contact contact:mContacts.getContacts())
	//			mSocket.on(contact.getId(), onNewRoom);
	//	}
	//	public void dettachListeners(){
	//		for (Contact contact:mContacts.getContacts())
	//			mSocket.off(contact.getId(), onNewRoom);
	//	}


	interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		fun onItemSelected(id: String)
	}

	companion object {

		fun newInstance(): ContactListFragment {
			return ContactListFragment()
		}
	}
}//
//	private Socket mSocket;
//	{
//		try {
//			mSocket = IO.socket(GCMConstants.SERVER);
//		} catch (URISyntaxException e) {
//		}
//	}
//
//	public static String room;
//
//	private Emitter.Listener onNewRoom = new Emitter.Listener() {
//		@Override
//		public void call(final Object... args) {
//			new Handler(Looper.getMainLooper()).post(new Runnable() {
//				@Override
//				public void run() {
////					room = (String) args[0];
//
//
//					// add the message to view
//					//					addMessage(username, message);
//					Toast.makeText(getActivity(), "newRoom TOAAST ", Toast.LENGTH_SHORT).show();
//					//					room = (String) args[0];
//				}
//
//			});
//		}
//	};
/**
 * A dummy implementation of the [Callbacks] interface that does
 * nothing. Used only when this fragment is not attached to an activity.
 */
