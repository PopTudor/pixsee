package com.marked.pixsee.chat;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.marked.pixsee.chat.data.ChatDatasource;
import com.marked.pixsee.chat.data.ChatRepository;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.model.user.User;
import com.marked.pixsee.networking.UploadAPI;

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
	private WeakReference<ChatContract.View> mView;
	private ChatDatasource mRepository;
	private User mAppsUser;
	private boolean mShowTypingAnimation = true;
	private ChatFragment.ChatFragmentInteraction mChatFragmentInteraction;
	private File mPictureFile;
	private User mThatUser;

	public ChatPresenter(ChatContract.View mView, ChatRepository mRepository, User appsUser, UploadAPI uploadAPI) {
		this.mRepository = mRepository;
		mUploadAPI = uploadAPI;
		this.mView = new WeakReference<>(mView);
		this.mView.get().setPresenter(this);
		mAppsUser = appsUser;
	}


	@Override
	public void attach() {
		loadMore(50, true);
	}

	@Override
	public void loadMore(int limit, User friendId) {
		mRepository.getMessages(friendId)
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
	public void receiveMessage(@NonNull Message message) {
		mRepository.saveMessage(message);
		mView.get().addMessage(message);
	}

	@Override
	public void setThatUser(User thatUser) {
		mThatUser = thatUser;
	}

	@Override
	public void sendImage(@NonNull final Message message) {
		if (message.getMessageType() == MessageConstants.MessageType.ME_IMAGE) {
			File file = new File(message.getData().get(MessageConstants.DATA_BODY));
			// create RequestBody instance from file
			RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-com.marked.pixsee.data"), file);
			// MultipartBody.Part is used to send also the actual file name
			MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);
			mUploadAPI.upload(mAppsUser.getUserID(),mThatUser.getUserID(),body)
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
//								String prepareImage = ServerConstants.SERVER_USER_IMAGE.replace(ServerConstants.SCHEME_HTTP, "")
//										+ "/" + responseBody.body().get("pictureName").getAsString();
								Message message1 = new Message.Builder()
										.messageType(MessageConstants.MessageType.YOU_IMAGE)
										.date(message.getDate())
										.from(message.getFrom())
										.to(message.getTo())
										.addData(MessageConstants.DATA_BODY, responseBody.body().get("pictureName").getAsString())
										.build();
								mView.get().imageSent(message1);
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

	@Override
	public void sendMessage(@NonNull Message message) {
		mRepository.saveMessage(message);
		mView.get().addMessage(message);
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

	}

	@Override
	public void execute(Command command) {
		command.execute();
	}
}
