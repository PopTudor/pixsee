package com.marked.pixsee.ui.chat;

import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.networking.UploadAPI;
import com.marked.pixsee.ui.chat.data.ChatRepository;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Tudor on 2016-05-28.
 */
@Module
class ChatModule {
	private ChatContract.View view;
	private User mThatUser;

	ChatModule(ChatContract.View view, User thatUser) {
		this.view = view;
		mThatUser = thatUser;
	}

	@Provides
	@FragmentScope
	ChatContract.Presenter providePresenter(ChatRepository repository,
	                                        UserManager user,
	                                        @Named(ServerConstants.SERVER) Retrofit retrofit,
	                                        ChattingInterface chattingInterface) {
		UploadAPI uploadAPI = retrofit.create(UploadAPI.class);
		ChatPresenter chatPresenter = new ChatPresenter(view, repository, user.getAppUser(), uploadAPI, chattingInterface);
		chatPresenter.setThatUser(mThatUser);

		return chatPresenter;
	}

	@Provides
	@FragmentScope
	ChattingInterface provideChattingInterface(UserManager appsUser) {
		return new ChatClient(appsUser.getAppUser(), mThatUser);
	}


}
