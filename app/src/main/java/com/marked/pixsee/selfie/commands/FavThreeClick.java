package com.marked.pixsee.selfie.commands;

import android.content.Context;

import com.marked.pixsee.R;
import com.marked.pixsee.commands.ClickCommand;
import com.marked.pixsee.selfie.data.SelfieObject;
import com.marked.pixsee.selfie.custom.SelfieRenderer;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-10.
 */
public class FavThreeClick extends ClickCommand {
	private WeakReference<SelfieRenderer> mFaceRenderer;

	public FavThreeClick(Context context, SelfieRenderer mSelfieRenderer) {
		super(context);
		this.mFaceRenderer = new WeakReference<>(mSelfieRenderer);
	}

	@Override
	public void execute() {
		SelfieObject selfieObject = new SelfieObject.FaceBuilder(mContext)
				                        .withTextureId(R.drawable.kiss)
				                        .withRenderer(mFaceRenderer.get())
				                        .build();
		mFaceRenderer.get().onFavoriteClicked(selfieObject);
	}
}
