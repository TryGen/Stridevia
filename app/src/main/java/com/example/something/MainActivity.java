package com.example.something;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor gyroscope;
    private SensorEventListener gyroListener;
    private Quaternion orientation;

    float x = 0 , y = 0, z = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        orientation = new Quaternion();

        /**
         * In case the device doesn't have a gyroscope.
         */
        if(gyroscope == null)
        {
            Toast.makeText(this, "This device doesn't have a gyroscope", Toast.LENGTH_SHORT).show();
            finish();
        }

       // displayCoords(x,y,z);

        gyroListener = new SensorEventListener() {

            /**
             * @param sensorEvent
             * sensorEvent.value[i] represents rotation in the i axis.
             */

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                /* if(sensorEvent.values[1] > 0.5f)
                     finish();*/
                orientation.ToQuaternion(
                        sensorEvent.values[0],
                        sensorEvent.values[1],
                        sensorEvent.values[2]);

                x = sensorEvent.values[0];
                y = sensorEvent.values[1];
                z = sensorEvent.values[2];
               // displayCoords(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]);
                System.out.println(x);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

    }

    public void displayCoords(float x, float y, float z)
    {
        String coords = "x: " + x + ", y: " + y + ", z: " + z;
        Toast.makeText(this, coords, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroListener,gyroscope,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroListener);
    }
}