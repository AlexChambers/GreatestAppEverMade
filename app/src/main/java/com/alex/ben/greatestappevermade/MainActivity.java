package com.example.testgraph;

// GraphView class provided by jjoe64
// Source: https://github.com/jjoe64/GraphView
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.Timer;

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


public class MainActivity extends ActionBarActivity {

	boolean run = true;
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
    		double [] x = msg.getData().getDoubleArray("xvalue");
    		double [] y = msg.getData().getDoubleArray("yvalue");
    		double [] z = msg.getData().getDoubleArray("zvalue");
    		int [] time = msg.getData().getIntArray("time");
    		
    		dataSeriesX.resetData(makeNewData(x, time));
    		dataSeriesY.resetData(makeNewData (y, time));
    		dataSeriesZ.resetData(makeNewData(z, time));
    		graphView.addSeries(dataSeriesX);
    		graphView.addSeries(dataSeriesY);
    		graphView.addSeries(dataSeriesZ);
    		graphView.redrawAll();
    		
    	}

		private GraphViewData[] makeNewData(double[] x, int[] time) {
			GraphViewData [] data = new GraphViewData [time.length];
			for (int i = 0; i < time.length; i++){
				data [i] = new GraphViewData (time [i], x [i]);
			}
			return data;
		}
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
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
                graphView.redrawAll();
                
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
                        db.execSQL("create table " + NAME + " (time text, x text, y text, z text );");
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
			//while (run) {
				double x [];
				double y [];
				double z [];
				int time [];
				//try {
					Cursor c = db.rawQuery("SELECT * FROM " + NAME, null);
					int n = c.getCount();
					int xcol = c.getColumnIndex("x");
					int ycol = c.getColumnIndex("y");
					int zcol = c.getColumnIndex("z");
					//Checks how much data there is, returns last 10 or all the current data
					if (n < 10){
						time = new int [10];
						for (int k = 0; k < 10; k++){
							time [k] = k + 1;
						}
						x = getData (db, xcol, n - 10, 10, c);
						y = getData (db, ycol, n - 10, 10, c);
						z = getData (db, zcol, n - 10, 10, c);
					} else{
						time = new int [n];
						for (int k = 0; k < n; k++){
							time [k] = k + 1;
						}
						x = getData (db, xcol, 0, n, c);
						y = getData (db, ycol, 0, n, c);
						z = getData (db, zcol, 0, n, c);
					}
					Bundle b = new Bundle (1);
					b.putIntArray("time", time);
					b.putDoubleArray("xvalue",x);
					b.putDoubleArray("yvalue",y);
					b.putDoubleArray("zvalue",z);
					Message msg = threadHandle.obtainMessage();
					msg.setData(b);
					threadHandle.sendMessage(msg);
					
				//} catch (InterruptedException e){
				//	e.printStackTrace();
				//}
			//}
			return null;
		}


		private double[] getData(SQLiteDatabase db, int column, int position, int length, Cursor c) {
			double [] data = new double [length];
			if (c.moveToPosition(position)) {
				for (int k = 0; k < length; k++)
				{
					data [k] = c.getDouble(column);
					boolean bool = c.moveToNext();
				}
			}
			return data;
		}
    	
    }
}