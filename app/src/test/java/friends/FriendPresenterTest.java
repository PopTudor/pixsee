package friends;

import com.google.common.collect.Lists;
import com.marked.pixsee.data.User;
import com.marked.pixsee.data.repository.Repository;
import com.marked.pixsee.data.repository.SQLSpecification;
import com.marked.pixsee.friends.friends.FriendPresenter;
import com.marked.pixsee.friends.friends.FriendsContract;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;

import static android.view.View.VISIBLE;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Tudor Pop on 16-Mar-16.
 * Unit tests for the implementation of {@link FriendPresenter}
 */
public class FriendPresenterTest {
	private static List<User> FRIENDS = Lists.newArrayList(new User("user1", "xyz", "xyz@emai.com", "asd"),
	                                                       new User("user2", "zxc", "zxc@emai.com", "zxc"),
	                                                       new User("user3", "zxc", "zxc@emai.com", "zxc"),
	                                                       new User("user4", "zxc", "zxc@emai.com", "zxc"));


	@Mock
	Repository<User>             mRepository;
	@Mock
	FriendsContract.View mDataListener;

	FriendPresenter mPresenter;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mPresenter = new FriendPresenter(mDataListener,mRepository);
	}

	@Test
	public void testLoadFriendsForced() throws Exception {
		when(mRepository.query(any(SQLSpecification.class))).thenReturn(Observable.from(FRIENDS).take(2).toList());
		mPresenter.loadMore(true, 2);
		verify(mDataListener).onFriendsInsert(FRIENDS.subList(0, 2), 0, FRIENDS.size() / 2); /* callback got triggered */
		assertEquals(2, mPresenter.getSize());
	}


	@Test
	public void testLoadFriendsShouldAppend() throws Exception {
		when(mRepository.query(any(SQLSpecification.class))).thenReturn(Observable.just(FRIENDS.subList(0, 2))).thenReturn(Observable.just(FRIENDS));
		mPresenter.loadMore(true, 2);
		
		verify(mDataListener).onFriendsInsert(FRIENDS.subList(0, 2), 0, 2); /* load only 2 items */
		assertEquals(2, mPresenter.getSize());
		/* add friends to the already loaded list */
		mPresenter.loadMore(false, 2);
		// load another 2 items, starting with an offset of 2
		//		verify(mDataListener).onFriendsLoaded(FRIENDS.subList(2, 4), 2, 2);
		verify(mDataListener).onFriendsInsert(FRIENDS, 2, 2);
		assertEquals(6, mPresenter.getSize());
		verify(mDataListener).setRecyclerViewVisibility(VISIBLE);
	}
}