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
        if (isEmpty(email)){
            if (coordinatorLayout != null) {
                Snackbar view = Snackbar.make(coordinatorLayout, "The email field is empty", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
                view.getView().setBackgroundColor(Color.WHITE);
                view.show();
            }else
                Toast.makeText(mContext, "The email field is empty", Toast.LENGTH_SHORT).show();
        }
        if (isEmpty(password)) {
            if (coordinatorLayout != null) {
                Snackbar view = Snackbar.make(coordinatorLayout, "The password field is empty", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
                view.getView().setBackgroundColor(Color.WHITE);
                view.show();
            }else
                Toast.makeText(mContext, "The password field is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
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

    /**
     * Check if given string is empty
     * @param string
     * @return
     */
    public static boolean isEmpty(String string){
        String str = string.trim();
        return string == null||str.isEmpty();
    }

    public static int getNumberDigits(String inString){
        if (isEmpty(inString)) {
            return 0;
        }
        int numDigits= 0;
        int length= inString.length();
        for (int i = 0; i < length; i++)
            if (Character.isDigit(inString.charAt(i)))
                numDigits++;
        return numDigits;
    }

    private float getRating(String password) throws IllegalArgumentException {
        if (password == null)
            throw new IllegalArgumentException();
        int passwordStrength = 0;
        if (password.length() > 5) {
            passwordStrength++;
        } // minimal pw length of 6
        if (password.toLowerCase().equals(password)) {
            passwordStrength++;
        } // lower and upper case
        if (password.length() > 8) {
            passwordStrength++;
        } // good pw length of 9+
        int numDigits = Utils.getNumberDigits(password);
        if (numDigits > 0 && numDigits != password.length()) {
            passwordStrength++;
        } // contains digits and non-digits
        return (float) passwordStrength;
    }


    public static Snackbar createWhiteSnackBar(Context context,CoordinatorLayout coordinatorLayout, String message) {
        return Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(context, R.color.white));
    }
}
