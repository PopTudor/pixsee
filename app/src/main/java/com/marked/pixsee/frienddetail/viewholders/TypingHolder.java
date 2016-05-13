package com.marked.pixsee.frienddetail.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.marked.pixsee.R;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

/**
 * Created by Tudor Pop on 02-Jan-16.
 * This makes the animation dots happens
 */
public class TypingHolder extends RecyclerView.ViewHolder {
	public TypingHolder(View itemView) {
		super(itemView);
	}
	public void start(){
		((DilatingDotsProgressBar)itemView.findViewById(R.id.dot)).showNow();
	}
	public void stop(){
		((DilatingDotsProgressBar)itemView.findViewById(R.id.dot)).hideNow();
	}
}
