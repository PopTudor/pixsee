package com.marked.vifo.fragment;

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
import com.marked.vifo.extra.ServerConstants;
import com.marked.vifo.gcm.service.GCMListenerService;
import com.marked.vifo.helper.SpacesItemDecoration;
import com.marked.vifo.helper.Utils;
import com.marked.vifo.model.Contact;
import com.marked.vifo.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static java.lang.String.format;

/**
 * A fragment representing a single Contact detail screen.
 * This fragment is either contained in a {@link ContactListActivity}
 * in two-pane mode (on tablets) or a {@link ContactDetailActivity}
 * on handsets.
 */
public class ContactDetailFragment extends Fragment implements GCMListenerService.Callbacks {
	public static final String ON_NEW_MESSAGE = "onMessage";
	public static final String ON_NEW_ROOM = "onRoom";
	public static final String ON_TYPING = "onTyping";
	/**
	 * keep track if the user is interacting with the app. If not, disconnect the socket
	 */
	private static boolean mIsInForegroundMode;
	private ArrayList<Message> mMessagesDataset;
	private MessageAdapter mMessageAdapter;
	private LinearLayoutManager mLinearLayoutManager;
	private RecyclerView mMessagesRecyclerView;
	private Context mContext;
	private String mThisUser;
	private Contact mOtherUser; // this user (app user)
	private Socket mSocket;
	private ContactDetailFragmentInteraction mCallback;
	private Emitter.Listener onMessage = onNewMessage();
	private Emitter.Listener onTyping = onTyping();

	{
		try {
			mSocket = IO.socket(ServerConstants.SERVER);
		} catch (URISyntaxException e) {
			Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
		}
	}

	public ContactDetailFragment() {
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

	public void sendMessage(String messageText) throws JSONException {
		Message message = new Message.Builder().addData(MessageConstants.DATA_BODY, messageText).from(mThisUser).to(mOtherUser.getId()).build();
		//		doGcmSendUpstreamMessage(message);
		JSONObject jsonObject = message.toJSON();

		mSocket.emit(ON_NEW_MESSAGE, jsonObject);
		addMessage(message);
	}

	/**
	 * This is used to receive messages from socket.io
	 *
	 * @param from
	 * @param data
	 */
	@Override
	public void receiveMessage(String from, Bundle data) {
		Message message = new Message.Builder().addData(data).viewType(MessageConstants.MessageType.YOU).build();
		addMessage(message);
	}

	/** Add message to dataset and notify the adapter of the change
	 * @param message the message to add
	 */
	private void addMessage(Message message) {
		mMessagesDataset.add(message);
		mMessageAdapter.notifyItemInserted(mMessagesDataset.size()-1);
		mMessagesRecyclerView.scrollToPosition(mMessagesDataset.size() - 1);
	}

	/**
	 * Remove message from dataset and notify the adapter
	 */
	private void removeMessage() {
		if (mMessagesDataset.isEmpty())
			return;
		mMessagesDataset.remove(mMessagesDataset.size()-1);
		mMessageAdapter.notifyItemRemoved(mMessagesDataset.size()-1);
		mMessagesRecyclerView.scrollToPosition(mMessagesDataset.size() - 1);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mThisUser = getDefaultSharedPreferences(mContext).getString(GCMConstants.USER_ID, null);
		if (getArguments() != null) // get the clicked user
			mOtherUser = getArguments().getParcelable(ContactDetailActivity.EXTRA_CONTACT);


		mMessagesDataset = new ArrayList<>();
		mMessageAdapter = new MessageAdapter(mContext, mMessagesDataset);

		mSocket.on(ON_NEW_MESSAGE, onNewMessage());
		mSocket.on(ON_TYPING, onTyping());


		mSocket.connect();

		mSocket.emit(ON_NEW_ROOM, Utils.toJSON(format("{from:%s,to:%s}", mThisUser, mOtherUser.getId())));
		GCMListenerService.setCallbacks(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mSocket.off(ON_NEW_MESSAGE, onNewMessage());
		mSocket.off(ON_TYPING, onTyping());
		mSocket.disconnect();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_contact_detail, container, false);
		mMessagesRecyclerView = (RecyclerView) rootView.findViewById(R.id.messagesRecyclerView);

		mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
		mMessagesRecyclerView.setLayoutManager(mLinearLayoutManager);
		mMessagesRecyclerView.addItemDecoration(new SpacesItemDecoration(15));
		mMessagesRecyclerView.setItemAnimator(new FadeInAnimator());

		mMessagesRecyclerView.setAdapter(mMessageAdapter);
		mMessagesRecyclerView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = ((AppCompatActivity) mContext).getCurrentFocus();
				if (view != null) {
					InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				return false;
			}
		});

		return rootView;
	}

	/**
	 * Send a message to the server using GCM
	 *
	 * @param message The message to send
	 */
	private void doGcmSendUpstreamMessage(Message message) {
		final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
		final String senderId = getString(R.string.gcm_defaultSenderId);
		final String msgId = UUID.randomUUID().toString();
		final String token = mOtherUser.getToken();
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
					Toast.makeText(mContext, "send message failed: " + result, Toast.LENGTH_LONG).show();
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
	public void onTyping(boolean typing) {
		mSocket.emit(ON_TYPING, Utils.toJSON(format("{from:%s,to:%s,typing:%s}", mThisUser, mOtherUser.getId(), typing)));
	}

	public Emitter.Listener onNewMessage() {
		if (onMessage != null)
			return onMessage;
		onMessage = new Emitter.Listener() {
			@Override
			public void call(final Object... args) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						JSONObject object = null;
						JSONObject data = null;
						try {
							object = new JSONObject(args[0].toString());
							data = object.getJSONObject("data");

							String body = data.getString("body");
							int type = object.getInt("type"); // MessageConstants.MessageType.ME
							String from = object.getString("from");

							Bundle bundle = new Bundle();
							bundle.putInt("type", type);
							bundle.putString("body", body);

							receiveMessage(from, bundle);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		};

		return onMessage;
	}

	public Emitter.Listener onTyping() {
		if (onTyping != null)
			return onTyping;
		onTyping = new Emitter.Listener() {
			@Override
			public void call(final Object... args) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						boolean typing = (boolean) args[0];
						if (typing) {
							if (!mMessagesDataset.isEmpty() && mMessagesDataset.get(mMessagesDataset.size()-1).getMessageType()==MessageConstants.MessageType.TYPING)
								return;
							Message message = new Message.Builder().viewType(MessageConstants.MessageType.TYPING).build();
							addMessage(message);
						}else if (!mMessagesDataset.isEmpty()) // !typing
							removeMessage();
					}
				});
			}
		};

		return onTyping;
	}

	public interface ContactDetailFragmentInteraction {
	}

}
