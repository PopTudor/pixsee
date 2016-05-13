package com.marked.pixsee.activity.entry;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.marked.pixsee.R;
import com.marked.pixsee.entry.AuthActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by Tudor Pop on 02-Mar-16.
 */
@RunWith(AndroidJUnit4.class)
class AuthActivityTest {
	@Rule
	ActivityTestRule<AuthActivity> activity = new ActivityTestRule<>(AuthActivity.class);


	@Test
	void testLogInButtonShouldStartLogInActivity() {

		Espresso.onView(ViewMatchers.withId(R.id.entryLogInButton)).perform(ViewActions.click());
		// this view is in login activity
		Espresso.onView(ViewMatchers.withId(R.id.logInButtonPixy)).check(ViewAssertions.matches(ViewMatchers.withText("login")));
	}
}