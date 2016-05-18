package com.marked.pixsee.frienddetail;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marked.pixsee.R;
import com.marked.pixsee.commons.SpaceItemDecorator;
import com.marked.pixsee.data.User;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.message.MessageConstants;
import com.marked.pixsee.data.message.MessageDataset;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.service.GCMListenerService;
import com.marked.pixsee.utility.GCMConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * A fragment representing a single Contact detail screen.
 * This fragment is either contained in a [ContactListActivity]
 * in two-pane mode (on tablets) or a [ChatDetailActivity]
 * on handsets.
 */
public class FriendDetailFragment extends Fragment implements GCMListenerService.Callbacks {
	private Context mContext;

	private String mThisUser;
	private User mThatUser;

	private MessageDataset mMessagesInstance;
	private MessageAdapter mMessageAdapter;

	private LinearLayoutManager mLinearLayoutManager;

	private Socket mSocket;
	private FriendDetailFragment.ContactDetailFragmentInteraction mCallback;

	private Emitter.Listener onMessage;
	private Emitter.Listener onTyping;


	public void sendMessage(String messageText, int messageType) {
		Message message = new Message.Builder()
				                  .addData(MessageConstants.DATA_BODY, messageText)
				                  .messageType(messageType)
				                  .from(mThisUser)
				                  .to(mThatUser.getUserID())
				                  .build();
		//		doGcmSendUpstreamMessage(message);
		JSONObject jsonObject = message.toJSON();

		mSocket.emit(FriendDetailFragment.ON_NEW_MESSAGE, jsonObject);
		message.setMessageType(reverseMessageType(messageType)); /* after the message is sent with message type 1 (to appear on the left for the other user)
		 we want to
	    appear on the right for this user*/
		addMessage(message);

	}

	public int reverseMessageType(int aInt) {
		switch (aInt) {
			case MessageConstants.MessageType.YOU_MESSAGE:
				return MessageConstants.MessageType.ME_MESSAGE;
			case MessageConstants.MessageType.YOU_IMAGE:
				return MessageConstants.MessageType.ME_IMAGE;
			case MessageConstants.MessageType.ME_MESSAGE:
				return MessageConstants.MessageType.YOU_MESSAGE;
			case MessageConstants.MessageType.ME_IMAGE:
				return MessageConstants.MessageType.YOU_IMAGE;
			default:
				return MessageConstants.MessageType.TYPING;
		}
	}

	/**
	 * This is used to receive messages from socket.io
	 *
	 * @param from
	 * @param data
	 */
	@Override
	public void receiveMessage(String from, Bundle data) {
		int messageType = data.getInt("type", MessageConstants.MessageType.YOU_MESSAGE);
		Message message = new Message.Builder().addData(data).messageType(messageType).from(mThatUser.getUserID()).to(mThatUser.getUserID()).build();
		addMessage(message);
	}

	/**
	 * Add message to dataset and notify the adapter of the change
	 *
	 * @param message the message to add
	 */
	private void addMessage(Message message) {
		mMessagesInstance.add(message);
		mMessageAdapter.notifyItemInserted(mMessagesInstance.size() - 1);
		messagesRecyclerView.scrollToPosition(mMessagesInstance.size() - 1);
	}

