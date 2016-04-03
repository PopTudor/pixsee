package com.marked.pixsee.face

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.vision.face.Face
import com.threed.jpct.*
import com.threed.jpct.util.MemoryHelper
import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Tudor Pop on 01-Apr-16.
 */

// The renderer class for the ImageTargetsBuilder sample.
class FaceRenderer(private val mActivity: AppCompatActivity) :
        GLSurfaceView.Renderer {
    var isActive = false
    var mFace: Face? = null
    //    var triangle: Triangle? = null
    /*
    * The model matrix. This matrix is used to place a model somewhere in the “world”. For example,
    * if you have a model of a car and you want it located 1000 meters to the east, you will use the model matrix to do this.
    * */
    private final var mModelMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    /*
    * The projection matrix. Since our screens are flat, we need to do a final transformation to “project” our
    * view onto our screen and get that nice 3D perspective. This is what the projection matrix is used for.
    * */
    private final val mProjectionMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    //    private final val mViewMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    //    private final val mMPVMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    //    private final var tmpMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);


    //    private final val mRotationMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    //    private final val mTranslationMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    val screenSize = Point()
    private val mTextureManager by lazy { TextureManager.getInstance() }
    private val mWorld by lazy {
        val tmp = World()
        tmp.setAmbientLight(25, 25, 25)
        tmp
    }
    private val mSun by lazy {
        val tmp = Light(mWorld)
        tmp.setIntensity(250f, 250f, 250f)
        tmp
    }
    private var fb: FrameBuffer?=null

    private var cam: Camera? = null
    private val fov = 0.0f
    private val fovy = 0.0f
    private val mFacePositionPaint: Paint
    private var loadOBJ: Object3D? = null


    init {

        mFacePositionPaint = Paint()
        mActivity.windowManager.defaultDisplay.getSize(screenSize)

        // keep the texture small(ideal under 1mb; if it's too big won't keep up with the program because the texture is loaded
        // on an background thread so it must be easy to load the texture) or compress any texture online
        // it's also recommended to be a power of 2. 2^x width/height
        var texture: Texture
        if (!mTextureManager.containsTexture("bourak")) {
            texture = Texture(mActivity.assets.open("bourak_3ds/bourak.jpg"))
            mTextureManager.addTexture("bourak", texture)
        }
        if (!mTextureManager.containsTexture("box")) {
            texture = Texture(64, 64, RGBColor.WHITE)
            mTextureManager.addTexture("box", texture)
        }

//        if (loadOBJ == null) {
            loadModel()
//        }
        MemoryHelper.compact()
    }
    var boxID = 0;
    fun loadModel() {
        try {
            val box = Primitives.getCube(10.0f)
            box.calcTextureWrapSpherical()
            box.setTexture("box")
            box.strip()
            box.build()
            boxID = box.id


            mWorld.addObject(box)

//            mWorld.getObject()
            cam = mWorld.camera
            cam?.moveCamera(Camera.CAMERA_MOVEOUT, 50f)
            cam?.lookAt(box.transformedCenter)

            val sv = SimpleVector()
            sv.set(box.transformedCenter)
            sv.y += 100f
            sv.z += 100f
            mSun.position = sv
        } catch (e: Exception) {
            Log.e("**", e.stackTrace.toString())
            loadOBJ = loadModel(mActivity.assets.open("bourak_3ds/bourak.3DS"), 0.008.toFloat())
            loadOBJ?.setTexture("bourak")
            loadOBJ?.build()

            mWorld.addObject(loadOBJ)
            cam = mWorld.camera
            cam?.moveCamera(Camera.CAMERA_MOVEOUT, 50f)
            cam?.lookAt(loadOBJ?.transformedCenter)

            val sv = SimpleVector()
            sv.set(loadOBJ?.transformedCenter)
            sv.y += 100f
            sv.z += 100f
            mSun.position = sv
        }

    }

    // Called when the surface is created or recreated.
    @SuppressLint("LongLogTag")
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        run { // state configuration
            gl.glClearColor(0f, 0f, 0f, 0f); /* screen black when cleared*/
            gl.glDisable(GL10.GL_DITHER);
            gl.glDisable(GL10.GL_CULL_FACE);
            gl.glEnable(GL10.GL_DEPTH_TEST);
        }

        //        triangle = Triangle()
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        //                GLES20.glFrontFace(GLES20.GL_CW)
        gl.glDepthFunc(GL10.GL_LEQUAL);
        //        gl.glTranslatef(0.0f, 0.0f, -5.0f);
    }

    // Called when the surface changed size.
    @SuppressLint("LongLogTag")
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height); /* This tells OpenGL the size of the surface it has available for rendering. */
        fb?.dispose()
        fb = FrameBuffer(width, height)


        run {
            // Create a new perspective projection matrix. The height will stay the same
            // while the width will vary as per aspect ratio.
            //            val ratio = width.toFloat() / height.toFloat();
            //            val left = -ratio.toFloat();
            //            val right = ratio.toFloat();
            //            val bottom = -1.0f;
            //            val top = 1.0f;
            //            val near = 40f;
            //            val far = 50f;
            //            Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        }
    }

    // Called to draw the current frame.
    override fun onDrawFrame(gl: GL10) {
        run { //clear
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT or  GL10.GL_DEPTH_BUFFER_BIT);// Clear the rendering surface.
            //            Matrix.setIdentityM(mModelMatrix, 0);
        }
        fb?.clear()


        // Calculate the projection and view transformation
        //        Matrix.multiplyMM(mModelMatrix, 0, mProjectionMatrix, 0, mCameraViewMatrix, 0);
        run { // setup CameraViewMatrix
            var point = mFace?.position ?: PointF(0f, 0f)
            var width = mFace?.width ?: 0f
            var height = mFace?.height ?: 0f

            // Position the eye behind the origin.
            val eyeX = 0.0f;
            val eyeY = 0.0f;
            val eyeZ = 1.0f;
            // We are looking toward the distance
            val lookX = point.x
            val lookY = point.y
            val lookZ = 0.0f;
            // Set our up vector. This is where our head would be pointing were we holding the camera.
            val upX = 0.0f;
            val upY = 1.0f;
            val upZ = 0.0f;
            // Set the view matrix. This matrix can be said to represent the camera position.
            // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
            // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
            //            Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        }

        run { // model transformation translation*rotation*scale
            var point = mFace?.position ?: PointF(0f, 0f)
            var width = mFace?.width ?: 0f
            var height = mFace?.height ?: 0f
            var objBox = mWorld.getObject(boxID)

//            objBox.translate(point.x / screenSize.x, point.y / screenSize.y, 1f)


            //            Matrix.translateM(mModelMatrix, 0, point.x / screenSize.x, point.y / screenSize.y, 0f)
            // Create a rotation transformation for the triangle
            //        val time = SystemClock.uptimeMillis() % 4000L;
            //        val mAngle = 0.090f * time.toInt();
            //        Matrix.setRotateM(mRotationMatrix, 0, 90f, 0f, 0f, 0.0f);
            //            Matrix.multiplyMM(mModelMatrix, 0, tmpMatrix, 0, mRotationMatrix, 0);

            //            Matrix.scaleM(mModelMatrix, 0, (width / screenSize.x) * 2, (height / screenSize.y) * 2, 0f)
        }


        //        tmpMatrix = mModelMatrix.copyOf()
        //        Matrix.multiplyMM(mvpMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        //        tmpMatrix = mvpMatrix.copyOf()
        //        Matrix.multiplyMM(mvpMatrix, 0, tmpMatrix, 0, mModelMatrix, 0);
        //
        //        triangle?.draw(mModelMatrix);
                renderFrame()
        //        updateCamera()
    }

    private fun renderFrame() {
        val angle = 90f
        val modelViewProjection = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
//        Matrix.translateM(mModelMatrix, 0, 0f, 0f, kObjectScale)
//        Matrix.rotateM(mModelMatrix, 0, angle, 0f, 0f, kObjectScale)
//        Matrix.scaleM(mModelMatrix, 0, kObjectScale, kObjectScale, kObjectScale)
//        Matrix.multiplyMM(modelViewProjection, 0, mProjectionMatrix, 0, mModelMatrix, 0)
        //            modelViewMatrix_Vuforia.data = modelViewMatrix

        //            val inverseMV = SampleMath.Matrix44FInverse(modelViewMatrix_Vuforia)
        //            val invTranspMV = SampleMath.Matrix44FTranspose(inverseMV)
        //            updateModelviewMatrix(invTranspMV.data)

        // hide the objects when the targets are not detected
        //            if (state.numTrackableResults == 0) {
        //                val m = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, -10000f, 1f)
        //                updateModelviewMatrix(m)
        //            }


        //        }

        mWorld.renderScene(fb)
        mWorld.draw(fb)
        fb?.display()
    }

    fun updateCamera() {
        val m = mModelMatrix
        val camUp: SimpleVector

        if (mActivity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            camUp = SimpleVector(-m[0], -m[1], -m[2])
        else
            camUp = SimpleVector(-m[4], -m[5], -m[6])

        val camDirection = SimpleVector(m[8], m[9], m[10])
        val camPosition = SimpleVector(m[12], m[13], m[14])

        cam!!.setOrientation(camDirection, camUp)
        cam!!.position = camPosition

        cam!!.fov = fov
        cam!!.yfov = fovy
    }

    fun updateModelviewMatrix(mat: FloatArray) {
        mModelMatrix = mat
    }

    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    fun updateFace(face: Face) {
        mFace = face
    }

    @Throws(FileNotFoundException::class)
    private fun loadModel(filename: InputStream, scale: Float): Object3D {
        val model = Loader.load3DS(BufferedInputStream(filename), scale)
        var o3d = Object3D(0)
        var temp: Object3D? = null
        for (i in model.indices) {
            temp = model[i]
            temp!!.center = SimpleVector.ORIGIN
            //            temp.rotateX((.4 * Math.PI).toFloat())
            //            temp.rotateY((.1 * Math.PI).toFloat())
            //            temp.rotateMesh()
            temp.rotationMatrix = com.threed.jpct.Matrix()
            o3d = Object3D.mergeObjects(o3d, temp)
            o3d.build()
        }
        return o3d
    }

    companion object {
        // Constants:
        internal val kObjectScale = 0.5f
        private val LOGTAG = "ImageTargetRenderer"
        fun loadShader(type: Int, shaderCode: String): Int {

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            val shader = GLES20.glCreateShader(type);

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            return shader;
        }

        fun checkGlError(glOperation: String) {
            var error = 0;
            while (error != GLES20.GL_NO_ERROR) {
                error = GLES20.glGetError()
                Log.e("TAG", glOperation + ": glError " + error);
                throw  RuntimeException(glOperation + ": glError " + error);
            }
        }
    }
}
