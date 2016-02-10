package com.marked.vifo

import android.app.Application
import android.test.ApplicationTestCase
import com.marked.vifo.helper.DataValidationTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Created by Tudor Pop on 24-Jan-16.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(DataValidationTest::class)
class MainUnitTest : ApplicationTestCase<Application>(Application::class.java) {
	fun testAssertArrayEquals() {
		val expected = "test".toByteArray()
		val actual = "test".toByteArray()
		//		assertEquals("should be equal", expected, actual)
	}
}