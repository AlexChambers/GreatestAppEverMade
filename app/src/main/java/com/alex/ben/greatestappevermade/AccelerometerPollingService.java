package com.alex.ben.greatestappevermade;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

// Accelerometer Class!
public class AccelerometerPollingService extends Service implements SensorEventListener {

    SensorManager accelManager;
    Long time;
    Sensor accelerometer;
    float accelX;
    float accelY;
    float accelZ;

    SQLiteDatabase db;
    String NAME;

    public AccelerometerPollingService(){}

    public void onSensorChanged(SensorEvent sensorEvent) {
        // Record accelerometer event data
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER && (System.currentTimeMillis() - time > 1000)) {
            time = System.currentTimeMillis();
            accelX = sensorEvent.values[0];
            accelY = sensorEvent.values[1];
            accelZ = sensorEvent.values[2];

            db = openOrCreateDatabase(NAME, Context.MODE_PRIVATE, null);
            db.beginTransaction();
            try {
                db.execSQL("insert into " + NAME + "(time, x, y, z) values (" + accelX + ", " + accelY + ", " + accelZ + ");");
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                // Could not insert row
            } finally {
                db.endTransaction();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


    @Override
    public void onCreate() {
        accelManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = accelManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        time = System.currentTimeMillis();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NAME = intent.getStringExtra("TABLE_NAME");
        return startId; }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}