package com.marked.vifo.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.marked.vifo.R;
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
public class LogInFragment extends Fragment implements View.OnClickListener ,RegistrationBroadcastReceiver.Dismiss{
    private Button mLogInButtonPixy;
    private MaterialEditText mEmail, mPassword;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout mContainer;
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

        // GCM registration
        mRegistrationBroadcastReceiver = new RegistrationBroadcastReceiver(mContext,this,mContainer);

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
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
        broadcastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(IgcmConstants.ACTION_LOGIN));
        broadcastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(IgcmConstants.ACTION_SIGNUP));
        broadcastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(IgcmConstants.ACTION_RECOVERY));
        broadcastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(IgcmConstants.ACTION_ERROR));
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

    @Override
    public void dismissDialog() {
        mProgressDialog.dismiss();
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

}
