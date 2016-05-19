package com.marked.pixsee.friends.di;

import android.content.Context;

import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.friends.cards.CardContract;
import com.marked.pixsee.friends.cards.CardPresenter;
import com.marked.pixsee.friends.cards.CardRepository;
import com.marked.pixsee.friends.data.FriendRepository;
import com.marked.pixsee.friends.friends.FriendPresenter;
import com.marked.pixsee.friends.friends.FriendsContract;
import com.marked.pixsee.injection.scopes.PerFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor Pop on 18-Mar-16.
 */
@Module
public class FriendModule {
	FriendsContract.View friendFragment;
	CardContract.View cardFragment;

	public FriendModule(FriendsContract.View friendFragment, CardContract.View cardFragment) {
		this.friendFragment = friendFragment;
		this.cardFragment = cardFragment;
	}

	@Provides
	@PerFragment
	FriendRepository provideRepository(Context application) {
		return new FriendRepository(PixyDatabase.getInstance(application));
	}

	@Provides
	@PerFragment
	FriendPresenter provideFriendPresenter(FriendRepository repository) {
		return new FriendPresenter(friendFragment, repository);
	}

	@Provides
	@PerFragment
	CardRepository provideCardRepository(Context application) {
		return new CardRepository(PixyDatabase.getInstance(application));
	}

	@Provides
	@PerFragment
	CardPresenter provideCardPresenter(CardRepository repository) {
		return new CardPresenter(cardFragment, repository);
	}
}
