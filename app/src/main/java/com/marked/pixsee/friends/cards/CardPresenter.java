package com.marked.pixsee.friends.cards;

import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.repository.Repository;

/**
 * Created by Tudor on 2016-05-19.
 */
public class CardPresenter implements CardContract.Presenter {
	private Repository<Message> mRepository;
	private CardContract.View mView;

	public CardPresenter(CardContract.View mView, Repository<Message> mRepository) {
		this.mRepository = mRepository;
		this.mView = mView;
		this.mView.setPresenter(this);
	}

	@Override
	public void start() {

	}
}
