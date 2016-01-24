package com.marked.vifo.helper

import android.app.Activity
import android.app.Application
import android.test.ApplicationTestCase
import android.test.mock.MockContext
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * Created by Tudor Pop on 24-Jan-16.
 */
class DataValidationTest : ApplicationTestCase<Application>(Application::class.java) {
	var email: String = ""
	var password: String = ""
	@Mock
	var context = MockContext();

	@Before
	public fun init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	fun testValidate() {
		val context = Mockito.mock(Activity::class.java)
		org.junit.Assert.assertTrue("The email or password is empty", DataValidation(context, email, password).validate())
	}
}