package com.alex.ben.greatestappevermade;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

// Accelerometer Class!
public class AccelerometerPollingService extends Service implements SensorEventListener {

    SensorManager accelManager;
    Sensor accelerometer;
    float accelX;
    float accelY;
    float accelZ;

    public AccelerometerPollingService(){}

    public void onSensorChanged(SensorEvent sensorEvent) {
        // Record accelerometer event data
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelX = sensorEvent.values[0];
            accelY = sensorEvent.values[1];
            accelZ = sensorEvent.values[2];

            // Store these in the database HERE!
            // db.execSQL( "insert into " +  )
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onCreate() {
        Toast.makeText(this, "Accelerometer is Active!", Toast.LENGTH_LONG).show();
        accelManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = accelManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return startId; }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}