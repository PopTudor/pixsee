package com.marked.pixsee.face;

import com.marked.pixsee.commands.Command;

/**
 * Created by Tudor on 2016-05-10.
 */
public class FacePresenter implements FaceContract.Presenter {
	private FaceContract.View view;

	public FacePresenter(FaceContract.View view) {
		this.view = view;
		this.view.setPresenter(this);
	}

	@Override
	public void execute(Command command) {
		command.execute();
	}

	@Override
	public void start() {

	}

	@Override
	public void onShutter() {
		view.showTakenPictureActions(); /* when the user touches the selfie button */
	}
}
