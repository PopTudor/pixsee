package com.marked.pixsee.friends.cards;

import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.friends.cards.data.MessageDatasource;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 2016-05-19.
 */
public class CardPresenter implements CardContract.Presenter {
	private MessageDatasource mRepository;
	private CardContract.View mView;

	public CardPresenter(CardContract.View mView, MessageDatasource mRepository) {
		this.mRepository = mRepository;
		this.mView = mView;
		this.mView.setPresenter(this);
	}

	@Override
	public void start() {
//		loadMore(10, true);
	}

	@Override
	public void loadMore(int limit) {
//		loadMore(limit, false);
	}

	@Override
	public void loadMore(int limit, String text) {
		mRepository.getMessages()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<Message>>() {
					@Override
					public void call(List<Message> messages) {
						mView.showCards(messages);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						mView.showNoCards();
					}
				});
	}

	@Override
	public void messageClicked(Message message) {

	}

	@Override
	public void moreClicked(Message message) {

	}

	@Override
	public void favoriteClicked(Message message) {

	}

	@Override
	public void replyClicked(Message message) {

	}

	@Override
	public void loadMore(int limit, boolean forceUpdate) {

	}

	@Override
	public void execute(Command command) {
		command.execute();
	}
}
