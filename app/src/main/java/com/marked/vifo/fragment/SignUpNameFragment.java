package com.marked.vifo.fragment;

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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUpNameFragmentInteraction} interface
 * to handle interaction events.
 * Use the {@link SignUpNameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpNameFragment extends Fragment {
    private SignUpNameFragmentInteraction mListener;
    private MaterialEditText mNameEditText;

    public SignUpNameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SignUpNameFragment.
     */
    public static SignUpNameFragment newInstance() {
        return new SignUpNameFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up_name, container, false);
        mNameEditText = (MaterialEditText) rootView.findViewById(R.id.contactNameTextView);
        rootView.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameEditText.getText().toString().trim();
                if (Utils.isEmpty(name))
                    Toast.makeText(getActivity(), "Please enter your name", Toast.LENGTH_LONG).show();
                else
                    onNextPressed(name);
            }
        });
        return rootView;
    }

    public void onNextPressed(String name) {
        if (mListener != null) {
            mListener.onSaveName(name);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpNameFragmentInteraction) {
            mListener = (SignUpNameFragmentInteraction) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement SignUpNameFragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface SignUpNameFragmentInteraction {
        void onSaveName(String name);
    }
}
