package com.marked.pixsee.activity.entry;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.marked.pixsee.R;
import com.marked.pixsee.login.LogInActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by Tudor Pop on 02-Mar-16.
 */
@RunWith(AndroidJUnit4.class)
class AuthActivityTest {
	@Rule
	ActivityTestRule<LogInActivity> activity = new ActivityTestRule<>(LogInActivity.class);


	@Test
	void testLogInButtonShouldStartLogInActivity() {

		Espresso.onView(ViewMatchers.withId(R.id.logInButtonPixy)).perform(ViewActions.click());
		// this view is in login activity
		Espresso.onView(ViewMatchers.withId(R.id.logInButtonPixy)).check(ViewAssertions.matches(ViewMatchers.withText("login")));
	}
}