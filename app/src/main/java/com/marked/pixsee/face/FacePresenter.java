package com.marked.pixsee.face;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.commands.Command;

/**
 * Created by Tudor on 2016-05-10.
 */
public class FacePresenter implements BasePresenter {

	void execute(Command command) {
		command.execute();
	}

	@Override
	public void start() {

	}
}
