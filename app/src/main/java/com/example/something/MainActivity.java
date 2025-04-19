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
                    latestGyroValues[0],
                    latestGyroValues[1],
                    latestGyroValues[2]
               );

                Log.d("GyroData",
                        "x: " + gyroOrientation.x + " "
                        + "y: " + gyroOrientation.y + " "
                        + "z: " + gyroOrientation.z + " "
                        + "w: " + gyroOrientation.w);


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
          /*  latestGyroValues[0] = event.values[0];
            latestGyroValues[1] = event.values[1];
            latestGyroValues[2] = event.values[2];*/
            getNormalizedData(event.values[0],event.values[1],event.values[2]);
        }
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
        int rotation = ((WindowManager) getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                latestGyroValues[0] = rawX;
                latestGyroValues[1] = rawY;
                latestGyroValues[2] = rawZ;
                break;

            case Surface.ROTATION_90:
                latestGyroValues[0] = -rawY;
                latestGyroValues[1] = rawX;
                latestGyroValues[2] = rawZ;
                break;

            case Surface.ROTATION_270:
                latestGyroValues[0] = rawY;
                latestGyroValues[1] = -rawX;
                latestGyroValues[2] = rawZ;
                break;

            case Surface.ROTATION_180:
                latestGyroValues[0] = -rawX;
                latestGyroValues[1] = -rawY;
                latestGyroValues[2] = rawZ;
                break;

            default:
                latestGyroValues[0] = rawX;
                latestGyroValues[1] = rawY;
                latestGyroValues[2] = rawZ;
        }
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
