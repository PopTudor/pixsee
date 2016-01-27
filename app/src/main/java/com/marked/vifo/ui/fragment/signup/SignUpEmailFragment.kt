package com.marked.vifo.ui.fragment.signup

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marked.vifo.R
import com.marked.vifo.helper.Toast
import com.marked.vifo.helper.Utils
import kotlinx.android.synthetic.main.fragment_sign_up_email.*
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SignUpEmailFragmentInteraction] interface
 * to handle interaction events.
 * Use the [SignUpEmailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpEmailFragment : Fragment() {
	private var mListener: SignUpEmailFragmentInteraction? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val rootView = inflater.inflate(R.layout.fragment_sign_up_email, container, false)
		hiUser.text = "Beautiful name ${arguments.getString(NAME_TAG)},"
		nextButton.setOnClickListener {
			if (!Utils.isOnline(activity)) {
				Utils.showNoConnectionDialog(activity)
			} else {
				val email = emailEditText.text.toString().trim { it <= ' ' }
				if (Utils.isEmpty(email))
					activity.Toast("Please enter an email")
				else if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
					onNextPressed(email)
				else
					activity.Toast("Invalid email adress")
			}
		}
		// Inflate the layout for this fragment
		return rootView
	}

	// TODO: Rename method, update argument and hook method into UI event
	fun onNextPressed(email: String) {
		if (mListener != null) {
			try {
				mListener!!.onSaveEmail(URLEncoder.encode(email, "UTF-8"))
			} catch (e: UnsupportedEncodingException) {
				e.printStackTrace()
			}

		}
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		if (context is SignUpEmailFragmentInteraction)
			mListener = context as SignUpEmailFragmentInteraction?
		else
			throw RuntimeException(context!!.toString() + " must implement SignUpEmailFragmentInteraction")
	}

	override fun onDetach() {
		super.onDetach()
		mListener = null
	}

	interface SignUpEmailFragmentInteraction {
		// TODO: Update argument type and name
		fun onSaveEmail(email: String)
	}

	companion object {
		val NAME_TAG = "NAME"
		fun newInstance(name: String): SignUpEmailFragment {
			val bundle = Bundle()
			bundle.putString(NAME_TAG, name)
			val fragment = SignUpEmailFragment()
			fragment.arguments = bundle
			return fragment
		}
	}
}// Required empty public constructor
