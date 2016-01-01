package com.marked.vifo.helper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.marked.vifo.gcm.service.GCMListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter.Listener;

/**
 * Created by Tudor Pop on 01-Jan-16.
 */
public class EmitterListeners {
	public static final String ON_NEW_MESSAGE = "newMessage";
	public static final String ON_NEW_ROOM = "newRoom";

	private Context mContext;
	private GCMListenerService.Callbacks mCallbacks;
	private Listener onNewMessage;

	public EmitterListeners(Context context, GCMListenerService.Callbacks callbacks) {
		mContext = context;
		mCallbacks = callbacks;
		onNewMessage = onNewMessage();
	}

	public Listener onNewMessage() {
		if (onNewMessage!=null)
			return onNewMessage;
		onNewMessage= new Listener() {
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
							int type = object.getInt("type");
							String from = object.getString("from");

							Bundle bundle = new Bundle();
							bundle.putInt("type", type);
							bundle.putString("body", body);

							mCallbacks.receiveMessage(from,bundle);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		};

		return onNewMessage;
	}

}
