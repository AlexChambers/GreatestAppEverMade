package com.alex.ben.greatestappevermade;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.Random;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .commit();
        }

        final GraphViewSeries exampleSeries = new GraphViewSeries( new GraphViewData[] { new GraphViewData(1, 0)}); //Initializes a graph with no data
        final GraphView graphView = new LineGraphView(this, "Assignment 1");
        graphView.setManualYAxisBounds(1, 0);  //Sets Y-axis bounds, ensures that blank graph doesn't look odd
        graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
        graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.RED);
        graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLUE);
        graphView.getGraphViewStyle().setNumHorizontalLabels(2);
        graphView.getGraphViewStyle().setNumVerticalLabels(5);
        graphView.addSeries(exampleSeries);

        //finds the display screen height
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;

        //Makes the graph only take up 70% of the screen
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) graphView.getLayoutParams();
        params.height = (int) (.7 * height);
        graphView.setLayoutParams(params);
        //Makes the graph only take up 70% of the screen

        final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.addView(graphView);




        // Button that generates graph
        Button generateGraphButton = (Button) findViewById(R.id.startGraph);
        generateGraphButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Random rand = new Random();
                //on each click, generates 4 random points of data
                exampleSeries.resetData(new GraphViewData[] {
                                new GraphViewData (1, rand.nextDouble()), new GraphViewData (2, rand.nextDouble()), new GraphViewData (3, rand.nextDouble()), new GraphViewData (4, rand.nextDouble())
                        }
                );
                graphView.getGraphViewStyle().setNumHorizontalLabels(4);
                graphView.addSeries(exampleSeries);
            }
        });

        Button clearGraph = (Button) findViewById(R.id.clearGraph);
        clearGraph.setOnClickListener (new OnClickListener (){
            public void onClick (View v){
                //resets to blank graph
                exampleSeries.resetData(new GraphViewData[] {
                                new GraphViewData (1, 0)
                        }
                );
                graphView.getGraphViewStyle().setNumHorizontalLabels(2);
                graphView.addSeries(exampleSeries);
            }
        });


    }

}
