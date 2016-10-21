package com.marked.pixsee.ui.selfie.commands;

import android.content.Context;

import com.marked.pixsee.R;
import com.marked.pixsee.ui.commands.ClickCommand;
import com.marked.pixsee.ui.selfie.SelfieFragment;
import com.marked.pixsee.ui.selfie.data.SelfieObject;

import org.rajawali3d.renderer.Renderer;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-10.
 */
public class FavOneClick extends ClickCommand {
	private WeakReference<SelfieFragment.OnFavoritesListener> mFaceRenderer;

	public FavOneClick(Context context, SelfieFragment.OnFavoritesListener mSelfieRenderer) {
		super(context);
		this.mFaceRenderer = new WeakReference<>(mSelfieRenderer);
	}

	@Override
	public void execute() {
		mFaceRenderer.get().onFavoriteClicked(new SelfieObject.FaceBuilder(mContext)
				                                      .withTextureId(R.drawable.mlg)
				                                      .withRenderer((Renderer) mFaceRenderer.get())
				                                      .build());
	}
}
