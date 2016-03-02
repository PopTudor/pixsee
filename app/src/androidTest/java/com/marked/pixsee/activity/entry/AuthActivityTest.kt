package com.marked.pixsee.activity.entry

import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.marked.pixsee.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Tudor Pop on 02-Mar-16.
 */
@RunWith(AndroidJUnit4::class)
class AuthActivityTest {
	@Rule @JvmField
	val activity = ActivityTestRule<AuthActivity>(AuthActivity::class.java)

	@Test
	fun testLogInButtonShouldStartLogInActivity() {
		onView(ViewMatchers.withId(R.id.entryLogInButton)).perform(click())
		// this view is in login activity
		onView(ViewMatchers.withId(R.id.logInButtonPixy)).check(ViewAssertions.matches(ViewMatchers.withText("login")))

		assert(true)
	}
}