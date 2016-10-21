package com.marked.pixsee.injection.components;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.ActivityScope;

import dagger.Component;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@Component(modules = {ActivityModule.class}, dependencies = SessionComponent.class)
@ActivityScope
public interface ActivityComponent extends SessionComponent {
	AppCompatActivity provideAppCompatActivity();

	Context provideContext();
}