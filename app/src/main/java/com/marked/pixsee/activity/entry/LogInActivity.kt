package com.marked.pixsee.activity.entry;

import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.view.MenuItem
import android.view.View
import com.marked.pixsee.R
import com.marked.pixsee.service.LogInRegistrationIntentService
import com.marked.pixsee.service.RegistrationBroadcastReceiver
import com.marked.pixsee.utility.DataValidation
import com.marked.pixsee.utility.Utils
import com.marked.pixsee.utility.extra.GCMConstants
import com.marked.pixsee.views.DialogRegistration
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private val mProgressDialog by lazy { ProgressDialog(this) };
    private val mRegistrationBroadcastReceiver by lazy { RegistrationBroadcastReceiver(DialogRegistration(this, mProgressDialog)) }
    private val mBroadcastManagerastManager by lazy { LocalBroadcastManager.getInstance(this) };

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        moreImageView.setColorFilter(ContextCompat.getColor(this, R.color.primary));
    }

    override
    fun onResume() {
        super.onResume();
        mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, IntentFilter(GCMConstants.ACTION_LOGIN));
        mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, IntentFilter(GCMConstants.ACTION_ERROR));
    }

    override fun onPause() {
        mBroadcastManagerastManager.unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    override
    fun onClick(v: View) {
        if (Utils.isOnline(this)) // if we got internet, process the click. else tell them to activate it
            when (v.id) {
                R.id.logInButtonPixy -> {
                    if (!DataValidation(this).validate(emailEditText.text.toString(), passwordEditText.text.toString()))
                        return;
                    LogInRegistrationIntentService.startActionLogin(this, emailEditText.text.toString(), passwordEditText.text.toString());
                    mProgressDialog.setTitle("Login")
                    mProgressDialog.setMessage("Please wait...")
                    mProgressDialog.isIndeterminate = true
                    mProgressDialog.show()
                }
                R.id.signUpButton -> {
                    startActivity(Intent(this, SignUpActivity::class.java))
                }
            }
        else
            Utils.showNoConnectionDialog(this);
    }

    override
    fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.aboutMenuItem -> {
                // TODO: 30-Nov-15 implement this
            }
        }
        return false;
    }

    /*
   * Triggered when the 'more' icon is clicked
   * The icon is in fragment_log_in.xml but handeled here because it won't get triggered in LogInFragment.java
   * */
    fun showPopup(view: View) {
        val popup = PopupMenu(this, view);
        val inflater = popup.menuInflater;
        inflater.inflate(R.menu.menu_popup_login, popup.menu);
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }
}