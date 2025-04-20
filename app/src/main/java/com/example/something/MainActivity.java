package com.example.something;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Mihnwq
 *
 * Class for the data intake of the phone's internal gyroscope.
 * version 0.2
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroSensor;

    private float[] latestGyroValues = new float[3];

    private float[] totalRotation = new float[3];
    private long lastTimestamp = 0;

    /**
     * handler and gyroLogger both used to manipulate the main thread.
     */
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable gyroLogger;

    /**
    *We specify the delay before the next data intake.
     */
    private int seconds = 1;

    private int delay = seconds * 1000;

    /**
     * @param gyroOrientation
     * Stores the gyro rotation in Quaternions.
     */
    Quaternion gyroOrientation = new Quaternion();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        /**
         * If the system has gyroscope , we initialize it.
         */
        if (gyroSensor != null) {
            sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Gyroscope not available", Toast.LENGTH_SHORT).show();
        }

       /**
       *In run() we manipulate the main thread to get the gyro data after n seconds.
        */
        gyroLogger = new Runnable() {
            @Override
            public void run() {

               gyroOrientation.ToQuaternion(
                       totalRotation[0],
                       totalRotation[1],
                       totalRotation[2]
               );

                Log.d("GyroData",
                        "x: " + Math.toDegrees(totalRotation[0]) + " "
                        + "y: " + Math.toDegrees(totalRotation[1]) + " "
                        + "z: " + Math.toDegrees(totalRotation[2]));

               // Log.d("Phone angle:", String.valueOf(Math.toDegrees(gyroOrientation.toAngle())));


                handler.postDelayed(this, delay);
            }
        };
        handler.post(gyroLogger);
    }



    /**
     * @param event
     * event[i]  respresents the rotation in the i axis.
     * each time the phone move we get the Euler angle of the gyro stored in latestGyroValues.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            getNormalizedData(event.values[0],event.values[1],event.values[2]);

            /**
             * Deoarece giroscopul returneaza decat pe axa i, viteza rad/s
             * Va trebuii sa extragem unghiul efectiv prin a elimina variabila de ,,timp"
             */

            /* if (lastTimestamp != 0) {
                float dt = (event.timestamp - lastTimestamp) * 1.0f / 1_000_000_000.0f; // secunde
                for (int i = 0; i < 3; i++) {
                   // totalRotation[i] += event.values[i] * dt;
                    totalRotation[i] += latestGyroValues[i] * dt;
                }
            }
            lastTimestamp = event.timestamp;
           */

            getTotalRotation(event);



        }
    }

    private void getTotalRotation(SensorEvent event)
    {
        if (lastTimestamp != 0) {
            float dt = (event.timestamp - lastTimestamp) * 1.0f / 1_000_000_000.0f;
            for (int i = 0; i < 3; i++) {
                // totalRotation[i] += event.values[i] * dt;
                totalRotation[i] += latestGyroValues[i] * dt;
                totalRotation[i] = normalizeAngle(totalRotation[i]);
            }
        }
        lastTimestamp = event.timestamp;
    }

    private float normalizeAngle(float angle)
    {
        float r = (float) (2 * Math.PI);

        if(angle >= r)
            return 0;
        else if (angle < 0)
            return r + angle;

        return angle;
    }

    /**
     * !EXPERIMENTAL FOR THE TIME BEING!
     * Phone Orientation and Gyroscope Axes
     *
     * Gyroscope measures angular velocity (rotation) around the device's axes.
     *
     * Legend:
     *  - X: Rotation around the left-right axis (pitch)
     *  - Y: Rotation around the top-bottom axis (roll)
     *  - Z: Rotation around the front-back axis (yaw)
     *
     * -----------------------------
     *
     *     Portrait Mode (upright)
     *
     *        +Y
     *         ↑
     *         |
     *   -X ← [📱] → +X       ← Phone held upright
     *         |
     *         ↓
     *        -Y
     *
     *       Z-axis points *out* of the screen (toward user)
     *         (toward your face)
     *
     *       Clockwise twist = -Z
     *
     * -----------------------------
     *
     *     Landscape Left (home button/right side down)
     *
     *       +Y
     *        ↑
     *        |
     *   -X [📱] +X          ← Phone turned left
     *        |
     *        ↓
     *       -Y
     *
     *     X and Y axes are swapped compared to portrait.
     *     Z still points out of screen (toward user)
     *
     * -----------------------------
     *
     *     Landscape Right (home button/left side down)
     *
     *       -Y
     *        ↑
     *        |
     *   +X [📱] -X          ← Phone turned right
     *        |
     *        ↓
     *       +Y
     *
     *     X and Y axes are swapped again.
     *     Z remains the same (out of screen)
     *
     * -----------------------------
     *
     *     Face Down (screen on table)
     *
     *         ↑
     *         |
     *        +Z     ← Z now points into the table
     *         |
     *       [📱]     ← You're seeing the back of the phone
     *         |
     *        -Z
     *
     *     X and Y are still based on screen orientation
     *
     * -----------------------------
     *
     * Summary:
     * - Gyroscope axes are fixed to the device body.
     * - You must remap axes in software depending on how the screen is rotated.
     * - We also use the built in window_service to get the phone orientation.
     */
    private void getNormalizedData(float rawX , float rawY , float rawZ)
    {
        //Requires auto-rotate enabled on the phone to work.
        int rotation = ((WindowManager) getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();


        /**
         * I will change this ugly a$$ switch case later.
         */
        switch (rotation) {
            case Surface.ROTATION_0:
                setRotations(rawX,rawY,rawZ);
                break;

            case Surface.ROTATION_90:
                setRotations(-rawY,rawX,rawZ);
                break;

            case Surface.ROTATION_270:
                setRotations(rawY,-rawX,rawZ);
                break;

            case Surface.ROTATION_180:
                setRotations(-rawX,-rawY,rawZ);
                break;
        }
    }

    private void setRotations(float x, float y, float z)
    {
        latestGyroValues[0] = x;
        latestGyroValues[1] = y;
        latestGyroValues[2] = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(gyroLogger);
    }
}
