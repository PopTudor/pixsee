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
public class FavOneClick extends ClickCommand {
	private WeakReference<FaceRenderer> mFaceRenderer;

	public FavOneClick(Context context, FaceRenderer mFaceRenderer) {
		super(context);
		this.mFaceRenderer = new WeakReference<>(mFaceRenderer);
	}

	@Override
	public void execute() {
		mFaceRenderer.get().onFavoriteClicked(new FaceObject.FaceBuilder(mContext)
				                                      .withTextureId(R.drawable.mlg)
				                                      .withRenderer(mFaceRenderer.get())
				                                      .build());
	}
}
