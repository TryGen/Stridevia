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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Mihnwq
 *
 * Class for the data intake of the phone's internal gyroscope.
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
            latestGyroValues[0] = event.values[0];
            latestGyroValues[1] = event.values[1];
            latestGyroValues[2] = event.values[2];
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
