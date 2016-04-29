package com.marked.pixsee.facedetail

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.imagepipeline.request.BasePostprocessor
import com.facebook.share.Sharer
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.MessageDialog
import com.facebook.share.widget.ShareDialog
import com.marked.pixsee.R
import com.marked.pixsee.face.SelfieActivity
import com.marked.pixsee.face.Utils
import kotlinx.android.synthetic.main.activity_face_detail.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FaceDetail : AppCompatActivity() {
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
    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() };
    private val shareDialog: ShareDialog by lazy {
        ShareDialog(this)
    };
    lateinit var overlayBitmap: Bitmap

    val redMeshPostprocessor = object : BasePostprocessor() {
        override fun getName(): String {
            return "redMeshPostprocessor";
        }

        override fun process(bitmap: Bitmap) {


            for (x in 1..bitmap.getWidth() step 2) {
                for (y in 1..bitmap.getHeight() step 2) {
                    bitmap.setPixel(x, y, Color.RED);
                }
            }
        }
    }
    lateinit var mPictureFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detail)
        Fresco.getImagePipeline().clearCaches()

        setupToolbar()
        setupImage()
        shareDialog.registerCallback(callbackManager, facebookCallback)

        shareFacebookImageButton.setOnClickListener {
            if (ShareDialog.canShow(SharePhotoContent::class.java)) {
                saveToDisk()
                val photo = SharePhoto.Builder()
                        .setCaption("Pixsee")
                        .setUserGenerated(true)
                        .setImageUrl(Uri.fromFile(mPictureFile))
                        .build();
                val content = SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                shareDialog.show(content);
            }
        }

        sendFacebookImageButton.setOnClickListener {
            saveToDisk()
            val photo = SharePhoto.Builder()
                    .setCaption("Pixsee")
                    .setUserGenerated(true)
                    .setImageUrl(Uri.fromFile(mPictureFile))
                    .build();
            val content = SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            MessageDialog.show(this, content);
        }
        saveImageButton.setOnClickListener {
            saveToDisk()
        }

        mVisible = true
        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent.apply {
            setOnClickListener { toggle() }
            setOnTouchListener(mDelayHideTouchListener)
        }
    }

    private fun saveToDisk() {
        val bitmap = getViewSnapshot(fullscreenAppBar)
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss")
        val now = Date()
        val prefix = "PX_IMG_"+formatter.format(now)
        mPictureFile = Utils.saveFile(bitmap, Bitmap.CompressFormat.JPEG, 100, prefix + ".jpg");
        toast("Image Saved !")
    }

    private fun setupImage() {
        //        mPictureFile = File(intent.getStringExtra(SelfieActivity.PHOTO_EXTRA))
        val factory = BitmapFactory.Options()
        factory.inSampleSize = 3

        photoImageView.apply {
            setImageURI(Uri.fromFile(File(intent.getStringExtra(SelfieActivity.PHOTO_EXTRA))), this)
            val hierarchy = GenericDraweeHierarchyBuilder(getResources())
                    .setOverlay(Drawable.createFromPath(intent.getStringExtra(SelfieActivity.PHOTO_RENDERER_EXTRA)))
                    .build();
            setHierarchy(hierarchy);
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
        fun getViewSnapshot(view: ViewGroup): Bitmap {
            view.isDrawingCacheEnabled = true
            val bmap = view.drawingCache
            val snapshot = Bitmap.createBitmap(bmap, 0, 0, bmap.width, bmap.height, null, true)
            view.isDrawingCacheEnabled = false
            return snapshot
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
}
