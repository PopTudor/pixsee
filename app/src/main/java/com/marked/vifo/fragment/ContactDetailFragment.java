package com.marked.vifo.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.marked.vifo.gcm.service.GCMListenerService;
import com.marked.vifo.helper.EmitterListeners;
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

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static java.lang.String.format;

/**
 * A fragment representing a single Contact detail screen.
 * This fragment is either contained in a {@link ContactListActivity}
 * in two-pane mode (on tablets) or a {@link ContactDetailActivity}
 * on handsets.
 */
public class ContactDetailFragment extends Fragment implements GCMListenerService.Callbacks {
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
	private EmitterListeners mEmitterListeners;

	{
		try {
			mSocket = IO.socket(GCMConstants.SERVER);
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

		mSocket.emit(EmitterListeners.ON_NEW_MESSAGE, jsonObject);
		addMessage(message);
	}

	@Override
	public void receiveMessage(String from, Bundle data) {
		Message message = new Message.Builder().addData(data).viewType(MessageConstants.MessageType.YOU).build();
		addMessage(message);
	}

	private void addMessage(Message message) {
		mMessagesDataset.add(message);
		mMessageAdapter.notifyItemInserted(mMessagesDataset.size());
		mMessagesRecyclerView.scrollToPosition(mMessagesDataset.size() - 1);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		if (getArguments() != null)
			mOtherUser = getArguments().getParcelable(ContactDetailActivity.EXTRA_CONTACT);
		mEmitterListeners = new EmitterListeners(mContext, this);
		mMessagesDataset = new ArrayList<>();
		mMessageAdapter = new MessageAdapter(mContext, mMessagesDataset);

		mThisUser = getDefaultSharedPreferences(mContext).getString(GCMConstants.USER_ID, null);
		mSocket.on(EmitterListeners.ON_NEW_MESSAGE, mEmitterListeners.onNewMessage());


		mSocket.connect();

		mSocket.emit("room", Utils.toJSON(format("{from:%s,to:%s}", mThisUser, mOtherUser.getId())));
		GCMListenerService.setCallbacks(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_contact_detail, container, false);
		mMessagesRecyclerView = (RecyclerView) rootView.findViewById(R.id.messagesRecyclerView);

		mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
		mMessagesRecyclerView.setLayoutManager(mLinearLayoutManager);
		mMessagesRecyclerView.addItemDecoration(new SpacesItemDecoration(15));

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
	public void onDestroy() {
		super.onDestroy();
		mSocket.disconnect();
		mSocket.off(EmitterListeners.ON_NEW_MESSAGE, mEmitterListeners.onNewMessage());
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
