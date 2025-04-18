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

    double x , y , z , w;

    public Quaternion()
    {
        x = y = z = w = 0;
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

        w = cr * cp * cy + sr * sp * sy;
        x = sr * cp * cy - cr * sp * sy;
        y = cr * sp * cy + sr * cp * sy;
        z = cr * cp * sy - sr * sp * cy;
    }


}