package com.marked.vifo.fragment.signup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marked.vifo.R;
import com.marked.vifo.helper.Utils;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpPassFragment extends Fragment {
    private SignUpPassFragmentInteraction mListener;
    private MaterialEditText mPasswordEditText;


    public SignUpPassFragment() {
        // Required empty public constructor
    }


    public static SignUpPassFragment newInstance() {
        return new SignUpPassFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up_pass, container, false);
        mPasswordEditText = (MaterialEditText) rootView.findViewById(R.id.passwordEditText);
        rootView.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPasswordEditText.getText().toString().trim();
                if (Utils.isEmpty(password))
                    Toast.makeText(getActivity(), "Please enter a password", Toast.LENGTH_LONG).show();
                else if(password.length()<6)
                    Toast.makeText(getActivity(), "The password must be at least 6 characters long", Toast.LENGTH_LONG).show();
                else
                    onNextPressed(password);
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onNextPressed(String password) {
        if (mListener != null) {
            mListener.onPasswordSave(password);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpPassFragmentInteraction) {
            mListener = (SignUpPassFragmentInteraction) context;
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



    public interface SignUpPassFragmentInteraction {
        // TODO: Update argument type and name
        void onPasswordSave(String password);
    }
}
