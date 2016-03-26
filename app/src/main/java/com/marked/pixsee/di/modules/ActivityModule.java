package com.marked.pixsee.di.modules;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.di.scopes.PerActivity;
import com.marked.pixsee.service.RegistrationListener;
import com.marked.pixsee.signup.DialogRegistration;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@Module
@PerActivity
public class ActivityModule {
	private AppCompatActivity activity;

	public ActivityModule(AppCompatActivity activity) {
		this.activity = activity;
	}

	@Provides
	@PerActivity
	ProgressDialog provideProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setTitle("Login");
		dialog.setMessage("Please wait...");
		dialog.setIndeterminate(true);
		return dialog;
	}

	@Provides
	@PerActivity
	RegistrationListener provideDialogRegistration(ProgressDialog dialog) {
		return new DialogRegistration(activity, dialog);
	}

	@Provides
	@PerActivity
	LocalBroadcastManager provideLocalBroadcastManager() {
		return LocalBroadcastManager.getInstance(activity);
	}

	@Provides
	@PerActivity
	AppCompatActivity provideActivity() {
		return activity;
	}

	@Provides
	@PerActivity
	Context provideContext() {
		return activity;
	}
}