	/**
	 * Remove message from dataset and notify the adapter
	 */
	private void removeMessage() {
		if (mMessagesInstance.isEmpty())
			return;
		mMessagesInstance.remove(mMessagesInstance.size() - 1);
		mMessageAdapter.notifyItemRemoved(mMessagesInstance.size());
		messagesRecyclerView.scrollToPosition(mMessagesInstance.size());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GCMListenerService.setCallbacks(this);
		mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
		try {
			mSocket = IO.socket(ServerConstants.SERVER);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		mMessagesInstance = MessageDataset.getInstance(mContext);
		mMessageAdapter = new MessageAdapter(mMessagesInstance);
		onMessage = onNewMessage();
		onTyping = onTyping();

		mSocket.on(FriendDetailFragment.ON_NEW_MESSAGE, onMessage);
		mSocket.on(FriendDetailFragment.ON_TYPING, onTyping);

		mSocket.connect();

		try {
			mSocket.emit(FriendDetailFragment.ON_NEW_ROOM, new JSONObject("{from:%s,to:%s}".format(mThisUser, mThatUser.getUserID())));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mSocket.off(FriendDetailFragment.ON_NEW_MESSAGE, onMessage);
		mSocket.off(FriendDetailFragment.ON_TYPING, onTyping);
		mSocket.disconnect();
	}

	private RecyclerView messagesRecyclerView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_contact_detail, container, false);
		mThatUser = getArguments().getParcelable(FriendDetailActivity.EXTRA_CONTACT);
		messagesRecyclerView = (RecyclerView) rootView.findViewById(R.id.messagesRecyclerView);
		messagesRecyclerView.setLayoutManager(mLinearLayoutManager);
		messagesRecyclerView.addItemDecoration(new SpaceItemDecorator(15));
		messagesRecyclerView.setAdapter(mMessageAdapter);
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
		String token = mThatUser.getToken();
		final Bundle data = message.toBundle();
		data.putString(GCMConstants.TOKEN, token);

		if (msgId.isEmpty())
			return;
		rx.Observable.create(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				try {
					gcm.send(senderId + GCMConstants.SERVER_UPSTREAM_ADRESS, msgId, data);
					subscriber.onCompleted();
				} catch (IOException e) {
					subscriber.onError(e);
				}
			}
		})
				.subscribeOn(Schedulers.computation())
				.subscribe(new Subscriber<String>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						Toast.makeText(mContext, "Send message failed", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onNext(String s) {
					}
				});
	}

	@Override
	public void onPause() {
		super.onPause();
		FriendDetailFragment.isInForeground = false;
		mMessagesInstance.clear();
	}

	@Override
	public void onResume() {
		super.onResume();
		FriendDetailFragment.isInForeground = true;
		mMessagesInstance.loadMore(mThatUser, 50);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mCallback = (ContactDetailFragmentInteraction) context;
			mContext = getActivity();

			mThisUser = PreferenceManager.getDefaultSharedPreferences(mContext).getString(GCMConstants.USER_ID, null);
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + " must implement " +
					                             ContactDetailFragmentInteraction.class);
		}

	}

	public void onTyping(boolean typing) {
		try {
			mSocket.emit(FriendDetailFragment.ON_TYPING, new JSONObject("{from:%s,to:%s,typing:%s}".format(mThisUser, mThatUser.getUserID(), typing)));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private Emitter.Listener onNewMessage() {
		return new Emitter.Listener() {
			@Override
			public void call(final Object... args) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject json = new JSONObject(args[0].toString());
							JSONObject data = json.getJSONObject(MessageConstants.DATA_PAYLOAD);
							String body = data.getString(MessageConstants.DATA_BODY);
							Integer type = json.getInt(MessageConstants.MESSAGE_TYPE); // MessageConstants.MessageType.ME_MESSAGE
							String from = json.getString(MessageConstants.FROM);
							Bundle bundle = new Bundle();
							bundle.putInt(MessageConstants.MESSAGE_TYPE, type);
							bundle.putString(MessageConstants.DATA_BODY, body);
							receiveMessage(from, bundle);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		};
	}

	public Emitter.Listener onTyping() {
		Emitter.Listener onTyping = new Emitter.Listener() {
			@Override
			public void call(final Object... args) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						boolean typing = (boolean) args[0];
						if (typing)
							if (!mMessagesInstance.isEmpty() && mMessagesInstance.get(mMessagesInstance.size() - 1).getMessageType()
									                                    == MessageConstants.MessageType.TYPING) {

								addMessage(new Message.Builder().messageType(MessageConstants.MessageType.TYPING).build());
							} else if (!mMessagesInstance.isEmpty())
								// !typing
								removeMessage();
					}
				});

			}
		};

		return onTyping;
	}

	interface ContactDetailFragmentInteraction {
	}

	public static final String ON_NEW_MESSAGE = "onMessage";
	public static final String ON_NEW_ROOM = "onRoom";
	public static final String ON_TYPING = "onTyping";
	/**
	 * keep track if the user is interacting with the app. If not, disconnect the socket
	 */
	/**
	 * Check if the user is using the app
	 *
	 * @return if the app is in foreground or not
	 */
	public static boolean isInForeground = false;

	public static FriendDetailFragment newInstance(Parcelable parcelable) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(FriendDetailActivity.EXTRA_CONTACT, parcelable);
		FriendDetailFragment fragment = new FriendDetailFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

}
