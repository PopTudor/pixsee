package com.marked.vifo.extra;

/**
 * Created by Tudor Pop on 22-Nov-15.
 */
public interface HTTPStatusCodes {
	String ERROR_RESPONSE_STATUS_CODE = "error_response";

	int NOT_FOUND = 404;
	int REQUEST_OK = 200;
	int REQUEST_TIMEOUT = 408;
	int REQUEST_CONFLICT = 409;

	int UNPROCESSABLE_ENTITY = 422;
}