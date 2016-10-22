package com.marked.pixsee.ui.chat;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.ui.chat.data.ChatRepository;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.networking.UploadAPI;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Tudor on 13-Oct-16.
 */

@Module
public class ChatModule {
	private ChatContract.View view;
	private User mUser;

	public ChatModule(ChatContract.View view,User user) {
		this.view = view;
		mUser = user;
	}

	@Provides
	@FragmentScope
	public ChatContract.Presenter providePresenter(ChatRepository repository, User user,
	                                               @Named(ServerConstants.SERVER) Retrofit retrofit) {
		UploadAPI uploadAPI = retrofit.create(UploadAPI.class);
		ChatPresenter chatPresenter = new ChatPresenter(view, repository, user, uploadAPI, new ChatClient(user, mUser));
		chatPresenter.setThatUser(mUser);
		return chatPresenter;
	}
}
