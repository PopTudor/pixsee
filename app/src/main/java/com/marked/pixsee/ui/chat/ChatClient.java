package com.marked.pixsee.ui.chat;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.marked.pixsee.RxBus;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.ui.chat.data.MessageConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Tudor on 13-Oct-16.
 */

class ChatClient implements ChattingInterface {
	static final String ON_NEW_MESSAGE = "onMessage";
	static final String ON_NEW_ROOM = "onRoom";
	static final String ON_TYPING = "onTyping";
	static final String ON_CONNECT = "onConnect";
	static final String ON_DISCONNECT = "onDisconnect";
	private final User mAppUser;
	private final User mThatUser;
	private Socket mSocket;
	private Emitter.Listener onMessage;
	private Emitter.Listener onTyping;
	private Emitter.Listener onConnect;
	private Gson mGson = new Gson();

	ChatClient(User appUser, User thatUser) {
		mAppUser = appUser;
		mThatUser = thatUser;
		final String SERVER_CHAT = ServerConstants.BASE_URL + ServerConstants.CHAT_PORT;
		try {
			mSocket = IO.socket(SERVER_CHAT);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		onMessage = onNewMessage();
		onTyping = onTyping();
		onConnect = onConnect();
	}

	private Emitter.Listener onConnect() {
		return new Emitter.Listener() {
			@Override
			public void call(Object... args) {

			}
		};
	}

	@Override
	public void connect() {
		mSocket.connect();
		mSocket.on(ON_NEW_MESSAGE, onMessage);
		mSocket.on(ON_TYPING, onTyping);
		mSocket.on(ON_CONNECT, onConnect);
	}

	@Override
	public void disconnect() {
		mSocket.emit(ON_DISCONNECT, mAppUser.getId());
		mSocket.off(ON_NEW_MESSAGE, onMessage);
		mSocket.off(ON_TYPING, onTyping);
		mSocket.disconnect();
	}

	@Override
	public void emit(String event, Object... objects) {
		try {
			for (Object object : objects)
				mSocket.emit(event, new JSONObject(mGson.toJson(object)));
			// JSONObject is needed because socketio uses it to parse and compose json strings
			// https://github.com/socketio/socket.io-client-java#usage
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
							Message message = mGson.fromJson(json.toString(), Message.class);
							Message.Builder builder = new Message.Builder()
									                          .date(message.getDate())
									                          .id(message.getId())
									                          .from(message.getFrom())
									                          // the database doesn't have from but in this case from & to are the same
									                          // from & to are switching here
									                          // I get message from you and now to send message back to you
									                          // to becomes from
									                          .to(message.getFrom())
									                          .messageType(message.getMessageType())
									                          .addData(message.getData());

							if (message.getMessageType() == MessageConstants.MessageType.YOU_IMAGE) {
								builder.addData(MessageConstants.DATA_BODY,
												ServerConstants.SERVER_USER_IMAGE +
														"/?img=" + message.getData().get(MessageConstants.DATA_BODY));
							}
							message = builder.build();

							if (message.getMessageType() == MessageConstants.MessageType.ME_MESSAGE)
								message.setMessageType(MessageConstants.MessageType.YOU_MESSAGE);
							RxBus.getInstance().post(message);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		};
	}

	/**
	 * keep track if the user is interacting with the app. If not, disconnect the socket
	 */

	public Emitter.Listener onTyping() { // // FIXME: 23-Jun-16 gets called twice when I click a friend
		return new Emitter.Listener() {
			@Override
			public void call(final Object... args) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						boolean typing = (boolean) args[0];
						RxBus.getInstance().post(typing);
					}
				});

			}
		};
	}

}
