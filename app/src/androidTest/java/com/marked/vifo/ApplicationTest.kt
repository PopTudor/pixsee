package com.marked.vifo

import android.app.Application
import android.test.ApplicationTestCase

/**
 * [Testing Fundamentals](http://d.android.com/tools/testing/testing_android.html)
 */
class ApplicationTest : ApplicationTestCase<Application>(Application::class.java) {
	fun testAssertArrayEquals() {
		val expected = "test".toByteArray()
		val actual = "test".toByteArray()
		assertEquals("should be equal", expected, actual)

	}


}