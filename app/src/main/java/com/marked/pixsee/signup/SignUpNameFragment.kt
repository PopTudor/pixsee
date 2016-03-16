package com.marked.pixsee.signup

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marked.pixsee.R
import kotlinx.android.synthetic.main.fragment_sign_up_name.*
import org.jetbrains.anko.toast

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SignUpNameFragmentInteraction] interface
 * to handle interaction events.
 * Use the [SignUpNameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpNameFragment : Fragment() {
	private var mListener: SignUpNameFragmentInteraction? = null

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val rootView = inflater!!.inflate(R.layout.fragment_sign_up_name, container, false)
		rootView.findViewById(R.id.nextButton).setOnClickListener {
			val name = friendNameTextView.text.toString().trim { it <= ' ' }
			if (name.isNullOrBlank())
				activity.toast("Please enter your name")
			else
				onNextPressed(name)
		}
		return rootView
	}

	fun onNextPressed(name: String) {
		if (mListener != null) mListener?.onSaveName(name)
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		if (context is SignUpNameFragmentInteraction)
			mListener = context as SignUpNameFragmentInteraction?
		else
			throw RuntimeException(context?.toString() + " must implement SignUpNameFragmentInteraction")
	}

	override fun onDetach() {
		super.onDetach()
		mListener = null
	}

	interface SignUpNameFragmentInteraction {
		fun onSaveName(name: String)
	}

	companion object {

		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 * @return A new instance of fragment SignUpNameFragment.
		 */
		fun newInstance(): SignUpNameFragment {
			return SignUpNameFragment()
		}
	}
}// Required empty public constructor
