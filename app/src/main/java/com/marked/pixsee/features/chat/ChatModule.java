package com.marked.pixsee.features.chat;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.di.scopes.FragmentScope;
import com.marked.pixsee.features.chat.data.ChatRepository;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.networking.UploadAPI;

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
	                                        @Named(DatabaseContract.AppsUser.TABLE_NAME) User user,
	                                        @Named(ServerConstants.SERVER) Retrofit retrofit,
	                                        ChattingInterface chattingInterface) {
		UploadAPI uploadAPI = retrofit.create(UploadAPI.class);
		ChatPresenter chatPresenter = new ChatPresenter(view, repository, user, uploadAPI, chattingInterface);
		chatPresenter.setThatUser(mThatUser);

		return chatPresenter;
	}

	@Provides
	@FragmentScope
	ChattingInterface provideChattingInterface(@Named(DatabaseContract.AppsUser.TABLE_NAME) User appsUser) {
		return new ChatClient(appsUser, mThatUser);
	}


}
