package com.marked.vifo.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marked.vifo.R;
import com.marked.vifo.activity.ContactDetailActivity;
import com.marked.vifo.activity.ContactListActivity;
import com.marked.vifo.adapter.MessageAdapter;
import com.marked.vifo.extra.GCMConstants;
import com.marked.vifo.extra.MessageConstants;
import com.marked.vifo.model.Contact;
import com.marked.vifo.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A fragment representing a single Contact detail screen.
 * This fragment is either contained in a {@link ContactListActivity}
 * in two-pane mode (on tablets) or a {@link ContactDetailActivity}
 * on handsets.
 */
public class ContactDetailFragment extends Fragment {
	RecyclerView mMessagesRecyclerView;
	ArrayList<Message> mMessagesDataset;
	Context mContext;
	ContactDetailFragmentInteraction mCallback;
	/**
	 * The user that we are talking with content this fragment is presenting.
	 */
	private Contact mItem;


	public ContactDetailFragment() {
		/**
		 * Mandatory empty constructor for the fragment manager to instantiate the
		 * fragment (e.g. upon screen orientation changes).
		 */
	}

	public static ContactDetailFragment newInstance() {
		Bundle bundle = new Bundle();
		ContactDetailFragment fragment = new ContactDetailFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mCallback = (ContactDetailFragmentInteraction) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + " must implement " +
			                             ContactDetailFragmentInteraction.class);
		}
	}

	public void sendMessage(String message) {
		doGcmSendUpstreamMessage(message);
		Message message1 = new Message.Builder().addData("message", message).build();
		mMessagesDataset.add(message1);
		mMessagesRecyclerView.getAdapter().notifyItemInserted(mMessagesDataset.size());
		mMessagesRecyclerView.scrollToPosition(mMessagesDataset.size() - 1);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	MessageAdapter mMessageAdapter;
	LinearLayoutManager mLinearLayoutManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_contact_detail, container, false);
		mMessagesRecyclerView = (RecyclerView) rootView.findViewById(R.id.messagesRecyclerView);

		mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
		mMessagesRecyclerView.setLayoutManager(mLinearLayoutManager);

		mMessagesDataset = new ArrayList<>();
		mMessageAdapter = new MessageAdapter(mContext, mMessagesDataset);
		mMessagesRecyclerView.setAdapter(mMessageAdapter);
		mMessagesRecyclerView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = ((AppCompatActivity)mContext).getCurrentFocus();
				if (view != null) {
					InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				return false;
			}
		});

		return rootView;
	}

	private void doGcmSendUpstreamMessage(String message) {
		final Activity activity = getActivity();
		final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity);
		final String senderId = getString(R.string.gcm_defaultSenderId);
		final String msgId = UUID.randomUUID().toString();
		//		final String timeToLive = getValue(R.id.upstream_ttl); // time to live is by defaul 4 weeks
		final Bundle data = new Bundle();
		data.putString(MessageConstants.MESSAGE_KEY, message);

		if (msgId.isEmpty()) {
			return;
		}

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				try {
					gcm.send(senderId + GCMConstants.SERVER_UPSTREAM_ADRESS, msgId, data);
					return null;
				} catch (IOException ex) {
					return "Error sending upstream message:" + ex.getMessage();
				}
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					Toast.makeText(activity,
							"send message failed: " + result, Toast.LENGTH_LONG).show();
				}
			}
		}.execute(null, null, null);
	}

	public interface ContactDetailFragmentInteraction {
	}

}
