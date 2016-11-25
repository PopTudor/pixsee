package com.marked.pixsee.ui.chat;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.marked.pixsee.RxBus;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.networking.UploadAPI;
import com.marked.pixsee.ui.chat.data.ChatDatasource;
import com.marked.pixsee.ui.chat.data.ChatRepository;
import com.marked.pixsee.ui.chat.data.MessageConstants;
import com.marked.pixsee.ui.commands.Command;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 2016-05-19.
 */
class ChatPresenter implements ChatContract.Presenter {
	private final UploadAPI mUploadAPI;
	private final ChattingInterface mChatClient;
	private WeakReference<ChatContract.View> mView;
	private ChatDatasource mRepository;
	private User mAppsUser;
	private boolean mShowTypingAnimation = true;
	private ChatFragment.ChatFragmentInteraction mChatFragmentInteraction;
	private File mPictureFile;
	private User mThatUser;
	private Gson mGson = new Gson();

	ChatPresenter(ChatContract.View mView, ChatRepository mRepository, User appsUser, UploadAPI uploadAPI, ChattingInterface chatClient) {
		this.mRepository = mRepository;
		mUploadAPI = uploadAPI;
		this.mView = new WeakReference<>(mView);
		mChatClient = chatClient;
		this.mView.get().setPresenter(this);
		mAppsUser = appsUser;
	}


	@Override
	public void attach() {
		loadMore(50, true);
		connect();

		RxBus.getInstance().register(Boolean.class, new Action1<Boolean>() {
			@Override
			public void call(Boolean aBoolean) {
				isTyping(aBoolean);
			}
		});
		RxBus.getInstance().register(Message.class, new Action1<Message>() {
			@Override
			public void call(Message message) {
				receiveMessage(message);
			}
		});
	}

	private void connect() {
		mChatClient.connect();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("user", mGson.toJson(mAppsUser, User.class));
		jsonObject.addProperty("from", mAppsUser.getId());
		jsonObject.addProperty("to", mThatUser.getId());
		jsonObject.addProperty("to_token", mThatUser.getPushToken());
		mChatClient.emit(ChatClient.ON_NEW_ROOM, jsonObject);
	}

	@Override
	public void detach() {
		mChatClient.disconnect();
	}

	@Override
	public void receiveMessage(@NonNull Message message) {
		mRepository.saveMessage(message);
		mView.get().addMessage(message);
	}

	public void onTyping(boolean typing) {
		try {
			mChatClient.emit(ChatClient.ON_TYPING,
					new JSONObject(String.format("{from:%s,to:%s,typing:%s,to_token:\'%s\'}",
							mAppsUser.getId(),
							mThatUser.getId(),
							typing,
							mThatUser.getPushToken())));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setThatUser(User thatUser) {
		mThatUser = thatUser;
	}

	@Override
	public void sendImage() {
		final Message message = new Message.Builder()
				                        .messageType(MessageConstants.MessageType.ME_IMAGE)
				                        .addData(MessageConstants.DATA_BODY, mPictureFile.getAbsolutePath())
				                        .from(mAppsUser.getId())
				                        .to(mThatUser.getId())
				                        .build();

		if (message.getMessageType() == MessageConstants.MessageType.ME_IMAGE) {
			File file = new File(message.getData().get(MessageConstants.DATA_BODY));
			// create RequestBody instance from file
			RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
			// MultipartBody.Part is used to send also the actual file name
			MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);
			mUploadAPI.upload(mAppsUser.getId(), mThatUser.getId(), body)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.doOnSubscribe(new Action0() {
						@Override
						public void call() {
							// add a loading animation when sending the image
						}
					})
					.subscribe(new Action1<Response<JsonObject>>() {
						@Override
						public void call(Response<JsonObject> responseBody) {
							if (responseBody.isSuccessful()) {
								sendMessage(message);
								Message message1 = new Message.Builder()
										                   .messageType(MessageConstants.MessageType.YOU_IMAGE)
										                   .date(message.getDate())
										                   .from(message.getFrom())
										                   .to(message.getTo())
										                   .addData(MessageConstants.DATA_BODY, responseBody.body().get("pictureName").getAsString())
										                   .build();
								mChatClient.emit(ChatClient.ON_NEW_MESSAGE, mGson.toJson(message1));
							}
						}
					}, new Action1<Throwable>() {
						@Override
						public void call(Throwable throwable) {
							throwable.printStackTrace();
						}
					});
		}
	}

	private void sendMessage(@NonNull Message message) {
		mChatClient.emit(ChatClient.ON_NEW_MESSAGE, message);
		mRepository.saveMessage(message);
		mView.get().addMessage(message);
	}

	@Override
	public void sendMessage(@NonNull String text) {
		Message message = new Message.Builder()
				                  .addData(MessageConstants.DATA_BODY, text)
				                  .from(mAppsUser.getId())
				                  .to(mThatUser.getId())
				                  .build();
		sendMessage(message);
	}

	@Override
	public File getPictureFile() {
		return mPictureFile;
	}

	@Override
	public void chatImageClicked(Uri uri) {
		mView.get().showImageFullscreen(uri);
	}

	@Override
	public void isTyping(boolean typing) {
		if (typing && mShowTypingAnimation) {
			mShowTypingAnimation = false;
			Message typingMessage = new Message.Builder()
					                        .messageType(MessageConstants.MessageType.TYPING)
					                        .build();
			mView.get().addMessage(typingMessage);
		}
		if (!typing) {
			mView.get().pop();
			mShowTypingAnimation = true;
		}
	}

	@Override
	public void chatClicked(Message message, int position) {
		mRepository.deleteMessages(message);
		mView.get().remove(message, position);
	}

	public void setChatInteraction(ChatFragment.ChatFragmentInteraction listener) {
		mChatFragmentInteraction = listener;
	}

	@Override
	public void pictureTaken(File file) {
		mPictureFile = file;
		mView.get().showImage(file);
	}

	@Override
	public void onCameraClick() {
		mChatFragmentInteraction.onCameraClick();
	}

	@Override
	public void clearImageButton() {
		mView.get().showPictureContainer(false);
	}

	@Override
	public void loadMore(int limit, boolean forceUpdate) {
		mRepository.getMessages(mThatUser)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<Message>>() {
					@Override
					public void call(List<Message> messages) {
						mView.get().showMessages(messages);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						mView.get().showEmptyMessages();
					}
				});
	}

	@Override
	public void execute(Command command) {
		command.execute();
	}
}
