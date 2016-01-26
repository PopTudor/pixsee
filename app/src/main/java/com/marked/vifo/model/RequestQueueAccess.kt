package com.marked.vifo.model

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest

import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

/**
 * Created by Tudor Pop on 01-Dec-15.
 */
class RequestQueueAccess private constructor(val queue: RequestQueue) {
	companion object {
		private var sQueue: RequestQueueAccess? = null
		fun getInstance(context: Context): RequestQueueAccess? {
			if (sQueue == null)
				sQueue = RequestQueueAccess(Volley.newRequestQueue(context))
			return sQueue
		}
	}

	fun add(request: JsonObjectRequest) {
		queue.add(request)
	}

	fun add(request: JsonRequest<Any>) {
		queue.add(request)
	}

	fun add(request: StringRequest) {
		queue.add(request)
	}
}
