package com.marked.vifo.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.marked.vifo.R;
import com.marked.vifo.activities.ContactListActivity;
import com.marked.vifo.extras.IHTTPStatusCodes;
import com.marked.vifo.gcm.extras.IgcmConstants;
import com.marked.vifo.gcm.services.RegistrationIntentService;
import com.marked.vifo.helper.Utils;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogInInteractionFragmentListener} interface
 * to handle interaction events.
 * Use the {@link LogInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogInFragment extends Fragment implements View.OnClickListener{
    private Button mLogInButtonPixy;
    private MaterialEditText mEmail,mPassword;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout mContainer;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private ImageView mMoreImageView;
    private Context mContext;

    private LogInInteractionFragmentListener mListener;

    public LogInFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LogInFragment newInstance() {
        LogInFragment fragment = new LogInFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_log_in, container, false);
        mLogInButtonPixy = (Button) rootView.findViewById(R.id.logInButtonPixy);
        mEmail = (MaterialEditText) rootView.findViewById(R.id.emailEditText);
        mPassword = (MaterialEditText) rootView.findViewById(R.id.passwordEditText);
        mContainer = (CoordinatorLayout) rootView.findViewById(R.id.container_log_in_fragment);
        mMoreImageView = (ImageView) rootView.findViewById(R.id.moreImageView);

        mLogInButtonPixy.setOnClickListener(this);

        mMoreImageView.setColorFilter(ContextCompat.getColor(mContext,R.color.primary));
        // GCM registration
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra(IgcmConstants.SENT_TOKEN_TO_SERVER,false)) { // if token was sent login
                    Intent intent1 = new Intent(mContext, ContactListActivity.class);
                    intent1.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                    mProgressDialog.dismiss();
                }else {
                    Snackbar snackbar=null;
                    mProgressDialog.dismiss();
                    int errorStatusCode = intent.getIntExtra(IHTTPStatusCodes.ERROR_RESPONSE_STATUS_CODE, 0);
                    switch (errorStatusCode) {
                        case IHTTPStatusCodes.REQUEST_CONFLICT:
                            snackbar = Snackbar.make(mContainer, "You already have an account", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
                            break;
                        case IHTTPStatusCodes.REQUEST_TIMEOUT:
                            snackbar = Snackbar.make(mContainer, "Timeout error", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
                            break;
                        case IHTTPStatusCodes.REQUEST_INCORRECT_PASSWORD:
                            snackbar = Snackbar.make(mContainer, "Incorrect password", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
                            break;

                    }
                    if (snackbar!=null) {
                        snackbar.getView().setBackgroundColor(Color.WHITE);
                        snackbar.show();
                    }
                }
            }
        };



        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LogInInteractionFragmentListener) {
            mListener = (LogInInteractionFragmentListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement LogInInteractionFragmentListener");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (Utils.isOnline(mContext)) // if we got internet, process the click. else tell them to activate it
            switch (v.getId()) {
                case R.id.logInButtonPixy:
                    if (!checkEnteredData())
                        break;
                    // Start IntentService to register this application with GCM.
                    intent = new Intent(mContext, RegistrationIntentService.class);
                    intent.putExtra("email", mEmail.getText().toString());
                    intent.putExtra("password", mPassword.getText().toString());
                    mContext.startService(intent);
                    mProgressDialog = ProgressDialog.show(mContext, "Login", "Please wait...", true);
                    break;
            }
        else
            Utils.showNoConnectionDialog(mContext);
    }
    public boolean checkEnteredData() {
        String emailString = mEmail.getText().toString().trim();
        String passwordString = mPassword.getText().toString().trim();
        if (emailString.isEmpty()) {
            Snackbar view = Snackbar.make(mContainer, "The email field is empty", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
            view.getView().setBackgroundColor(Color.WHITE);
            view.show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            Snackbar view = Snackbar.make(mContainer, "You must enter a valid email", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
            view.getView().setBackgroundColor(Color.WHITE);
            view.show();
            return false;
        }
        if (passwordString.isEmpty()) {
            Snackbar view = Snackbar.make(mContainer, "The password field is empty", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
            view.getView().setBackgroundColor(Color.WHITE);
            view.show();
            return false;
        }
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface LogInInteractionFragmentListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(IgcmConstants.SENT_TOKEN_TO_SERVER));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
