package com.marked.pixsee.utility

import android.content.Context
import android.widget.Toast
import org.jetbrains.anko.toast
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.*

/**
 * Created by Tudor Pop on 02-Mar-16.
 */
@RunWith(MockitoJUnitRunner::class)
class DataValidationTest {
	@Mock lateinit var mMockContext:Context



	@Test
	fun testValidate() {
		Mockito.mock(Toast.makeText(mMockContext, "Matchers.anyString()",Toast.LENGTH_SHORT).show().javaClass)
		Mockito.`when`(Toast.makeText(mMockContext,"",Toast.LENGTH_SHORT)).thenReturn(Mockito.mock(Toast::class.java))
		Mockito.`when`(mMockContext.toast("")).thenReturn(Unit)
	}
}