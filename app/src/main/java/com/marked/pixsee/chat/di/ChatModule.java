package com.marked.pixsee.chat.di;

import android.content.Context;
import android.preference.PreferenceManager;

import com.marked.pixsee.chat.ChatContract;
import com.marked.pixsee.chat.ChatPresenter;
import com.marked.pixsee.chat.data.ChatDatasource;
import com.marked.pixsee.chat.data.ChatDiskDatasource;
import com.marked.pixsee.chat.data.ChatNetworkDatasource;
import com.marked.pixsee.chat.data.ChatRepository;
import com.marked.pixsee.data.database.PixyDatabase;

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
	public ChatContract.Presenter providePresenter(Context context) {
		ChatDatasource diskDatasource = new ChatDiskDatasource(PixyDatabase.getInstance(context));
		ChatDatasource networkDatasource = new ChatNetworkDatasource(PreferenceManager.getDefaultSharedPreferences(context));
		ChatDatasource repository = new ChatRepository(diskDatasource, networkDatasource);
		return new ChatPresenter(view, repository);
	}
}
