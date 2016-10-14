package com.marked.pixsee.features.selfie.commands;

import android.content.Context;

import com.marked.pixsee.R;
import com.marked.pixsee.commands.ClickCommand;
import com.marked.pixsee.features.selfie.SelfieFragment;
import com.marked.pixsee.features.selfie.data.SelfieObject;

import org.rajawali3d.renderer.Renderer;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-10.
 */
public class FavTwoClick extends ClickCommand {
	private WeakReference<SelfieFragment.OnFavoritesListener> mFaceRenderer;

	public FavTwoClick(Context context, SelfieFragment.OnFavoritesListener mSelfieRenderer) {
		super(context);
		this.mFaceRenderer = new WeakReference<>(mSelfieRenderer);
	}

	@Override
	public void execute() {
		SelfieObject selfieObject = new SelfieObject.FaceBuilder(mContext)
				                        .withTextureId(R.drawable.hearts)
				                        .withRenderer((Renderer) mFaceRenderer.get())
				                        .build();
		mFaceRenderer.get().onFavoriteClicked(selfieObject);
	}
}
