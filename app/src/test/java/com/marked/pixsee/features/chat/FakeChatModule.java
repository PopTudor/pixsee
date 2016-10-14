package com.marked.pixsee.features.chat;

import com.marked.pixsee.UserUtilTest;
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
 * Created by Tudor on 13-Oct-16.
 */

@Module
public class FakeChatModule {
	private ChatContract.View view;

	public FakeChatModule(ChatContract.View view) {
		this.view = view;
	}

	@Provides
	@FragmentScope
	public ChatContract.Presenter providePresenter(ChatRepository repository, @Named(DatabaseContract.AppsUser.TABLE_NAME) User user,
	                                               @Named(ServerConstants.SERVER) Retrofit retrofit) {
		UploadAPI uploadAPI = retrofit.create(UploadAPI.class);
		ChatPresenter chatPresenter = new ChatPresenter(view, repository, user, uploadAPI, new ChatClient(user, UserUtilTest.getUserTest()));
		return chatPresenter;
	}
}
