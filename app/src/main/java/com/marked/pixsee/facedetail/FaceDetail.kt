package com.marked.pixsee.facedetail

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import com.marked.pixsee.R
import com.marked.pixsee.face.SelfieActivity
import kotlinx.android.synthetic.main.activity_face_detail.*
import kotlinx.android.synthetic.main.toolbar.*

class FaceDetail : AppCompatActivity() {
    private var button: ImageButton? = null
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
        if (FaceDetail.AUTO_HIDE) {
            delayedHide(FaceDetail.AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    fun showAnimation(): Unit {
        app_bar.animate()
                .alpha(1.0f)
                .setDuration(300L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        app_bar?.visibility = View.VISIBLE
                    }
                })
                .start()
    }

    fun hideAnimation(): Unit {
        app_bar.animate()
                .alpha(0.0f)
                .setDuration(300L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        app_bar?.visibility = View.GONE
                    }
                })
                .start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detail)
        photoImageView.setImageURI(Uri.parse("file://" + intent.getStringExtra(SelfieActivity.PHOTO_EXTRA)))
        photoRendererImageView.setImageURI(Uri.parse("file://" + intent.getStringExtra(SelfieActivity.PHOTO_RENDERER_EXTRA)))

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        mVisible = true
        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent.apply {
            setOnClickListener { toggle() }
            setOnTouchListener(mDelayHideTouchListener)
        }
        button = findViewById(R.id.button) as ImageButton
        button?.setOnClickListener { }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

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
}
