package com.marked.pixsee.injection.modules;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.injection.scopes.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@Module
@ActivityScope
public class ActivityModule {
	private AppCompatActivity activity;

	public ActivityModule(AppCompatActivity activity) {
		this.activity = activity;
	}

	@Provides
	@ActivityScope
	ProgressDialog provideProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setTitle("Login");
		dialog.setMessage("Please wait...");
		dialog.setIndeterminate(true);
		return dialog;
	}

	@Provides
	@ActivityScope
	LocalBroadcastManager provideLocalBroadcastManager() {
		return LocalBroadcastManager.getInstance(activity);
	}

	@Provides
	@ActivityScope
	AppCompatActivity provideActivity() {
		return activity;
	}

	@Provides
	@ActivityScope
	Context provideContext() {
		return activity;
	}

}
