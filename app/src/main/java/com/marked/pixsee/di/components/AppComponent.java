package com.marked.pixsee.di.components;

import android.app.Application;

import com.marked.pixsee.di.modules.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Tudor Pop on 17-Mar-16.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
	Application application();
}
