package com.marked.pixsee.chat;

import com.marked.pixsee.chat.data.ChatRepository;
import com.marked.pixsee.di.scopes.FragmentScope;
import com.marked.pixsee.model.database.DatabaseContract;
import com.marked.pixsee.model.user.User;
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

	public ChatModule(ChatContract.View view) {
		this.view = view;
	}

	@Provides
	@FragmentScope
	public ChatContract.Presenter providePresenter(ChatRepository repository, @Named(DatabaseContract.AppsUser.TABLE_NAME) User user,
	                                               @Named(ServerConstants.SERVER) Retrofit retrofit) {
		UploadAPI uploadAPI = retrofit.create(UploadAPI.class);
		ChatPresenter chatPresenter = new ChatPresenter(view, repository, user,uploadAPI);
		return chatPresenter;
	}
}
