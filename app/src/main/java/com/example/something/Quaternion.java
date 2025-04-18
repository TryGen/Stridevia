package com.example.something;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * This is a sample class for quaternions.
 *
 * @author Mihnwq
 * @version 0.1
 */

public class Quaternion {

    float x , y , z , w;

    public Quaternion()
    {
        ResetAngles();
    }

    public void ResetAngles()
    {
        x = y = z = w = 0;
    }

    /**
     * @param roll
     * @param pitch
     * @param yaw
     * (roll, pitch ,yaw) represents the Euler angles.
     * We assume these values are in RADIANS.
     */
    public void ToQuaternion(double roll, double pitch, double yaw)
    {

        double cr = cos(roll * 0.5);
        double sr = sin(roll * 0.5);

        double cp = cos(pitch * 0.5);
        double sp = sin(pitch * 0.5);

        double cy = cos(yaw * 0.5);
        double sy = sin(yaw * 0.5);

        w = (float)(cr * cp * cy + sr * sp * sy);
        x = (float)(sr * cp * cy - cr * sp * sy);
        y = (float)(cr * sp * cy + sr * cp * sy);
        z = (float)(cr * cp * sy - sr * sp * cy);
    }


}