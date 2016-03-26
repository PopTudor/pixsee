package friends;

import android.test.mock.MockContext;

import com.google.common.collect.Lists;
import com.marked.pixsee.friends.FriendsContract;
import com.marked.pixsee.data.User;
import com.marked.pixsee.friends.data.FriendRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.verify;

/**
 * Created by Tudor Pop on 16-Mar-16.
 * Unit tests for the implementation of {@link FriendPresenter}
 */
public class FriendViewModelTest {
	private static List<User> FRIENDS = Lists
			.newArrayList(new User("user", "xyz", "xyz@emai.com", "asd",null), new User("user1", "zxc", "zxc@emai.com", "zxc",null));

	private static List<User> EMPTY_NOTES = new ArrayList<>(0);

	@Mock
	private FriendRepository mFriendRepository;



	MockContext context = new MockContext();
	@Before
	public void setupNotesPresenter() {
		// Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
		// inject the mocks in the test the initMocks method needs to be called.
		MockitoAnnotations.initMocks(this);
//		Mockito.when(context.getApplicationContext()).thenReturn(new Application());

//		mFriendRepository = ;
		// Get a reference to the class under test
	}

	@Test
	public void testLoadFriends() throws Exception {

		verify(mFriendRepository).loadMore(FRIENDS.size());
		Assert.assertEquals(mFriendRepository.size(), FRIENDS.size());
	}

	@Test
	public void testLoadFriends1() throws Exception {

	}

	@Test
	public void testOpenFriendDetailUI() throws Exception {

	}
}