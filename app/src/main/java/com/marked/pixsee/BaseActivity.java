package com.marked.pixsee;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Tudor on 24-Jul-16.
 */
public abstract class BaseActivity extends AppCompatActivity {
	/**
	 * Inherit BaseActivity in production code and then in tests create a private class that inherits the production activity
	 * and override this method to be able to switch the component and it's modules
	 */
	protected abstract void injectComponent();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		injectComponent();
	}
}
