package com.marked.pixsee.utility

import android.content.Context
import android.widget.Toast
import org.jetbrains.anko.toast
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

/**
 * Created by Tudor Pop on 02-Mar-16.
 */
@RunWith(MockitoJUnitRunner::class)
class DataValidationTest {
	@Mock lateinit var mMockContext:Context



	@Test
	fun testValidate() {
		Mockito.`when`(Toast.makeText(mMockContext,"",Toast.LENGTH_SHORT)).thenReturn(Mockito.mock(Toast::class.java))
		Mockito.`when`(mMockContext.toast("")).thenReturn(Unit)

	}
}