package com.alex.ben.greatestappevermade;

// GraphView class provided by jjoe64
// Source: https://github.com/jjoe64/GraphView
import java.util.Timer;
import java.util.TimerTask;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

// Standard Library Imports
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import static android.database.sqlite.SQLiteDatabase.openDatabase;



public class MainActivity extends ActionBarActivity {


    // Database
    SQLiteDatabase db;

    // Passing this as a Bundle to AccelerometerPollingService class
    Bundle data;
    String NAME;


    // Create the graph. Set colors and values.
    GraphViewSeries dataSeriesX;
    GraphViewSeries dataSeriesY;
    GraphViewSeries dataSeriesZ;
    GraphView graphView;

    //Handler to obtain data from the AsynchTask
    final Handler threadHandle = new Handler () {
        @Override
        public void handleMessage(Message msg){
            double value = msg.getData().getDouble("value");
            dataSeriesX.resetData (new GraphViewData []{new GraphViewData (1, 0), new GraphViewData (2, value)});
            graphView.addSeries(dataSeriesX);
            graphView.redrawAll();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        dataSeriesX = new GraphViewSeries( new GraphViewData[] { new GraphViewData(1, 0)});
        dataSeriesY = new GraphViewSeries( new GraphViewData[] { new GraphViewData(1, 0)});
        dataSeriesZ = new GraphViewSeries( new GraphViewData[] { new GraphViewData(1, 0)});
        graphView = new LineGraphView(this, "Accelerometer Data");

        //graphView.setManualYAxisBounds(1, 0);  //Sets Y-axis bounds, ensures that blank graph doesn't look odd
        graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
        graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.RED);
        graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLUE);
        graphView.getGraphViewStyle().setNumHorizontalLabels(10);
        graphView.getGraphViewStyle().setNumVerticalLabels(10);
        graphView.addSeries(dataSeriesX);
        graphView.addSeries(dataSeriesY);
        graphView.addSeries(dataSeriesZ);



        // Finds the display screen height
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;

        // Makes the graph only take up 60% of the screen
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) graphView.getLayoutParams();
        params.height = (int) (.6 * height);
        graphView.setLayoutParams(params);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.addView(graphView);


        // Generate the graph
        Button generateGraphButton = (Button) findViewById(R.id.startGraph);
        generateGraphButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new updateGraph().execute();
            }
        });

        // Clear the graph
        Button clearGraph = (Button) findViewById(R.id.clearGraph);
        clearGraph.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                //resets to blank graph
                dataSeriesX.resetData(new GraphViewData[]{new GraphViewData(1, 0)});
                dataSeriesY.resetData(new GraphViewData[]{new GraphViewData(1, 0)});
                dataSeriesZ.resetData(new GraphViewData[]{new GraphViewData(1, 0)});
                graphView.getGraphViewStyle().setNumHorizontalLabels(10);
                graphView.addSeries(dataSeriesX);
                graphView.addSeries(dataSeriesY);
                graphView.addSeries(dataSeriesZ);

            }
        });

        // User info fields
        final EditText patientname = (EditText) findViewById(R.id.name);
        final EditText patientage = (EditText) findViewById(R.id.age);
        final EditText patientid = (EditText) findViewById(R.id.ID);


        final RadioGroup genders = (RadioGroup) findViewById(R.id.genders);
        genders.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                int selected = genders.getCheckedRadioButtonId();
                NAME = getTableName(patientname, patientid, patientage, selected);
                Intent accelIntent = new Intent();
                accelIntent.putExtra("TABLE_NAME", NAME);
                accelIntent.setAction("com.alex.ben.greatestappevermade.AccelerometerPollingService");

                // Do nothing if any field is empty
                if (patientage.getText().toString().equals("") || patientid.getText().toString().equals("") || patientname.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {

                    db = openOrCreateDatabase(NAME, Context.MODE_PRIVATE, null); //SQLiteDatabase.CREATE_IF_NECESSARY

                    // Create table in database
                    db.beginTransaction();
                    try {
                        db.execSQL("DROP TABLE IF EXISTS " + NAME + ";");
                        db.execSQL("create table " + NAME + " ( recID integer PRIMARY KEY autoincrement, time text, x text, y text, z text );");
                        db.setTransactionSuccessful();
                        Toast.makeText(getApplicationContext(), "Table Created!\n(" + NAME + ")", Toast.LENGTH_SHORT).show();
                        startService(accelIntent);
                        Toast.makeText(getApplicationContext(), "Accelerometer Service Started!", Toast.LENGTH_SHORT).show();
                    } catch (SQLiteException sqlError) {
                        Toast.makeText(getApplicationContext(), "Table \"" + NAME + "\" already exists.", Toast.LENGTH_SHORT).show();
                    } finally {
                        db.endTransaction();
                    }


                }
            }

        });
    }

    // Generate the table name
    public String getTableName(EditText name, EditText id, EditText age, int selected) {
        String sex;
        if (selected == R.id.female) {
            sex = "Female";
        } else {
            sex = "Male";
        }
        return (name.getText().toString() + "_" + id.getText().toString() + "_" + age.getText().toString() + "_" + sex);
    }

    //Thread to continuously check the table and update the UI
    private class updateGraph extends AsyncTask<String, Long, Void>{
        @Override
        protected void onPreExecute(){
            Toast.makeText(MainActivity.this, "Thread Starting", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            for (int i = 0; i < 1000000; i++){
                try {
                    Thread.sleep(1000);
                    Bundle b = new Bundle (1);
                    b.putDouble("value",(double) i);
                    Message msg = threadHandle.obtainMessage();
                    msg.setData(b);
                    threadHandle.sendMessage(msg);

                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

    }
}