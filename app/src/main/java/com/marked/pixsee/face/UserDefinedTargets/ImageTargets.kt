/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.marked.pixsee.face.UserDefinedTargets

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.widget.RelativeLayout
import com.marked.pixsee.R
import com.marked.pixsee.face.VuforiaApplication.ApplicationException
import com.marked.pixsee.face.VuforiaApplication.utils.GLDrawingSurfaceView
import com.marked.pixsee.face.VuforiaApplication.utils.Texture
import com.qualcomm.vuforia.*
import kotlinx.android.synthetic.main.activity_preview.*
import java.util.*

// The main activity for the UserDefinedTargets sample.
class ImageTargets : AppCompatActivity(), com.marked.pixsee.face.VuforiaApplication.ApplicationControl, com.marked.pixsee.face.SampleAppMenu.SampleAppMenuInterface {
    var targetBuilderCounter = 1
    var dataSetUserDef: DataSet? = null
    var refFreeFrame: RefFreeFrame? = null
    var mIsDroidDevice = false

    private val vuforiaAppSession: com.marked.pixsee.face.VuforiaApplication.ApplicationSession by lazy { com.marked.pixsee.face.VuforiaApplication.ApplicationSession(this) }
    private val mDialog: AlertDialog by lazy { AlertDialog.Builder(this@ImageTargets).create() }
    private val mTextures: Vector<Texture> by lazy { Vector<Texture>() }

    // Our OpenGL view:
    private var mGlView: GLDrawingSurfaceView? = null
    // Our renderer:
    private var mRenderer: ImageTargetRenderer? = null
    // The textures we will use for rendering:
    // View overlays to be displayed in the Augmented View
    private var mUILayout: RelativeLayout? = null
    private lateinit var mBottomBar: View
    private var mCameraButton: View? = null
    // Alert dialog for displaying SDK errors
    private var mGestureDetector: GestureDetector? = null
    //	private SampleAppMenu             mSampleAppMenu;
    private var mSettingsAdditionalViews: ArrayList<View>? = null
    private var mExtendedTracking = true
    //    private LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
    // Alert Dialog used to display SDK errors
    private var mErrorDialog: AlertDialog? = null

    private var mCameraSwitch = CameraDevice.CAMERA.CAMERA_FRONT

    val isUserDefinedTargetsRunning: Boolean
        get() {
            val trackerManager = TrackerManager.getInstance()
            val objectTracker = trackerManager.getTracker(ObjectTracker.getClassType()) as ObjectTracker

            if (objectTracker != null) {
                val targetBuilder = objectTracker.imageTargetBuilder
                if (targetBuilder != null) {
                    Log.e(ImageTargets.Companion.LOGTAG, "Quality> " + targetBuilder.frameQuality)
                    return if (targetBuilder.frameQuality != ImageTargetBuilder.FRAME_QUALITY.FRAME_QUALITY_NONE)
                        true
                    else
                        false
                }
            }

            return false
        }

