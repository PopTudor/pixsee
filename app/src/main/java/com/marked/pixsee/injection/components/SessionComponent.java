package com.marked.pixsee.injection.components;

import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.injection.modules.UserModule;
import com.marked.pixsee.injection.scopes.Local;
import com.marked.pixsee.injection.scopes.Remote;
import com.marked.pixsee.injection.scopes.Repository;
import com.marked.pixsee.injection.scopes.Session;
import com.marked.pixsee.ui.chat.data.ChatRepository;

import dagger.Component;

/**
 * Created by Tudor on 21-Oct-16.
 */
@Component(modules = {UserModule.class}, dependencies = AppComponent.class)
@Session
public interface SessionComponent extends AppComponent {
	@Local
	UserDatasource provideLocalDatasource();

	@Remote
	UserDatasource provideRemoteDatasource();

	@Repository
	UserDatasource provideUserRepository();

	UserManager provideUserManager();

	ChatRepository provideChatRepository();
}
