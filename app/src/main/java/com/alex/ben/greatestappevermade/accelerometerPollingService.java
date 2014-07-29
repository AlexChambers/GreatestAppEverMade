package com.alex.ben.greatestappevermade;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.AsyncTask;

/**
 * Created by achambe4 on 7/28/2014.
 */
public class accelerometerPollingService extends AsyncTask<String, Long, Void> {

    public void onPreExecute()
    {
        // Begin doing something here
    }

    protected Void doInBackground(final String args)
    {
        publishProgress(Long);
    }

}
