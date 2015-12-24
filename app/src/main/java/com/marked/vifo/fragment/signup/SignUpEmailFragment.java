package com.marked.vifo.fragment.signup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marked.vifo.R;
import com.marked.vifo.helper.Utils;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUpEmailFragmentInteraction} interface
 * to handle interaction events.
 * Use the {@link SignUpEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpEmailFragment extends Fragment {
    private SignUpEmailFragmentInteraction mListener;
    private MaterialEditText mEmail;

    public SignUpEmailFragment() {
        // Required empty public constructor
    }

    public static SignUpEmailFragment newInstance() {
        return new SignUpEmailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up_email, container, false);
        mEmail = (MaterialEditText) rootView.findViewById(R.id.emailEditText);
        rootView.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isOnline(getActivity())){
                    Utils.showNoConnectionDialog(getActivity());
                } else {
                    String email = mEmail.getText().toString().trim();
                    if (Utils.isEmpty(email))
                        Toast.makeText(getActivity(), "Please enter an email", Toast.LENGTH_LONG).show();
                    else if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
                        onNextPressed(email);
                    else
                        Toast.makeText(getActivity(), "Invalid email adress", Toast.LENGTH_LONG).show();
                }
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onNextPressed(String email) {
        if (mListener != null) {
            try {
                mListener.onSaveEmail(URLEncoder.encode(email, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpEmailFragmentInteraction) {
            mListener = (SignUpEmailFragmentInteraction) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement SignUpEmailFragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface SignUpEmailFragmentInteraction {
        // TODO: Update argument type and name
        void onSaveEmail(String email);
    }
}
