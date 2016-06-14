package com.marked.pixsee.signup;


import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.marked.pixsee.networking.HTTPStatusCodes;

/**
 * Created by Tudor Pop on 23-Jan-16.
 */
public class DialogRegistration  {
	private ProgressDialog progressDialog ;
	private Context context ;

	public DialogRegistration(ProgressDialog progressDialog, Context context) {
		this.progressDialog = progressDialog;
		this.context = context;
	}

	public void onError(int errorStatusCode) {
		switch (errorStatusCode){
			case HTTPStatusCodes.REQUEST_CONFLICT:
				Toast.makeText(context,"You already have an account",Toast.LENGTH_SHORT).show();
				break;
			case HTTPStatusCodes.REQUEST_TIMEOUT:
				Toast.makeText(context,"Timeout error",Toast.LENGTH_SHORT).show();
				break;
			case HTTPStatusCodes.UNPROCESSABLE_ENTITY:
				Toast.makeText(context,"Incorrect password",Toast.LENGTH_SHORT).show();
				break;
			case HTTPStatusCodes.NOT_FOUND:
				Toast.makeText(context,"We are sorry, but we did not found you",Toast.LENGTH_SHORT).show();
				break;

		}
	}

}