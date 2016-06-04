package com.marked.pixsee.friends;

import android.view.View;

import com.marked.pixsee.commands.Command;
import com.marked.pixsee.friends.data.User;
import com.marked.pixsee.friends.data.datasource.FriendsDatasource;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor Pop on 23-Mar-16.
 */
public class FriendPresenter implements FriendsContract.Presenter {
    private FriendsDatasource repository;
    private WeakReference<FriendsContract.View> mView;
    private int size = 0;
    @Inject
    public FriendPresenter(FriendsContract.View view, FriendsDatasource repository) {
        this.repository = repository;
        this.mView = new WeakReference<FriendsContract.View>(view);
        this.mView.get().setPresenter(this);
    }

    @Override
    public void loadMore(int limit,@NotNull String text) {
        if (text.isEmpty())
            return;
        repository.getUsers(text)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<User>>() {
                    @Override
                    public void call(List<User> users) {
                        mView.get().onFriendsReplace(users);
                    }
                });
    }

    public int getSize() {
        return size;
    }

    @Override
    public void loadMore(final int limit, boolean forceUpdate) {
        if (forceUpdate) {
            final Subscription subscription = repository.refreshUsers()
                    .flatMap(new Func1<List<User>, Observable<List<User>>>() {
                        @Override
                        public Observable<List<User>> call(List<User> users) {
                            return Observable.from(users).toSortedList();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<User>>() {
                                   @Override
                                   public void call(List<User> users) {
                                       if (users.size() > 0) {
                                           mView.get().setRecyclerViewVisibility(View.VISIBLE);
                                           mView.get().onFriendsReplace(users);
                                           size = users.size();
                                       }
                                   }
                               }
                            , new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    if (throwable instanceof SocketTimeoutException)
                                        mView.get().showNoInternetConnection();
                                    else
                                        mView.get().showNoFriends();
                                }
                            });
        } else {
            final Subscription subscription = repository.getUsers()
                    .flatMap(new Func1<List<User>, Observable<List<User>>>() {
                        @Override
                        public Observable<List<User>> call(List<User> users) {
                            return Observable.from(users).toSortedList();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<User>>() {
                                   @Override
                                   public void call(List<User> users) {
                                       if (users.size() > 0) {
                                           mView.get().setRecyclerViewVisibility(View.VISIBLE);
                                           mView.get().onFriendsReplace(users);
                                           size = users.size();
                                       }
                                   }
                               }
                            , new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    mView.get().showNoFriends();
                                }
                            });
        }
    }

    @Override
    public void start() {
        loadMore(25, false);
    }


    @Override
    public void execute(Command command) {
        command.execute();
    }

    @Override
    public void loadMore(int num) {
        loadMore(num, false);
    }

    @Override
    public void clear() {
        repository.clear();
    }

    @Override
    public void actionInviteClick() {
        mView.get().showInviteFriends();
    }
}
