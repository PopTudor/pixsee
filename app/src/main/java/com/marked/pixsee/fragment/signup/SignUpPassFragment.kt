package com.marked.pixsee.fragment.signup

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marked.pixsee.R
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.fragment_sign_up_pass.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

class SignUpPassFragment : Fragment() {
	private var mListener: SignUpPassFragmentInteraction? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val rootView = inflater.inflate(R.layout.fragment_sign_up_pass, container, false)
		rootView.nextButton.onClick {
			val password = passwordEditText.text.toString().trim { it <= ' ' }
			if (password.isNullOrBlank())
				activity.toast("Please enter a password")
			else if (password.length < 6)
				activity.toast("The password must be at least 6 characters long")
			else
				onNextPressed(password)
		}
		// Inflate the layout for this fragment
		return rootView
	}

	// TODO: Rename method, update argument and hook method into UI event
	fun onNextPressed(password: String) {
		if (mListener != null) {
			mListener?.onSavePassword(password)
		}
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		if (context is SignUpPassFragmentInteraction) {
			mListener = context as SignUpPassFragmentInteraction?
		} else throw RuntimeException(context.toString() + " must implement SignUpEmailFragmentInteraction")
	}

	override fun onDetach() {
		super.onDetach()
		mListener = null
	}


	interface SignUpPassFragmentInteraction {
		// TODO: Update argument type and name
		fun onSavePassword(password: String)
	}

	companion object {
		fun newInstance(): SignUpPassFragment {
			return SignUpPassFragment()
		}
	}
}// Required empty public constructor
