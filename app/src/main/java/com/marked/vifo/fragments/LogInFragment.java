package com.marked.vifo.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.marked.vifo.R;
import com.marked.vifo.extras.IHTTPStatusCodes;
import com.marked.vifo.gcm.RegistrationBroadcastReceiver;
import com.marked.vifo.gcm.extras.IgcmConstants;
import com.marked.vifo.gcm.services.LogInRegistrationIntentService;
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
public class LogInFragment extends Fragment implements View.OnClickListener {
    private Button mLogInButtonPixy;
    private MaterialEditText mEmail, mPassword;
    private RegistrationBroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout mContainer;
    private ImageView mMoreImageView;
    private Context mContext;
    private LocalBroadcastManager mBroadcastManagerastManager;

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
        mBroadcastManagerastManager = LocalBroadcastManager.getInstance(mContext);
        // GCM registration
        mRegistrationBroadcastReceiver = new RegistrationBroadcastReceiver();
        mRegistrationBroadcastReceiver.setOnRegistrationBroadcastReceiverListener(new RegistrationBroadcastReceiver.RegistrationBroadcastReceiverListener() {
            @Override
            public void dismissDialog() {mProgressDialog.dismiss();}

            @Override
            public void actionError(int errorStatusCode) {
                Snackbar snackbar = null;
                switch (errorStatusCode) {
                    case IHTTPStatusCodes.REQUEST_CONFLICT:
                        snackbar = createWhiteSnackBar(mContext, mContainer, "You already have an account");
                        break;
                    case IHTTPStatusCodes.REQUEST_TIMEOUT:
                        snackbar = createWhiteSnackBar(mContext,mContainer,"Timeout error");
                        break;
                    case IHTTPStatusCodes.REQUEST_INCORRECT_PASSWORD:
                        snackbar = createWhiteSnackBar(mContext,mContainer,"Incorrect password");
                        break;
                    case IHTTPStatusCodes.NOT_FOUND:
                        snackbar = createWhiteSnackBar(mContext, mContainer, "We are sorry, but we did not found you");
                        break;
                }
                if (snackbar != null) {
                    snackbar.getView().setBackgroundColor(Color.WHITE);
                    snackbar.show();
                }
            }
        });
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
        mMoreImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.primary));

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
                    if (!Utils.checkEnteredData(mContext, mEmail.getText().toString(), mPassword.getText().toString(), mContainer))
                        break;
                    LogInRegistrationIntentService.startActionLogin(mContext, mEmail.getText().toString(), mPassword.getText().toString());
                    mProgressDialog = ProgressDialog.show(mContext, "Login", "Please wait...", true);
                    break;
            }
        else
            Utils.showNoConnectionDialog(mContext);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(IgcmConstants.ACTION_LOGIN));
        mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(IgcmConstants.ACTION_SIGNUP));
        mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(IgcmConstants.ACTION_RECOVERY));
        mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(IgcmConstants.ACTION_ERROR));
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

    private Snackbar createWhiteSnackBar(Context context, CoordinatorLayout coordinatorLayout,String message){
        return Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(mContext, R.color.white));
    }
}
