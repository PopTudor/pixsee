package com.marked.vifo.extra

/**
 * Created by Tudor Pop on 22-Nov-15.
 */
interface HTTPStatusCodes {
	companion object {
		val ERROR_RESPONSE_STATUS_CODE = "error_response"

		val NOT_FOUND = 404
		val REQUEST_TIMEOUT = 408
		val REQUEST_CONFLICT = 409

		val REQUEST_INCORRECT_PASSWORD = 600
	}
}