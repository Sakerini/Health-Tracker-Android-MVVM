package com.example.healthtracker.falldetection;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.healthtracker.model.User;

public class Accelerometer implements SensorEventListener {

    private Sensor mSensor;
    private SensorManager mSensorManager;

    private long lastUpdate = -1;
    private float accelerationValues[];
    private float lastAccelerationValues[];

    private int fallThreshold = 20;

    private float mAccelCurrent = SensorManager.GRAVITY_EARTH;
    private float mAccelLast = SensorManager.GRAVITY_EARTH;
    private float mAccel = 0.00f;

    private final static int CHECK_INTERVAL = 100; // [msec]


    public Accelerometer(SensorManager sensorManager, Sensor sensor) {
        mSensorManager = sensorManager;
        mSensor = sensor;
    }

    public void startListening() {
        if (mSensor == null) {
            // Send a failure msg
        } else {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void stopListening() {
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // sampling frequency f= 10Hz.
            if ((curTime - lastUpdate) > CHECK_INTERVAL) {

                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                accelerationValues = event.values.clone();

                if (lastAccelerationValues != null) {

                    mAccelLast = mAccelCurrent;
                    double x = accelerationValues[0];
                    double y = accelerationValues[1];
                    double z = accelerationValues[2];

                    mAccelCurrent = (float) Math.sqrt(Math.abs(x) * Math.abs(x) + Math.abs(y) * Math.abs(y)
                            + Math.abs(z) * Math.abs(z));

                    float delta = mAccelCurrent - mAccelLast;
                    mAccel = mAccel * 0.9f + delta;


                    if (mAccel > fallThreshold) {

                        Log.w("ACCELERE MACC", "acceleration greater than threshold");
                        // Fall Detected !!
                        // reinitialize data
                        lastUpdate = -1;
                        fallThreshold = 10;
                        mAccel = 0.00f;
                        mAccelCurrent = SensorManager.GRAVITY_EARTH;
                        mAccelLast = SensorManager.GRAVITY_EARTH;
                        User.getInstance().setFallen(true);
                        accelerationValues = null;
                        lastAccelerationValues = null;
                        return;
                    }
                }

                lastAccelerationValues = accelerationValues.clone();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }
}
