package com.marked.pixsee.signup

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marked.pixsee.R
import com.marked.pixsee.utility.Utils
import kotlinx.android.synthetic.main.fragment_sign_up_email.*
import kotlinx.android.synthetic.main.fragment_sign_up_email.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.io.UnsupportedEncodingException
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
		rootView.hiUser.text = "Beautiful name ${arguments.getString(Companion.NAME_TAG)},"
		rootView.nextButton.onClick {
			if (!Utils.isOnline(activity)) {
				Utils.showNoConnectionDialog(activity)
			} else {
				val email = emailEditText.text.toString().trim { it <= ' ' }
				if (email.isNullOrBlank())
					activity.toast("Please enter an email")
				else if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
					onNextPressed(email)
				else
					activity.toast("Invalid email adress")
			}
		}
		// Inflate the layout for this fragment
		return rootView
	}

	// TODO: Rename method, update argument and hook method into UI event
	fun onNextPressed(email: String) {
		if (mListener != null) {
			try {
				mListener!!.onSaveEmail(email)
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
			bundle.putString(Companion.NAME_TAG, name)
			val fragment = SignUpEmailFragment()
			fragment.arguments = bundle
			return fragment
		}
	}
}// Required empty public constructor
