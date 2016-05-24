package com.marked.pixsee.face.commands;

import android.content.Context;

import com.marked.pixsee.R;
import com.marked.pixsee.commands.ClickCommand;
import com.marked.pixsee.face.data.FaceObject;
import com.marked.pixsee.face.custom.FaceRenderer;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-10.
 */
public class FavTwoClick extends ClickCommand {
	private WeakReference<FaceRenderer> mFaceRenderer;

	public FavTwoClick(Context context, FaceRenderer mFaceRenderer) {
		super(context);
		this.mFaceRenderer = new WeakReference<>(mFaceRenderer);
	}

	@Override
	public void execute() {
		FaceObject faceObject = new FaceObject.FaceBuilder(mContext)
				                        .withTextureId(R.drawable.hearts)
				                        .withRenderer(mFaceRenderer.get())
				                        .build();
		mFaceRenderer.get().onFavoriteClicked(faceObject);
	}
}
