package com.alex.ben.greatestappevermade;

// GraphView class provided by jjoe64
// Source: https://github.com/jjoe64/GraphView
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

// Standard Library Imports
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
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



public class MainActivity extends ActionBarActivity {


    // Database
    SQLiteDatabase db;

    // Passing this as a Bundle to AccelerometerPollingService class
    //Bundle data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        // Create the graph. Set colors and values.
        final GraphViewSeries dataSeriesX = new GraphViewSeries( new GraphViewData[] { new GraphViewData(1, 0)});
        final GraphViewSeries dataSeriesY = new GraphViewSeries( new GraphViewData[] { new GraphViewData(1, 0)});
        final GraphViewSeries dataSeriesZ = new GraphViewSeries( new GraphViewData[] { new GraphViewData(1, 0)});
        final GraphView graphView = new LineGraphView(this, "Assignment 2");

        graphView.setManualYAxisBounds(1, 0);  //Sets Y-axis bounds, ensures that blank graph doesn't look odd
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
				/*Random rand = new Random();
                // generates graph using 10 most recent data entries from the database
                float dataArrayX[] = new float[10];
                float dataArrayY[] = new float[10];
                float dataArrayZ[] = new float[10];

                // Initialize array values to 0
                for(int looper = 10; looper > 0; looper--)
                {
                    dataArrayX[looper] = 0;
                    dataArrayY[looper] = 0;
                    dataArrayZ[looper] = 0;
                }

                // for loop counts backwards, just like our database table (start with last entry in table)
                for(int looper = 10; looper > 0; looper--)
                {
                    // Get the data from database table
                    // dataArrayX[looper] = ;
                    // dataArrayY[looper] = ;
                    // dataArrayZ[looper] = ;
                }

                // Set random data for three graphs
                dataSeriesX.resetData(new GraphViewData[] {
                        new GraphViewData(1, rand.nextDouble()), new GraphViewData(2, rand.nextDouble()),
                        new GraphViewData(3, rand.nextDouble()), new GraphViewData(4, rand.nextDouble()),
                        new GraphViewData(5, rand.nextDouble()), new GraphViewData(6, rand.nextDouble()),
                        new GraphViewData(7, rand.nextDouble()), new GraphViewData(8, rand.nextDouble()),
                        new GraphViewData(9, rand.nextDouble()), new GraphViewData(10, rand.nextDouble())
                });

                dataSeriesY.resetData(new GraphViewData[] {
                        new GraphViewData(1, rand.nextDouble()), new GraphViewData(2, rand.nextDouble()),
                        new GraphViewData(3, rand.nextDouble()), new GraphViewData(4, rand.nextDouble()),
                        new GraphViewData(5, rand.nextDouble()), new GraphViewData(6, rand.nextDouble()),
                        new GraphViewData(7, rand.nextDouble()), new GraphViewData(8, rand.nextDouble()),
                        new GraphViewData(9, rand.nextDouble()), new GraphViewData(10, rand.nextDouble())
                });

                dataSeriesZ.resetData(new GraphViewData[] {
                        new GraphViewData(1, rand.nextDouble()), new GraphViewData(2, rand.nextDouble()),
                        new GraphViewData(3, rand.nextDouble()), new GraphViewData(4, rand.nextDouble()),
                        new GraphViewData(5, rand.nextDouble()), new GraphViewData(6, rand.nextDouble()),
                        new GraphViewData(7, rand.nextDouble()), new GraphViewData(8, rand.nextDouble()),
                        new GraphViewData(9, rand.nextDouble()), new GraphViewData(10, rand.nextDouble())
                });

                graphView.getGraphViewStyle().setNumHorizontalLabels(10);
                graphView.addSeries(dataSeriesX);
                graphView.addSeries(dataSeriesY);
                graphView.addSeries(dataSeriesZ);*/
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
                String NAME = getTableName(patientname, patientid, patientage, selected);

                // Do nothing if any field is empty
                if (patientage.getText().toString().equals("") || patientid.getText().toString().equals("") || patientname.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {

                    db = openOrCreateDatabase(NAME, Context.MODE_PRIVATE, null);

                    // Create table in database
                    db.beginTransaction();
                    try {
                        db.execSQL("create table " + NAME + " ( recID integer PRIMARY KEY autoincrement, time text, x text, y text, z text );");
                        db.setTransactionSuccessful();
                        Toast.makeText(getApplicationContext(), "Table Created", Toast.LENGTH_LONG).show();
                    } catch (SQLiteException sqlError) {
                        Toast.makeText(getApplicationContext(), "Could not query database:\n" + NAME, Toast.LENGTH_SHORT).show();
                    } finally {
                        db.endTransaction();
                    }
                    Intent accelIntent = new Intent();
                    accelIntent.putExtra("TABLE_NAME", NAME);
                    accelIntent.setAction("com.alex.ben.greatestappevermade.AccelerometerPollingService");
                    startService(accelIntent);
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
}