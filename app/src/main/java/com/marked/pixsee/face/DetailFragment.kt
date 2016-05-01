package com.marked.pixsee.face

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.MessageDialog
import com.facebook.share.widget.ShareDialog
import com.marked.pixsee.R
import com.marked.pixsee.utility.toast
import kotlinx.android.synthetic.main.fragment_face_detail.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class DetailFragment : Fragment() {
    private val mHideHandler = Handler()
    private var mVisible: Boolean = false
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        showAnimation()
    }
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        if (DetailFragment.AUTO_HIDE) {
            delayedHide(DetailFragment.AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private var mListener: OnFragmentInteractionListener? = null
    var pictureFile: String? = null

    private lateinit var mAppbar:AppBarLayout

    private val facebookCallback by lazy {
        object : FacebookCallback<Sharer.Result> {
            override fun onCancel() {
                toast("Cancel...")
            }

            override fun onError(error: FacebookException?) {
                toast("Error...")
            }

            override fun onSuccess(result: Sharer.Result?) {
                toast("Posted...")
            }
        }
    }
    private val callbackManager: CallbackManager by lazy { com.facebook.CallbackManager.Factory.create() };
    private val shareDialog: ShareDialog by lazy { ShareDialog(this) };

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mPictureFile = getArguments().getString(SelfieActivity.PHOTO_EXTRA, "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_face_detail, container, false)
        shareDialog.registerCallback(callbackManager, facebookCallback)

        mAppbar = rootView.app_bar
        rootView.saveImageButton.setOnClickListener {
            saveToDisk()
        }

        rootView.shareFacebookImageButton.setOnClickListener {
            if (ShareDialog.canShow(SharePhotoContent::class.java) && pictureFile != null) {
                val photo = SharePhoto.Builder()
                        .setCaption("Pixsee")
                        .setUserGenerated(true)
                        .setImageUrl(Uri.fromFile(File(pictureFile)))
                        .build();
                val content = SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                shareDialog.show(content);
            }
        }

        rootView.sendFacebookImageButton.setOnClickListener {
            if(pictureFile !=null) {
                val photo = SharePhoto.Builder()
                        .setCaption("Pixsee")
                        .setUserGenerated(true)
                        .setImageUrl(Uri.fromFile(File(pictureFile)))
                        .build();
                val content = SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                MessageDialog.show(this, content);
            }
        }
        return rootView
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context as OnFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            //			activity.onBackPressed()
        }
    }

    private fun saveToDisk() {
//        val bitmap = BitmapUtils.getBitmapFromView(fullscreenAppBar)
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss")
        val now = Date()
        val prefix = "PX_IMG_" + formatter.format(now)
        toast("Image Saved !")
    }

    fun showAnimation(): Unit {
        mAppbar.animate()
                .alpha(1.0f)
                .setDuration(300L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        mAppbar.visibility = View.VISIBLE
                    }
                })
                .start()
    }

    fun hideAnimation(): Unit {
        mAppbar.animate()
                .alpha(0.0f)
                .setDuration(300L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        mAppbar.visibility = View.GONE
                    }
                })
                .start()
    }

    override fun onStart() {
        super.onStart()
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(3000)
    }

    private fun toggle() = if (mVisible) hide() else show()

    private fun hide() {
        // Hide UI first
        hideAnimation()

        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
    }

    @SuppressLint("InlinedApi")
    private fun show() {
        // Show the system bar
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        fun newInstance(): DetailFragment {
            val detailFragment = DetailFragment()
            val bundle = Bundle()
//            bundle.putString(SelfieActivity.PHOTO_EXTRA, picturePath)
            detailFragment.arguments = bundle
            return detailFragment
        }

        /**
         * Whether or not the system UI should be auto-hidden after
         * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 8000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 100
    }
}// Required empty public constructor
