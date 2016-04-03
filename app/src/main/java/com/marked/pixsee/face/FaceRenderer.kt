package com.marked.pixsee.face

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.vision.face.Face
import com.threed.jpct.*
import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Tudor Pop on 01-Apr-16.
 */

// The renderer class for the ImageTargetsBuilder sample.
class FaceRenderer(private val mActivity: AppCompatActivity, graphicOverlay: GraphicOverlay) :
        GraphicOverlay.Graphic(graphicOverlay), GLSurfaceView.Renderer {
    var isActive = false
    var mFace: Face? = null
    var triangle: Triangle? = null
    /* mModelMatrix is an abbreviation for "Model View Projection Matrix"
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
    private final val mCameraViewMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private final val mRotationMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private final val mTranslationMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    private val FACE_POSITION_RADIUS = 10.0f
    private val ID_TEXT_SIZE = 40.0f
    private val ID_Y_OFFSET = 50.0f
    private val ID_X_OFFSET = -50.0f
    private val mFaceHappiness: Float = 0.toFloat()
    val pointScreen = Point()
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
    private val fb: FrameBuffer by lazy { FrameBuffer(320, 240) }

    private var cam: Camera? = null
    private val fov = 0.0f
    private val fovy = 0.0f
    private val mFacePositionPaint: Paint

    init {

        mFacePositionPaint = Paint()
        mActivity.windowManager.defaultDisplay.getSize(pointScreen)

        //        try {
        //            // keep the texture small(ideal under 1mb; if it's too big won't keep up with the program because the texture is loaded
        //            // on an background thread so it must be easy to load the texture) or compress any texture online
        //            // it's also recommended to be a power of 2. 2^x width/height
        //            var texture: Texture
        //            if (!mTextureManager.containsTexture("bourak")) {
        //                texture = Texture(mActivity.assets.open("bourak_3ds/bourak.jpg"))
        //                mTextureManager.addTexture("bourak", texture)
        //            }
        //            if (!mTextureManager.containsTexture("box")) {
        //                texture = Texture(64, 64, RGBColor.WHITE)
        //                mTextureManager.addTexture("box", texture)
        //            }
        //        } catch (e: IOException) {
        //            e.printStackTrace()
        //        }
        //
        //        val loadOBJ: Object3D
        //
        //        try {
        //            loadOBJ = loadModel(mActivity.assets.open("bourak_3ds/bourak.3DS"), 0.008.toFloat())
        //            loadOBJ.setTexture("bourak")
        //            loadOBJ.build()
        //
        //            mWorld.addObject(loadOBJ)
        //            cam = mWorld.camera
        //            cam!!.moveCamera(Camera.CAMERA_MOVEOUT, 50f)
        //            cam!!.lookAt(loadOBJ.transformedCenter)
        //
        //            val sv = SimpleVector()
        //            sv.set(loadOBJ.transformedCenter)
        //            sv.y += 100f
        //            sv.z += 100f
        //            mSun.position = sv
        //
        //        } catch (e: Exception) {
        //            Log.e("**", e.stackTrace.toString())
        //
        //            val box = Primitives.getCube(10.0f)
        //            box.calcTextureWrapSpherical()
        //            box.setTexture("box")
        //            box.strip()
        //            box.build()
        //
        //            mWorld.addObject(box)
        //            cam = mWorld.camera
        //            cam!!.moveCamera(Camera.CAMERA_MOVEOUT, 50f)
        //            cam!!.lookAt(box.transformedCenter)
        //
        //            val sv = SimpleVector()
        //            sv.set(box.transformedCenter)
        //            sv.y += 100f
        //            sv.z += 100f
        //            mSun.position = sv
        //        }
        //
        //        MemoryHelper.compact()
    }

    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    fun updateFace(face: Face) {
        mFace = face
        postInvalidate()
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
        run { // setup CameraViewMatrix
            // Position the eye behind the origin.
            val eyeX = 0.0f;
            val eyeY = 0.0f;
            val eyeZ = 1.0f;
            // We are looking toward the distance
            val lookX = 0.0f;
            val lookY = 0.0f;
            val lookZ = -5.0f;
            // Set our up vector. This is where our head would be pointing were we holding the camera.
            val upX = 0.0f;
            val upY = 1.0f;
            val upZ = 0.0f;
            // Set the view matrix. This matrix can be said to represent the camera position.
            // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
            // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
            Matrix.setLookAtM(mCameraViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        }
        triangle = Triangle()
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        //        GLES20.glFrontFace(GLES20.GL_CW)
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
    }

    // Called when the surface changed size.
    @SuppressLint("LongLogTag")
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height); /* This tells OpenGL the size of the surface it has available for rendering. */
        val ratio = width.toFloat() / height.toFloat();
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f);
    }

    private val mAngle: Float = 0f

    // Called to draw the current frame.
    override fun onDrawFrame(gl: GL10) {
        val scratch = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
        run { //clear
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT or  GL10.GL_DEPTH_BUFFER_BIT);// Clear the rendering surface.
        }

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mModelMatrix, 0, mProjectionMatrix, 0, mCameraViewMatrix, 0);

        var point = mFace?.position
        //        var width = mFace?.width
        //        var height = mFace?.height
        if (point === null) {
            point = PointF(0f, 0f)
        }
        Matrix.translateM(mTranslationMatrix, 0, point.x / pointScreen.x, point.y / pointScreen.y, 0f)
        //        if(height === null || width === null){
        //            height=0f
        //            width = 0f
        //        }
        //        val x = translateX(point.x + width / 2)
        //        val y = translateY(point.y + height / 2)
        //        val xOffset = scaleX(width / 2.0f)
        //        val yOffset = scaleY(height / 2.0f)
        //        val left = x - xOffset
        //        val top = y - yOffset
        //        val right = x + xOffset
        //        val bottom = y + yOffset

        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0f, 0f, 1.0f);

        Matrix.multiplyMM(scratch, 0, mModelMatrix, 0, mTranslationMatrix, 0);

        // Draw triangle
        triangle?.draw(scratch);
        //        renderFrame()
        //        updateCamera()
    }

    override fun draw(canvas: Canvas?) {
        //        val face = mFace ?: return
        // Draws a circle at the position of the detected face, with the face's track id below.
        //        val x = translateX(face.getPosition().x + face.getWidth() / 2)
        //        val y = translateY(face.getPosition().y + face.getHeight() / 2)
        //        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint)
        //        canvas.drawText("id: " , x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint)
        //        canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint)
        //        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint)
        //        canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET * 2, y - ID_Y_OFFSET * 2, mIdPaint)

        // Draws a bounding box around the face.
        //        val xOffset = scaleX(face.getWidth() / 2.0f)
        //        val yOffset = scaleY(face.getHeight() / 2.0f)
        //        val left = x - xOffset
        //        val top = y - yOffset
        //        val right = x + xOffset
        //        val bottom = y + yOffset
        //        canvas.drawRect(left, top, right, bottom, mBoxPaint)
        //        throw UnsupportedOperationException()
    }

    fun drawWorld() {
        try {
            mWorld.renderScene(fb)
            mWorld.draw(fb)
            fb.display()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun renderFrame() {
        // Clear color and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        //        val vuforiaMatrix = floatArrayOf(0.0f, 1.69032f, 0.0f, 0.0f, -3.0050135f, 0.0f, 0.0f, 0.0f, 0.0f, -0.0015625f, 1.004008f, 1.0f, 0.0f, 0.0f, -20.040081f, 0.0f)
        //        val modelViewMatrix = floatArrayOf(0.9999714f, 0.007403262f, 0.0015381078f, 0.0f, 0.0074077616f, -0.9999683f, -0.002941356f, 0.0f, 0.0015162831f, 0.002952666f, -0.9999945f, 0.0f, 2.1681705f, 4.7709565f, 264.3288f, 1.0f)
        val angle = 90f
        val modelViewProjection = FloatArray(16)
        Matrix.translateM(mModelMatrix, 0, 0f, 0f, kObjectScale)
        Matrix.rotateM(mModelMatrix, 0, angle, 0f, 0f, kObjectScale)
        Matrix.scaleM(mModelMatrix, 0, kObjectScale, kObjectScale, kObjectScale)
        Matrix.multiplyMM(modelViewProjection, 0, mProjectionMatrix, 0, mModelMatrix, 0)
        //            modelViewMatrix_Vuforia.data = modelViewMatrix

        //            val inverseMV = SampleMath.Matrix44FInverse(modelViewMatrix_Vuforia)
        //            val invTranspMV = SampleMath.Matrix44FTranspose(inverseMV)
        //            updateModelviewMatrix(invTranspMV.data)

        // hide the objects when the targets are not detected
        //            if (state.numTrackableResults == 0) {
        //                val m = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, -10000f, 1f)
        //                updateModelviewMatrix(m)
        //            }

        drawWorld()
        //        }

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
