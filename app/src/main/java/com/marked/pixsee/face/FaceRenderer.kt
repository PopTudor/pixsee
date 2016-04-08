package com.marked.pixsee.face

import android.content.res.Configuration
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
class FaceRenderer(private val mActivity: AppCompatActivity) : GLSurfaceView.Renderer {
    var mFace: Face? = null

    /*
    * The model matrix. This matrix is used to place a model somewhere in the “world”. For example,
    * if you have a model of a car and you want it located 1000 meters to the east, you will use the model matrix to do this.
    * */
    private final var mModelMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    val screenSize = Point()
    var facePosition = PointF()
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
    private var fb: FrameBuffer? = null

    private val mCam: Camera
    private val fov = 0.0f
    private val fovy = 0.0f
    private var loadOBJ: Object3D? = null
    private val box:Object3D = Primitives.getCube(5f)
    private val plane:Object3D = Primitives.getPlane(20, 10f);


    init {
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
        mCam = mWorld.camera
        plane.setAdditionalColor(RGBColor.GREEN);
        plane.rotateX( Math.PI.toFloat() / 2f);
        plane.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);

        loadModel()
        mWorld.addObject(plane)
        mCam.lookAt(plane.getTransformedCenter());
        MemoryHelper.compact()
    }

    fun loadModel() {
        try {
            box.calcTextureWrapSpherical()
            box.setTexture("box")
            box.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            box.setBillboarding(true) /*A billboarded object will ingore its own rotation matrix and will always face the camera.*/
            box.strip()
            box.build()


            mWorld.addObject(box)

            //            mWorld.getObject()

            mCam.moveCamera(Camera.CAMERA_MOVEOUT, 50f)
//            mCam.lookAt(box.transformedCenter)

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

            mCam.moveCamera(Camera.CAMERA_MOVEOUT, 50f)
            mCam.lookAt(loadOBJ?.transformedCenter)

            val sv = SimpleVector()
            sv.set(loadOBJ?.transformedCenter)
            sv.y += 100f
            sv.z += 100f
            mSun.position = sv
        }

    }

    // Called when the surface is created or recreated.
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        run { // state configuration
//            gl.glClearColor(0f, 0f, 0f, 0f); /* screen black when cleared*/
//            gl.glDisable(GL10.GL_DITHER);
//            gl.glDisable(GL10.GL_CULL_FACE);
//            gl.glEnable(GL10.GL_DEPTH_TEST);
        }

//        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
//        gl.glShadeModel(GL10.GL_SMOOTH);
//        gl.glDepthFunc(GL10.GL_LEQUAL);
        //        gl.glTranslatef(0.0f, 0.0f, -5.0f);
    }

    // Called when the surface changed size.
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height); /* This tells OpenGL the size of the surface it has available for rendering. */
        fb?.dispose()
        fb = FrameBuffer(width, height)
    }

    // Called to draw the current frame.
    override fun onDrawFrame(gl: GL10) {
        run { //clear
//            gl.glClear(GL10.GL_COLOR_BUFFER_BIT or  GL10.GL_DEPTH_BUFFER_BIT);// Clear the rendering surface.
            //            Matrix.setIdentityM(mModelMatrix, 0);
        }

        // model transformation translation*rotation*scale
//        var point = mFace?.position ?: PointF(0f, 0f)
//        var width = mFace?.width ?: 0f
//        var height = mFace?.height ?: 0f
//        var objBox = mWorld.getObject(boxID)
//        val centerX = width / screenSize.x
//        val centerY = height / screenSize.y

        //            objBox.setOrigin(SimpleVector(point.x / screenSize.x, point.y / screenSize.y, 0f));

//        val dir = Interact2D.reproject2D3DWS(cam, fb, point.x.toInt(), point.y.toInt()).normalize()
//        val a = (0f.minus(cam.position?.z ?: 0f)) / dir.z;
//        val xn = cam.position?.x ?: 0.plus(a * dir.x);
//        val yn = cam.position?.y ?: 0 + a * dir.y;

        //        run {
        //            objBox.clearTranslation()
        //            objBox.translate(SimpleVector(dir.x, dir.y, 0f));
        //        }


        //            cam?.lookAt(SimpleVector(point.x / screenSize.x, point.y / screenSize.y, 0f))

        follow()
        fb?.clear()
        renderFrame()

        //        updateCamera()
    }


    private fun follow() {
        var point = mFace?.position ?: PointF(0f, 0f)

        var ray = Interact2D.reproject2D3D(mCam, fb, point.x.toInt(), point.y.toInt());
        if (ray != null) {
            var norm = ray.normalize(); // Just to be sure...
//            norm.calcAngle()
            val mat = mCam.back.invert3x3()
            norm.matMul(mat)

            val f = mWorld.calcMinDistance(mCam.position, norm, 1000f);
//            if (f != Object3D.COLLISION_NONE) {
                val  offset = SimpleVector(norm);
                norm.scalarMul(f);
                norm = norm.calcSub(offset);
                box.translationMatrix.setIdentity()
                box.translate(norm)
                box.translate(mCam.position)
//            }
        }
    }

    private fun renderFrame() {
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

        mCam.setOrientation(camDirection, camUp)
        mCam.position = camPosition

        mCam.fov = fov
        mCam.yfov = fovy
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
