package com.marked.pixsee.chat;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;

import com.marked.pixsee.R;
import com.marked.pixsee.chat.data.ChatRepository;
import com.marked.pixsee.data.repository.user.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Tudor on 18-Jun-16.
 */
@RunWith(AndroidJUnit4.class)
public class ChatFragmentTest {
	@Rule
	public ActivityTestRule<ChatActivity> mChatActivityActivityTestRule =
			new ActivityTestRule<ChatActivity>(ChatActivity.class, false, true){
				@Override
				protected Intent getActivityIntent() {
					Intent intent = new Intent(InstrumentationRegistry.getContext(), ChatActivity.class);
					intent.putExtra(ChatActivity.EXTRA_CONTACT, new User("123","Pop Tudor","",""));
					return intent;
				}
			};

	ChatFragment mChatFragment;
	ChatContract.Presenter mPresenter;
	ChatRepository mChatRepository;

	@Before
	public void setUp() throws Exception {
		FragmentManager mFragmentManager = mChatActivityActivityTestRule.getActivity().getSupportFragmentManager();
		mChatFragment = (ChatFragment) mFragmentManager.findFragmentById(R.id.fragmentContainer);

//		mChatRepository = new ChatRepository(new );
//		mPresenter = new ChatPresenter(mChatFragment,)
	}

	@Test
	public void testAddMessage() throws Exception {
	}

	@Test
	public void testOnTyping() throws Exception {

	}

	@Test
	public void testOnTyping1() throws Exception {

	}

	@Test
	public void testShowCards() throws Exception {

	}

	@Test
	public void testShowNoChats() throws Exception {

	}

	@Test
	public void testSetPresenter() throws Exception {
		mChatFragment.setPresenter(mPresenter);
	}

	@Test
	public void testChatClicked() throws Exception {

	}

	@Test
	public void testRemove() throws Exception {

	}

	@Test
	public void testShowFilters() throws Exception {

	}
}