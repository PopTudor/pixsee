package com.marked.pixsee.face.commands;

import android.content.Context;

import com.marked.pixsee.R;
import com.marked.pixsee.commands.ClickCommand;
import com.marked.pixsee.face.FaceObject;
import com.marked.pixsee.face.FaceRenderer;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-10.
 */
public class FavThreeClick extends ClickCommand {
	private WeakReference<FaceRenderer> mFaceRenderer;

	public FavThreeClick(Context context, FaceRenderer mFaceRenderer) {
		super(context);
		this.mFaceRenderer = new WeakReference<>(mFaceRenderer);
	}

	@Override
	public void execute() {
		FaceObject faceObject = new FaceObject.FaceBuilder(mContext)
				                        .withTextureId(R.drawable.kiss)
				                        .withRenderer(mFaceRenderer.get())
				                        .build();
		mFaceRenderer.get().onFavoriteClicked(faceObject);
	}
}
