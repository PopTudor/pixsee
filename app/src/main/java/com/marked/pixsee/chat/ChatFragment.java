package com.marked.pixsee.chat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.marked.pixsee.R;
import com.marked.pixsee.chat.custom.ChatAdapter;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.chat.di.ChatModule;
import com.marked.pixsee.chat.di.DaggerChatComponent;
import com.marked.pixsee.commons.SpaceItemDecorator;
import com.marked.pixsee.friends.data.User;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.service.GCMListenerService;
import com.marked.pixsee.utility.GCMConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import tyrantgit.explosionfield.ExplosionField;

/**
 * A fragment representing a single Contact detail screen.
 * This fragment is either contained in a [ContactListActivity]
 * in two-pane mode (on tablets) or a [ChatDetailActivity]
 * on handsets.
 */
public class ChatFragment extends Fragment implements ChatContract.View, ChatAdapter.ChatInteraction {
	private String mThisUser;
	private User mThatUser;

	private ChatAdapter mChatAdapter;
	private LinearLayoutManager mLinearLayoutManager;

	private ExplosionField mExplosionField;
	private Socket mSocket;

    private Emitter.Listener onMessage;
    private Emitter.Listener onTyping;
    @Inject
    ChatContract.Presenter mPresenter;


	public void sendMessage(String messageText, int messageType) {
		Message message = new Message.Builder()
				                  .addData(MessageConstants.DATA_BODY, messageText)
				                  .messageType(messageType)
				                  .from(mThisUser)
				                  .to(mThatUser.getUserID())
				                  .build();
		//		doGcmSendUpstreamMessage(message);
		JSONObject jsonObject = message.toJSON();

		mSocket.emit(ChatFragment.ON_NEW_MESSAGE, jsonObject);
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
	 * Add message to dataset and notify the adapter of the change
	 *
	 * @param message the message to add
	 */
	@Override
	public void addMessage(Message message) {
		mChatAdapter.getDataset().add(message);
		mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount()-1);
		messagesRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GCMListenerService.setCallbacks(mPresenter);
		mChatAdapter = new ChatAdapter(this);
		mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
		ActivityComponent activityComponent = DaggerActivityComponent.builder().activityModule(new ActivityModule((AppCompatActivity) getActivity()))
				                                      .build();
		DaggerChatComponent.builder().activityComponent(activityComponent).chatModule(new ChatModule(this)).build().inject(this);
		try {
			mSocket = IO.socket(ServerConstants.SERVER);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		onMessage = onNewMessage();
		onTyping = onTyping();

		mSocket.on(ChatFragment.ON_NEW_MESSAGE, onMessage);
		mSocket.on(ChatFragment.ON_TYPING, onTyping);

		mSocket.connect();

		try {
			mSocket.emit(ChatFragment.ON_NEW_ROOM, new JSONObject(String.format("{from:%s,to:%s}",mThisUser, mThatUser.getUserID())));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mSocket.off(ChatFragment.ON_NEW_MESSAGE, onMessage);
		mSocket.off(ChatFragment.ON_TYPING, onTyping);
		mSocket.disconnect();
	}

	private RecyclerView messagesRecyclerView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
		messagesRecyclerView = (RecyclerView) rootView.findViewById(R.id.messagesRecyclerView);
		messagesRecyclerView.setLayoutManager(mLinearLayoutManager);
		messagesRecyclerView.addItemDecoration(new SpaceItemDecorator(15));
		messagesRecyclerView.setAdapter(mChatAdapter);
		return rootView;
	}

	/**
	 * Send a message to the server using GCM
	 *
	 * @param message The message to send
	 */
	private void doGcmSendUpstreamMessage(Message message) {
		final FirebaseMessaging gcm = FirebaseMessaging.getInstance();
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
//				try {
//					RemoteMessage remoteMessage = new RemoteMessage();
//					gcm.send(senderId + GCMConstants.SERVER_UPSTREAM_ADRESS, msgId, data);
					subscriber.onCompleted();
//				} catch (IOException e) {
//					subscriber.onError(e);
//				}
			}
		})
				.subscribeOn(Schedulers.computation())
				.subscribe(new Subscriber<String>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						Toast.makeText(getActivity(), "Send message failed", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onNext(String s) {
					}
				});
	}

	@Override
	public void onStart() {
		super.onStart();
		mPresenter.start();
	}

	@Override
	public void onPause() {
		super.onPause();
		isInForeground = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		isInForeground = true;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mThatUser = getArguments().getParcelable(ChatActivity.EXTRA_CONTACT);
		mThisUser = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(GCMConstants.USER_ID, null);
		mExplosionField = ExplosionField.attach2Window(getActivity());;

	}

	public void onTyping(boolean typing) {
		try {
			mSocket.emit(ChatFragment.ON_TYPING, new JSONObject(String.format("{from:%s,to:%s,typing:%s}",mThisUser, mThatUser.getUserID(),
					typing)));
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
                            mPresenter.receiveMessage(from, bundle);
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
                        mPresenter.isTyping(typing);
                    }
                });

            }
        };

        return onTyping;
    }

	@Override
	public void pop() {
		if (mChatAdapter.getDataset().size()<=0)
			return;
		mChatAdapter.getDataset().remove(mChatAdapter.getDataset().size() - 1);
		mChatAdapter.notifyItemRemoved(mChatAdapter.getDataset().size());
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

	public static ChatFragment newInstance(Parcelable parcelable) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(ChatActivity.EXTRA_CONTACT, parcelable);
		ChatFragment fragment = new ChatFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void showCards(List<Message> cards) {

	}

	@Override
	public void showNoChats() {

	}

	@Override
	public void setPresenter(ChatContract.Presenter presenter) {

	}

	@Override
	public void chatClicked(View view, Message message,int position) {
		mPresenter.chatClicked(message);
		mExplosionField.explode(view);
		mChatAdapter.getDataset().remove(message);
		mChatAdapter.notifyItemRemoved(position);
	}
}
