package com.marked.pixsee.main.di;

import com.marked.pixsee.commands.Command;
import com.marked.pixsee.main.MainContract;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-27.
 */
class MainPresenter implements MainContract.Presenter {
	private WeakReference<MainContract.View> mWeakView;

	public MainPresenter(MainContract.View view) {
		this.mWeakView = new WeakReference<>(view);
		this.mWeakView.get().setPresenter(this);
	}

	@Override
	public void chatClicked() {
		mWeakView.get().displayChat(true);
	}

	@Override
	public void start() {
		chatClicked();
	}

	@Override
	public void execute(Command command) {
		command.execute();
	}
}
