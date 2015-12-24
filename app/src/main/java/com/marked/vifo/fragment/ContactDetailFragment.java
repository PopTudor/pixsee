package com.marked.vifo.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marked.vifo.R;
import com.marked.vifo.activity.ContactDetailActivity;
import com.marked.vifo.activity.ContactListActivity;
import com.marked.vifo.adapter.MessageAdapter;
import com.marked.vifo.extra.GCMConstants;
import com.marked.vifo.extra.MessageConstants;
import com.marked.vifo.gcm.service.GCMListenerService;
import com.marked.vifo.helper.SpacesItemDecoration;
import com.marked.vifo.model.Contact;
import com.marked.vifo.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * A fragment representing a single Contact detail screen.
 * This fragment is either contained in a {@link ContactListActivity}
 * in two-pane mode (on tablets) or a {@link ContactDetailActivity}
 * on handsets.
 */
public class ContactDetailFragment extends Fragment implements GCMListenerService.Callbacks {
	private static boolean mIsInForegroundMode;
	private ArrayList<Message> mMessagesDataset;
	private MessageAdapter mMessageAdapter;
	private LinearLayoutManager mLinearLayoutManager;
	private RecyclerView mMessagesRecyclerView;
	private Context mContext;
	private Contact mUser;
	
	private ContactDetailFragmentInteraction mCallback;
	private Emitter.Listener onNewMessage = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					JSONObject data = (JSONObject) args[0];
					String username;
					String message;
					try {
						username = data.getString("username");
						message = data.getString("message");
					} catch (JSONException e) {
						return;
					}

					// add the message to view
					//					addMessage(username, message);
					Log.d("***", "run " + username + "/" + message);
				}
			});
		}
	};
	private Socket mSocket;

	{
		try {
			mSocket = IO.socket(GCMConstants.SERVER);
		} catch (URISyntaxException e) {
		}
	}

	public ContactDetailFragment() {
		/**
		 * Mandatory empty constructor for the fragment manager to instantiate the
		 * fragment (e.g. upon screen orientation changes).
		 */
	}

	public static ContactDetailFragment newInstance(Parcelable parcelable) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(ContactDetailActivity.EXTRA_CONTACT, parcelable);
		ContactDetailFragment fragment = new ContactDetailFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	/**
	 * Check if the user is using the app
	 *
	 * @return if the app is in foreground or not
	 */
	public static boolean isInForeground() {
		return mIsInForegroundMode;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mSocket.disconnect();
		mSocket.off("new message", onNewMessage);
	}

	public void sendMessage(String messageText) throws JSONException {
		String id = getDefaultSharedPreferences(mContext).getString(GCMConstants.USER_ID, null);
		Message message = new Message.Builder()
				                  .addData(MessageConstants.DATA_BODY, messageText)
				                  .from(id)
				                  .to(mUser.getId())
				                  .build();
		//		doGcmSendUpstreamMessage(message);
		JSONObject jsonObject = message.toJSON();

		mSocket.emit("new message", jsonObject);
		/*UI update*/
		mMessagesDataset.add(message);
		mMessageAdapter.notifyItemInserted(mMessagesDataset.size());
		mMessagesRecyclerView.scrollToPosition(mMessagesDataset.size() - 1);
	}

	@Override
	public void receiveMessage(String from, Bundle data) {
		Message message = new Message.Builder().addData(data)
		                                       .viewType(MessageConstants.MessageType.YOU).build();
		mMessagesDataset.add(message);
		((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mMessageAdapter.notifyItemInserted(mMessagesDataset.size());
				mMessagesRecyclerView.scrollToPosition(mMessagesDataset.size() - 1);
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mSocket.on("new message", onNewMessage);
		mSocket.on("news", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Log.d("***", "call ");
			}
		});

		mSocket.connect();
		if (getArguments() != null) {
			mUser = getArguments().getParcelable(ContactDetailActivity.EXTRA_CONTACT);
		}
		GCMListenerService.setCallbacks(this);
		mMessagesDataset = new ArrayList<>();
		mMessageAdapter = new MessageAdapter(mContext, mMessagesDataset);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_contact_detail, container, false);
		mMessagesRecyclerView = (RecyclerView) rootView.findViewById(R.id.messagesRecyclerView);

		mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,
		                                               false);
		mMessagesRecyclerView.setLayoutManager(mLinearLayoutManager);
		mMessagesRecyclerView.addItemDecoration(new SpacesItemDecoration(15));

		mMessagesRecyclerView.setAdapter(mMessageAdapter);
		mMessagesRecyclerView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = ((AppCompatActivity) mContext).getCurrentFocus();
				if (view != null) {
					InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
							Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				return false;
			}
		});

		return rootView;
	}

	private void doGcmSendUpstreamMessage(Message message) {
		final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
		final String senderId = getString(R.string.gcm_defaultSenderId);
		final String msgId = UUID.randomUUID().toString();
		final String token = mUser.getToken();
		final Bundle data = message.toBundle();
		data.putString(GCMConstants.TOKEN, token);

		if (msgId.isEmpty())
			return;

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
					Toast.makeText(mContext, "send message failed: " + result, Toast.LENGTH_LONG)
					     .show();
				}
			}
		}.execute(null, null, null);
	}

	@Override
	public void onPause() {
		super.onPause();
		mIsInForegroundMode = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		mIsInForegroundMode = true;
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

	public interface ContactDetailFragmentInteraction {
	}
}
