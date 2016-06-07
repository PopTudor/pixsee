package com.marked.pixsee.face;

import com.marked.pixsee.commands.Command;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-10.
 */
public class FacePresenter implements FaceContract.Presenter {
	private WeakReference<FaceContract.View> mView;

	public FacePresenter(FaceContract.View view) {
		this.mView = new WeakReference<FaceContract.View>(view);
		this.mView.get().setPresenter(this);
	}

	@Override
	public void execute(Command command) {
		command.execute();
	}

	@Override
	public void displayActions(boolean showSelfieActions) {
		mView.get().displayActions(showSelfieActions);
	}

	@Override
	public void start() {

	}

	@Override
	public void onShutter() {
		mView.get().showTakenPictureActions(); /* when the user touches the selfie button */
	}
}
