package com.example.something;

import static java.lang.Math.acos;
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

    public static final Quaternion zero = new Quaternion(0,0,0,0);

    public Quaternion normalized = Quaternion.zero;

    public Quaternion(){}


    public Quaternion(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;

        getNormalized(this);
    }


    public static void getNormalized(Quaternion q)
    {
        float norm = q.getNormOf(q);

        q.normalized = q;

        q.normalized.x /= norm;
        q.normalized.y /= norm;
        q.normalized.z /= norm;
        q.normalized.w /= norm;
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

        getNormalized(this);
    }

    private Quaternion getConjugateOf(Quaternion q)
    {
        return new Quaternion(-q.x,-q.y,-q.z,q.w);
    }

    private float getNormOf(Quaternion q){return (float) Math.sqrt((q.x * q.x) + (q.y * q.y) + (q.z * q.z) + (q.w * q.w));}

    private float getSqrNormOf(Quaternion q){float norm = getNormOf(q); return norm * norm;}

    public static Quaternion getInvOf(Quaternion q)
    {
        Quaternion qConj = q.getConjugateOf(q);

        float qNormSqr = q.getSqrNormOf(q);

        return new Quaternion(qConj.x / qNormSqr,qConj.y / qNormSqr ,qConj.z / qNormSqr,qConj.w / qNormSqr);
    }

    public float toAngle()
    {
         return (float) (2 * acos(this.normalized.w));
    }

}