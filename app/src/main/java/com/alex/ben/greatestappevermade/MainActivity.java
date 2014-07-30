package com.alex.ben.greatestappevermade;

// GraphView class provided by jjoe64
// Source: https://github.com/jjoe64/GraphView
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

// Standard Library Imports
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    // Database
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .commit();
        }

        // Create the graph (color, values)
        final GraphViewSeries dataSeries = new GraphViewSeries( new GraphViewData[] { new GraphViewData(1, 0)}); //Initializes a graph with no data
        final GraphView graphView = new LineGraphView(this, "Assignment 1");
        graphView.setManualYAxisBounds(1, 0);  //Sets Y-axis bounds, ensures that blank graph doesn't look odd
        graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
        graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.RED);
        graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLUE);
        graphView.getGraphViewStyle().setNumHorizontalLabels(2);
        graphView.getGraphViewStyle().setNumVerticalLabels(5);
        graphView.addSeries(dataSeries);

        // Finds the display screen height
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;

        // Makes the graph only take up 70% of the screen
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) graphView.getLayoutParams();
        params.height = (int) (.7 * height);
        graphView.setLayoutParams(params);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.addView(graphView);

        // Button that generates the graph
        Button generateGraphButton = (Button) findViewById(R.id.startGraph);
        generateGraphButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Random rand = new Random();
                //on each click, generates 4 random points of data
                dataSeries.resetData(new GraphViewData[]{
                                new GraphViewData(1, rand.nextDouble()), new GraphViewData(2, rand.nextDouble()), new GraphViewData(3, rand.nextDouble()), new GraphViewData(4, rand.nextDouble())
                        }
                );
                graphView.getGraphViewStyle().setNumHorizontalLabels(4);
                graphView.addSeries(dataSeries);
            }
        });

        // Button that clears the graph
        Button clearGraph = (Button) findViewById(R.id.clearGraph);
        clearGraph.setOnClickListener (new OnClickListener (){
            public void onClick (View v){

                //resets to blank graph
                dataSeries.resetData(new GraphViewData[]{ new GraphViewData(1, 0)} );
                graphView.getGraphViewStyle().setNumHorizontalLabels(2);
                graphView.addSeries(dataSeries);
            }
        });

        // User info fields
        final EditText patientname = (EditText) findViewById (R.id.name);
        final EditText patientage = (EditText) findViewById (R.id.age);
        final EditText patientid = (EditText) findViewById (R.id.ID);

        final RadioGroup genders = (RadioGroup) findViewById (R.id.genders);
        genders.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                int selected = genders.getCheckedRadioButtonId();
                String NAME = getTableName(patientname, patientid, patientage, selected);

                // Do nothing if a field is empty
                if( patientage.toString().equals("") ||
                    patientid.toString().equals("") ||
                    patientname.toString().equals("") )
                { Toast.makeText(getApplicationContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show(); }
                else
                {
                    try {
                        db = openOrCreateDatabase(NAME, Context.MODE_PRIVATE, null);
                    } catch (SQLiteException e) {
                        Toast.makeText(getApplicationContext(), "Could not open/create database", Toast.LENGTH_SHORT).show();
                    }
                    db.beginTransaction();

                    try {
                        db.execSQL("create table " + NAME + " ( recID integer PRIMARY KEY autoincrement, time text, x text, y text, z text );");
                        db.setTransactionSuccessful();
                    } catch (SQLiteException e) {
                        Toast.makeText(getApplicationContext(), "Could not query database", Toast.LENGTH_SHORT).show();
                    } finally {
                        db.endTransaction();
                    }
                }
            }
        });
    }
    public String getTableName(EditText name, EditText id, EditText age, int selected){
        String sex;
        if (selected == R.id.female){
            sex = "Female";
        } else {
            sex = "Male";
        }
        // Return the table name
        return (name.getText().toString() + "_" + id.getText().toString() + "_" + age.getText().toString() + "_" + sex);
    }

    // Accelerometer Class!
    private class accelerometerPollingService extends Service implements SensorEventListener  {

        SensorManager accelManager;
        Sensor accelerometer;
        float accelX;
        float accelY;
        float accelZ;

        public void onSensorChanged(SensorEvent sensorEvent)
        {
            // Only want accelerometer events
            Sensor sensor = sensorEvent.sensor;
            if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelX = sensorEvent.values[0];
                accelY = sensorEvent.values[1];
                accelZ = sensorEvent.values[2];

                accelManager.unregisterListener(this);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
