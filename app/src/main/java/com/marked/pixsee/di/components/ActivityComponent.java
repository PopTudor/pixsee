package com.marked.pixsee.di.components;

import com.marked.pixsee.di.modules.ActivityModule;
import com.marked.pixsee.di.scopes.PerActivity;
import com.marked.pixsee.login.LogInActivity;

import dagger.Component;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@Component(modules = {ActivityModule.class})
@PerActivity
public interface ActivityComponent {
	void inject(LogInActivity activity);

}
