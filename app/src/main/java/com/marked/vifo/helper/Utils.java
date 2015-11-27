package com.marked.vifo.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.widget.Toast;

import com.marked.vifo.R;

/**
 * Created by Tudor Pop on 15-Nov-15.
 */
public class Utils {
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showNoConnectionDialog(Context ctx1) {
        final Context ctx = ctx1;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(true);
        builder.setMessage("You need to activate cellular or wifi network in order to login.");
        builder.setTitle("No internet !");
        builder.setPositiveButton("Wifi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ctx.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        builder.setNegativeButton("Cellular", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                ctx.startActivity(intent);
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });

        builder.show();
    }

    /**
     * @param email the email to check if it has the right format
     * @param password the password to check if is ok
     * @param coordinatorLayout the coordinator needed for the Snackbar or null for Toast
     * @return true if everything works fine, false otherwise
     */
    public static boolean checkEnteredData(Context mContext, String email, String password, CoordinatorLayout coordinatorLayout) {
        String emailString = email.trim();
        String passwordString = password.trim();
        if (emailString.isEmpty()) {
            if (coordinatorLayout != null) {
                Snackbar view = Snackbar.make(coordinatorLayout, "The email field is empty", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
                view.getView().setBackgroundColor(Color.WHITE);
                view.show();
            }else
                Toast.makeText(mContext, "The email field is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passwordString.isEmpty()) {
            if (coordinatorLayout != null) {
                Snackbar view = Snackbar.make(coordinatorLayout, "The password field is empty", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
                view.getView().setBackgroundColor(Color.WHITE);
                view.show();
            }else
                Toast.makeText(mContext, "The password field is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            if (coordinatorLayout!=null) {
                Snackbar view = Snackbar.make(coordinatorLayout, "You must enter a valid email", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
                view.getView().setBackgroundColor(Color.WHITE);
                view.show();
            }else
                Toast.makeText(mContext, "You must enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
