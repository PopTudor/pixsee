package com.marked.pixsee.friends.friends.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.marked.pixsee.data.mapper.CursorToUserMapper;
import com.marked.pixsee.data.mapper.Mapper;
import com.marked.pixsee.data.mapper.UserToCvMapper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;


/**
 * Created by Tudor Pop on 12-Dec-15.
 * Singleton class used to keep all the friends of the user
 */
public class FriendRepository implements FriendsDatasource {
	private FriendsDatasource disk;
	private FriendsDatasource network;
	final List<User> cache = new ArrayList<>();
	private boolean dirtyCache;

	public FriendRepository(@NotNull FriendsDatasource disk, @NotNull FriendsDatasource network) {
		this.disk = disk;
		this.network = network;
	}

	private Mapper<Cursor, User> cursorToUserMapper = new CursorToUserMapper();
	private Mapper<User, ContentValues> userToCvMapper = new UserToCvMapper();

	public int length() {
		int size;
//		Cursor cursor = db.getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, null);
//		size = cursor.getCount();
//		cursor.close();
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
						return Observable.from(users);
					}
				})
				.doOnNext(new Action1<User>() {
					@Override
					public void call(User user) {
						disk.saveUser(user);
						cache.clear();
						cache.add(user);
					}
				})
				.doOnCompleted(new Action0() {
					@Override
					public void call() {
						dirtyCache = false;
					}
				}).toList();
		if (cache.size()==0)
			return remote;
		else
			return local;
	}

	@Override
	public void saveUser(@NonNull List<User> users) {
		disk.saveUser(users);
		network.saveUser(users);
		cache.addAll(users);
	}

	@Override
	public Observable<User> getUser(@NonNull User userId) {
		return null;
	}

	@Override
	public void saveUser(@NonNull User item) {
		network.saveUser(item);
		disk.saveUser(item);
		cache.set(cache.indexOf(item), item);
	}

	@Override
	public void refreshUsers() {

	}

	@Override
	public void deleteAllUsers() {

	}

	@Override
	public void deleteUsers(@NonNull User userId) {
		disk.deleteUsers(userId);
		network.deleteUsers(userId);
	}
//	fun JSONArray.contactListfromJSONArray(startingIndex:Int=0):List<User>{
//			                                                                      val contacts=ArrayList<User>()
//
//					                                                                                                  var result:JSONObject
//					                                                                                                  var id:String
//					                                                                                                  var name:String
//					                                                                                                  var email:String
//					                                                                                                  var token:String
//					                                                                                                  for(i in startingIndex..length()-1){
//					                                                                                                  result=getJSONObject(i)
//					                                                                                                  id=result.getString(FriendConstants.ID)
//					                                                                                                  name=result.getString(FriendConstants.NAME)
//					                                                                                                  email=result.getString(FriendConstants.EMAIL)
//					                                                                                                  token=result.getString(FriendConstants.TOKEN)
//
//					                                                                                                  contacts.add(User(id,name,email,token))
//					                                                                                                  }
//
//					                                                                                                  return contacts
//					                                                                                                  }
}

