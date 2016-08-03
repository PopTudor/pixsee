package com.marked.pixsee.data.user;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by Tudor Pop on 12-Dec-15.
 * Singleton class used to keep all the friends of the user
 */
public class UserRepository implements UserDatasource {
    private UserDatasource disk;
    private UserDatasource network;
    final List<User> cache = new ArrayList<>(10);
    private boolean dirtyCache;

    public UserRepository(UserDatasource disk, UserDatasource network) {
        this.disk = disk;
        this.network = network;
    }

    public int length() {
        return cache.size();
    }

    @Override
    public Observable<List<User>> getUsers() {
        if (cache.size() != 0 && !dirtyCache)
            return Observable.just(cache);


        // Query the local storage if available. If not, query the network.
        Observable<List<User>> local = disk.getUsers()
                .doOnNext(new Action1<List<User>>() {
                    @Override
                    public void call(List<User> users) {
                        cache.clear();
                        cache.addAll(users);
                    }
                });
        Observable<List<User>> remote = network.getUsers()
                .flatMap(new Func1<List<User>, Observable<User>>() {
                    @Override
                    public Observable<User> call(List<User> users) {
                        disk.saveUser(users);
                        return Observable.from(users);
                    }
                })
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        cache.add(user);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        dirtyCache = false;
                    }
                }).toList();
    /* http://blog.danlew.net/2015/06/22/loading-data-from-multiple-sources-with-rxjava/ */
        return Observable.concat(Observable.just(cache), local, remote)
                .first(new Func1<List<User>, Boolean>() {
                    @Override
                    public Boolean call(List<User> users) {
                        return users.size() > 0;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<User>> getUsers(String byName) {
        return network.getUsers(byName);
    }

    @Override
    public void saveUser(@NonNull List<User> users) {
        disk.saveUser(users);
        network.saveUser(users);
        cache.addAll(users);
    }

	@Override
	public Observable<JsonObject> saveUser(@NonNull User item) {
		cache.add(item);
		disk.saveUser(item);
		return network.saveUser(item);
	}

	@Override
	public Observable saveAppUser(@NonNull User user) {
		disk.saveAppUser(user);
		return network.saveAppUser(user);
	}

	@Override
    public Observable<List<User>> refreshUsers() {
        clear();
        return network.getUsers()
		        .flatMap(new Func1<List<User>, Observable<User>>() {
			        @Override
			        public Observable<User> call(List<User> users) {
				        return Observable.from(users);
			        }
		        })
		        .doOnNext(new Action1<User>() {
			        @Override
			        public void call(User user) {
				        cache.add(user);
			        }
		        })
		        .doOnCompleted(new Action0() {
			        @Override
			        public void call() {
				        dirtyCache = false;
				        disk.refreshUsers();
				        disk.saveUser(cache);
			        }
		        })
		        .subscribeOn(Schedulers.io())
		        .toList();
    }

    @Override
    public Observable<User> getUser(@NonNull User userId) {
        return Observable.empty();
    }

	@Override
	public User getUser(@NonNull String tablename) {
		return disk.getUser(tablename);
	}

    @Override
    public void deleteAllUsers() {
	    cache.clear();
		disk.deleteAllUsers();
    }

    @Override
    public void deleteUsers(@NonNull User userId) {
        disk.deleteUsers(userId);
        network.deleteUsers(userId);
    }

    @Override
    public void clear() {
        dirtyCache = true;
        cache.clear();
    }
}

