package com.marked.pixsee.utility;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;


/**
 * Created by Tudor Pop on 15-Nov-15.
 */
public class Utils {
    public static boolean isOnline(Context context) {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showNoConnectionDialog(final Context ctx1) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(ctx1);
	    builder.setCancelable(true);
	    builder.setMessage("You need to activate cellular or wifi network in order to login.");
	    builder.setTitle("No internet !");
	    builder.setPositiveButton("Wifi", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			    ctx1.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		    }
	    });
	    builder.setNegativeButton("Cellular", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			    Intent intent = new Intent();
			    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
			    ctx1.startActivity(intent);
		    }
	    });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        }
        });


        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
	        @Override
	        public void onCancel(DialogInterface dialog) {

	        }
        });

	    builder.show();
    }


    public int getNumberDigits(String inString) {
        if (inString == null || inString.isEmpty()) return 0;
	    int count = 0;
	    for (char c : inString.toCharArray())
		    if (Character.isDigit(c)) ++count;
	    return count;
    }
}
