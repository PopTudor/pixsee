package com.marked.pixsee.chat.di;

import com.marked.pixsee.chat.ChatContract;
import com.marked.pixsee.chat.ChatPresenter;
import com.marked.pixsee.chat.data.ChatRepository;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.injection.scopes.FragmentScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 2016-05-28.
 */
@Module
public class ChatModule {
	private ChatContract.View view;

	public ChatModule(ChatContract.View view) {
		this.view = view;
	}

	@Provides
	@FragmentScope
	public ChatContract.Presenter providePresenter(ChatRepository repository,@Named(DatabaseContract.AppsUser.TABLE_NAME) User user) {
		ChatPresenter chatPresenter = new ChatPresenter(view, repository, user);
		return chatPresenter;
	}
}