    companion object {
        val CMD_BACK = -1
        val CMD_EXTENDED_TRACKING = 1
        private val LOGTAG = "UserDefinedTargets"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_preview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        if (item.itemId == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    // Called when the activity first starts or needs to be recreated after
    // resuming the application or a configuration change.
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(ImageTargets.Companion.LOGTAG, "onCreate")
        super.onCreate(savedInstanceState)

        vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // Load any sample specific textures:
        mTextures.add(Texture.loadTextureFromApk("rock.jpg", assets))

        mGestureDetector = GestureDetector(this, GestureListener())
        mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith("droid")

    }


    // We want to load specific textures from the APK, which we will later use
    // for rendering.

    // Called when the activity will start interacting with the user.
    override fun onResume() {
        Log.d(ImageTargets.Companion.LOGTAG, "onResume")
        super.onResume()

        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        try {
            vuforiaAppSession.resumeAR()
        } catch (e: ApplicationException) {
            Log.e(ImageTargets.Companion.LOGTAG, e.string)
        }

        // Resume the GL view:
        if (mGlView != null) {
            mGlView!!.visibility = View.VISIBLE
            mGlView!!.onResume()
        }

    }

    // Called when the system is about to start resuming a previous activity.
    override fun onPause() {
        Log.d(ImageTargets.Companion.LOGTAG, "onPause")
        super.onPause()

        if (mGlView != null) {
            mGlView!!.visibility = View.INVISIBLE
            mGlView!!.onPause()
        }

        try {
            vuforiaAppSession.pauseAR()
        } catch (e: ApplicationException) {
            Log.e(ImageTargets.Companion.LOGTAG, e.string)
        }

    }

    // The final call you receive before your activity is destroyed.
    override fun onDestroy() {
        Log.d(ImageTargets.Companion.LOGTAG, "onDestroy")
        super.onDestroy()

        try {
            vuforiaAppSession.stopAR()
        } catch (e: ApplicationException) {
            Log.e(ImageTargets.Companion.LOGTAG, e.string)
        }

        // Unload texture:
        mTextures.clear();
        System.gc()
    }

    // Callback for configuration changes the activity handles itself
    override fun onConfigurationChanged(config: Configuration) {
        Log.d(ImageTargets.Companion.LOGTAG, "onConfigurationChanged")
        super.onConfigurationChanged(config)

        vuforiaAppSession.onConfigurationChanged()

        // Removes the current layout and inflates a proper layout
        // for the new screen orientation

        if (mUILayout != null) {
            mUILayout!!.removeAllViews()
            (mUILayout!!.parent as ViewGroup).removeView(mUILayout)

        }

        addOverlayView(false)
    }

    // Shows error message in a system dialog box
    private fun showErrorDialog() {
        if (mDialog.isShowing)
            mDialog.dismiss()

        val clickListener = DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }

        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.button_OK), clickListener)

        mDialog.setTitle(getString(R.string.target_quality_error_title))

        val message = getString(R.string.target_quality_error_desc)

        // Show dialog box with error message:
        mDialog.setMessage(message)
        mDialog.show()
    }

    // Shows error message in a system dialog box on the UI thread
    fun showErrorDialogInUIThread() {
        runOnUiThread { showErrorDialog() }
    }

    // Initializes AR application components.
    private fun initApplicationAR() {
        // TODO: 01-Oct-15 understand this
        // Do application initialization
        refFreeFrame = RefFreeFrame(this, vuforiaAppSession)
        refFreeFrame!!.init()

        // Create OpenGL ES view:
        val depthSize = 16
        val stencilSize = 0
        val translucent = Vuforia.requiresAlpha()

        mGlView = GLDrawingSurfaceView(this)
        mGlView!!.init(translucent, depthSize, stencilSize)

        mRenderer = ImageTargetRenderer(this, vuforiaAppSession)
        mGlView!!.setRenderer(mRenderer)
        addOverlayView(true)
    }

    // Adds the Overlay view to the GLView
    private fun addOverlayView(initLayout: Boolean) {
        // Inflates the Overlay Layout to be displayed above the Camera View
        val inflater = LayoutInflater.from(this)
        mUILayout = inflater.inflate(R.layout.activity_preview, null, false) as RelativeLayout

        mUILayout!!.visibility = View.VISIBLE
        // If this is the first time that the application runs then the
        // uiLayout background is set to BLACK color, will be set to
        // transparent once the SDK is initialized and camera ready to draw
        if (initLayout) {
            mUILayout!!.setBackgroundColor(Color.WHITE)
        }

        // Adds the inflated layout to the view
        addContentView(mUILayout, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        // Gets a reference to the bottom navigation bar
        mBottomBar = mUILayout!!.findViewById(R.id.bottom_bar)

        // Gets a reference to the Camera button
        mCameraButton = mUILayout!!.findViewById(R.id.camera_button)

        // Gets a reference to the loading dialog container
        //        loadingDialogHandler.mLoadingDialogContainer = mUILayout.findViewById(R.id.loading_layout);
        switchCamera.setOnClickListener { vuforiaAppSession.switchCamera() }
        startUserDefinedTargets()

        mBottomBar.visibility = View.VISIBLE
        mCameraButton!!.visibility = View.VISIBLE
        mUILayout!!.bringToFront()
    }
/* NOT USEFUL */
    fun startUserDefinedTargets(): Boolean {
        Log.d(ImageTargets.Companion.LOGTAG, "startUserDefinedTargets")
        val trackerManager = TrackerManager.getInstance()
        val objectTracker = trackerManager.getTracker(ObjectTracker.getClassType()) as ObjectTracker
        if (objectTracker != null) {
            val targetBuilder = objectTracker.imageTargetBuilder
            if (targetBuilder != null) {
                // if needed, stop the target builder
                if (targetBuilder.frameQuality != ImageTargetBuilder.FRAME_QUALITY.FRAME_QUALITY_NONE)
                    targetBuilder.stopScan()
                objectTracker.stop()
                targetBuilder.startScan()
            }
        } else
            return false
        return true
    }

    // Button Camera clicked
    fun onCameraClick(v: View) {
        if (isUserDefinedTargetsRunning) {
            // Shows the loading dialog
            //            loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);

            // Builds the new target
            startBuild()
        }
    }

    fun startBuild() {
        val trackerManager = TrackerManager.getInstance()
        val objectTracker = trackerManager.getTracker(ObjectTracker.getClassType()) as ObjectTracker

        if (objectTracker != null) {
            val targetBuilder = objectTracker.imageTargetBuilder
            if (targetBuilder != null) {
                // Uncomment this block to show and error message if
                // the frame quality is Low
                //if (targetBuilder.getFrameQuality() == ImageTargetBuilder.FRAME_QUALITY.FRAME_QUALITY_LOW)
                //{
                //     showErrorDialogInUIThread();
                //}

                var name: String
                do {
                    name = "UserTarget-" + targetBuilderCounter
                    Log.d(ImageTargets.Companion.LOGTAG, "TRYING " + name)
                    targetBuilderCounter++
                } while (!targetBuilder.build(name, 320.0f))

                refFreeFrame!!.setCreating()
            }
        }
    }

    // Creates a texture given the filename
    fun createTexture(nName: String): Texture {
        return Texture.loadTextureFromApk(nName, assets)
    }

    // Callback function called when the target creation finished
    fun targetCreated() {
        // Hides the loading dialog
        //        loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
//        if (refFreeFrame != null) {
//            refFreeFrame!!.reset()
//        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Process the Gestures
        //		if (mSampleAppMenu != null && mSampleAppMenu.processEvent(event))
        //			return true;

        return mGestureDetector!!.onTouchEvent(event)
    }

    fun updateRendering() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
//        refFreeFrame!!.initGL(metrics.widthPixels, metrics.heightPixels)
    }

    override fun doInitTrackers(): Boolean {
        // Indicate if the trackers were initialized correctly
        var result = true

        // Initialize the image tracker:
        val trackerManager = TrackerManager.getInstance()
        val tracker = trackerManager.initTracker(ObjectTracker.getClassType()) // init the tracker
        if (tracker == null) {
            Log.d(ImageTargets.Companion.LOGTAG, "Failed to initialize ObjectTracker.")
            result = false
        } else {
            Log.d(ImageTargets.Companion.LOGTAG, "Successfully initialized ObjectTracker.")
        }

        return result
    }

    override fun doLoadTrackersData(): Boolean {
        // Get the image tracker:
        val trackerManager = TrackerManager.getInstance()
        val objectTracker = trackerManager.getTracker(ObjectTracker.getClassType()) as ObjectTracker// give a
        // reference to initialized tracker
        if (objectTracker == null) {
            Log.d(ImageTargets.Companion.LOGTAG, "Failed to load tracking data set because the ObjectTracker has not been initialized.")
            return false
        }

        // Create the data set:
        dataSetUserDef = objectTracker.createDataSet()
        if (dataSetUserDef == null) {
            Log.d(ImageTargets.Companion.LOGTAG, "Failed to create a new tracking data.")
            return false
        }

        if (!objectTracker.activateDataSet(dataSetUserDef)) {
            Log.d(ImageTargets.Companion.LOGTAG, "Failed to activate data set.")
            return false
        }

        Log.d(ImageTargets.Companion.LOGTAG, "Successfully loaded and activated data set.")
        return true
    }

    override fun doStartTrackers(): Boolean {
        // Indicate if the trackers were started correctly
        val result = true

        val objectTracker = TrackerManager.getInstance().getTracker(ObjectTracker.getClassType())
        objectTracker?.start()

        return result
    }

    override fun doStopTrackers(): Boolean {
        // Indicate if the trackers were stopped correctly
        val result = true

        val objectTracker = TrackerManager.getInstance().getTracker(ObjectTracker.getClassType())
        objectTracker?.stop()

        return result
    }

    override fun doUnloadTrackersData(): Boolean {
        // Indicate if the trackers were unloaded correctly
        var result = true

        // Get the image tracker:
        val trackerManager = TrackerManager.getInstance()
        val objectTracker = trackerManager.getTracker(ObjectTracker.getClassType()) as ObjectTracker
        if (objectTracker == null) {
            result = false
            Log.d(ImageTargets.Companion.LOGTAG, "Failed to destroy the tracking data set because the ObjectTracker has not been initialized.")
        }
        if (dataSetUserDef != null) {
            if (objectTracker.activeDataSet != null && !objectTracker.deactivateDataSet(dataSetUserDef)) {
                Log.d(ImageTargets.Companion.LOGTAG, "Failed to destroy the tracking data set because the data set could not be deactivated.")
                result = false
            }

            if (!objectTracker.destroyDataSet(dataSetUserDef)) {
                Log.d(ImageTargets.Companion.LOGTAG, "Failed to destroy the tracking data set.")
                result = false
            }

            Log.d(ImageTargets.Companion.LOGTAG, "Successfully destroyed the data set.")
            dataSetUserDef = null
        }

        return result
    }

    override fun doDeinitTrackers(): Boolean {
        // Indicate if the trackers were deinitialized correctly
        val result = true

//        if (refFreeFrame != null)
//            refFreeFrame!!.deInit()

        val tManager = TrackerManager.getInstance()
        tManager.deinitTracker(ObjectTracker.getClassType())

        return result
    }

    override fun onInitARDone(exception: ApplicationException?) {
        if (exception == null) {
            initApplicationAR()

            // Activate the renderer
            mRenderer!!.mIsActive = true

            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

            // Sets the UILayout to be drawn in front of the camera
            mUILayout!!.bringToFront()

            // Hides the Loading Dialog
            //            loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

            // Sets the layout background to transparent
            mUILayout!!.setBackgroundColor(Color.TRANSPARENT)

            try {
                vuforiaAppSession.startAR(mCameraSwitch)
            } catch (e: ApplicationException) {
                Log.e(ImageTargets.Companion.LOGTAG, e.string)
            }

            val result = CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO)

            if (!result)
                Log.e(ImageTargets.Companion.LOGTAG, "Unable to enable continuous autofocus")

            setSampleAppMenuAdditionalViews()
            //			mSampleAppMenu = new SampleAppMenu(this, this, "User Defined Targets", mGlView, mUILayout,
            //											   mSettingsAdditionalViews);
            setSampleAppMenuSettings()

        } else {
            Log.e(ImageTargets.Companion.LOGTAG, exception.string)
            showInitializationErrorMessage(exception.string)
        }
    }

    // Shows initialization error messages as System dialogs
    fun showInitializationErrorMessage(message: String) {
        val errorMessage = message
        runOnUiThread {
            if (mErrorDialog != null) {
                mErrorDialog!!.dismiss()
            }

            // Generates an Alert Dialog to show the error message
            val builder = AlertDialog.Builder(this@ImageTargets)
            builder.setMessage(errorMessage).setTitle(getString(R.string.INIT_ERROR)).setCancelable(false).setIcon(0).setPositiveButton(getString(R.string.button_OK)) { dialog, id -> finish() }

            mErrorDialog = builder.create()
            mErrorDialog!!.show()
        }
    }

    override fun onQCARUpdate(state: State) {
        val trackerManager = TrackerManager.getInstance()
        val objectTracker = trackerManager.getTracker(ObjectTracker.getClassType()) as ObjectTracker

        if (refFreeFrame!!.hasNewTrackableSource()) {
            Log.d(ImageTargets.Companion.LOGTAG, "Attempting to transfer the trackable source to the dataset")

            // Deactivate current dataset
            objectTracker.deactivateDataSet(objectTracker.activeDataSet)

            // Clear the oldest target if the dataset is full or the dataset
            // already contains five user-defined targets.
            if (dataSetUserDef!!.hasReachedTrackableLimit() || dataSetUserDef!!.numTrackables >= 5)
                dataSetUserDef!!.destroy(dataSetUserDef!!.getTrackable(0))

            if (mExtendedTracking && dataSetUserDef!!.numTrackables > 0) {
                // We need to stop the extended tracking for the previous target
                // so we can enable it for the new one
                val previousCreatedTrackableIndex = dataSetUserDef!!.numTrackables - 1

                objectTracker.resetExtendedTracking()
                dataSetUserDef!!.getTrackable(previousCreatedTrackableIndex).stopExtendedTracking()
            }

            // Add new trackable source.
            /* HERE *** WITHOUT THIS IT DOES NOT RENDER ANYTHING*/
            val trackable = dataSetUserDef!!.createTrackable(refFreeFrame!!.newTrackableSource)

            // Reactivate current dataset
            objectTracker.activateDataSet(dataSetUserDef)

            if (mExtendedTracking) {
//                trackable.startExtendedTracking()
            }

        }
    }

    // This method sets the additional views to be moved along with the GLView
    private fun setSampleAppMenuAdditionalViews() {
        mSettingsAdditionalViews = ArrayList<View>()
        mSettingsAdditionalViews!!.add(mBottomBar)
    }

    // This method sets the menu's settings
    private fun setSampleAppMenuSettings() {
        //		SampleAppMenuGroup group;

        //		group = mSampleAppMenu.addGroup("", false);
        //		group.addTextItem(getString(R.string.menu_back), -1);

        //		group = mSampleAppMenu.addGroup("", true);
        //		group.addSelectionItem(getString(R.string.menu_extended_tracking), CMD_EXTENDED_TRACKING, false);

        //		mSampleAppMenu.attachMenu();
    }

    override fun menuProcess(command: Int): Boolean {
        var result = true

        when (command) {
            ImageTargets.Companion.CMD_BACK -> finish()

            ImageTargets.Companion.CMD_EXTENDED_TRACKING -> {
                if (dataSetUserDef!!.numTrackables > 0) {
                    val lastTrackableCreatedIndex = dataSetUserDef!!.numTrackables - 1

                    val trackable = dataSetUserDef!!.getTrackable(lastTrackableCreatedIndex)

                    if (!mExtendedTracking) {
                        if (!trackable.startExtendedTracking()) {
                            Log.e(ImageTargets.Companion.LOGTAG, "Failed to start extended tracking target")
                            result = false
                        } else {
                            Log.d(ImageTargets.Companion.LOGTAG, "Successfully started extended tracking target")
                        }
                    } else {
                        if (!trackable.stopExtendedTracking()) {
                            Log.e(ImageTargets.Companion.LOGTAG, "Failed to stop extended tracking target")
                            result = false
                        } else {
                            Log.d(ImageTargets.Companion.LOGTAG, "Successfully stopped extended tracking target")
                        }
                    }
                }

                if (result)
                    mExtendedTracking = !mExtendedTracking
            }
        }

        return result
    }

    // Process Single Tap event to trigger autofocus
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        // Used to set autofocus one second after a manual focus is triggered
        private val autofocusHandler = Handler()


        override fun onDown(e: MotionEvent): Boolean {
            return true
        }


        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // Generates a Handler to trigger autofocus
            // after 1 second
            autofocusHandler.postDelayed({
                val result = CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO)

                if (!result)
                    Log.e("SingleTapUp", "Unable to trigger focus")
            }, 1000L)

            return true
        }
    }


}
