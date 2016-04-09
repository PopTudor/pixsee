package com.marked.pixsee.face;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;

import com.getwandup.rxsensor.RxSensor;
import com.getwandup.rxsensor.domain.RxSensorEvent;
import com.google.android.gms.vision.face.Face;
import com.marked.pixsee.R;

import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Tudor on 4/8/2016.
 */
public class FaceRenderer extends org.rajawali3d.renderer.RajawaliRenderer {
    private static final String TAG = "***********";
    private Context context;
    private DirectionalLight directionalLight;
    private Sphere earthSphere;
    private Face mFace;
    double viewportWidth;
    double viewportHeight;

    double xAcc, yAcc;
    Subscription subscription;

    public FaceRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(30);
    }


    public void setMFace(Face mFace) {
        this.mFace = mFace;
    }

    @Override
    protected void initScene() {
        viewportWidth = getViewportWidth();
        viewportHeight = getViewportHeight();
        RxSensor rxSensor = new RxSensor(mContext);
        subscription = rxSensor.observe(Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL)
                .subscribe(new Subscriber<RxSensorEvent>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(RxSensorEvent sensorEvent) {
                        xAcc = sensorEvent.getValues()[0];
                        yAcc = sensorEvent.getValues()[1];
                    }
                });

        directionalLight = new DirectionalLight(1f, .2f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);
        getCurrentScene().addLight(directionalLight);

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setColor(0);

        Texture earthTexture = new Texture("Earth", R.drawable.earth);
        try {
            material.addTexture(earthTexture);

        } catch (ATexture.TextureException error) {
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        earthSphere = new Sphere(0.5f, 24, 24);
        earthSphere.setMaterial(material);
        getCurrentScene().addChild(earthSphere);
        getCurrentCamera().setZ(5f);
        getCurrentCamera().setLookAt(0, 0, 0);
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);
        earthSphere.rotate(Vector3.Axis.Y, 1.0);
        if (mFace != null) {
            PointF point = mFace.getPosition();
            double widthNormalized = mFace.getWidth() / viewportWidth;
            double heightNormalized = mFace.getHeight() / viewportHeight;
            double xNormalized = point.x / viewportWidth;
            double yNormalized = point.y / viewportHeight;

            double x = widthNormalized - xNormalized * xAcc;
            double y = heightNormalized - yNormalized * yAcc;

            earthSphere.setPosition(x, y, 0);
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }
}
