package com.example.amyzhao.graphtest2;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.DefaultLabelFormatter;

public class GraphActivity extends AppCompatActivity {

    float[] fvc;
    float[] fev;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        System.out.println("hello");
        //Intent intent = getIntent();
        System.out.println("got intent");
        generateGraphs();
        GraphActivity.context = getApplicationContext();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean generateGraphs() {
        // Line graph
        GraphView lineGraph = (GraphView) findViewById(R.id.lineGraph);
        LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6),
                new DataPoint(5, 2),
                new DataPoint(6, 10),
                new DataPoint(7, 11),
        });
        lineGraph.addSeries(lineSeries);

        lineSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                Toast.makeText(context, (CharSequence) ("10/30/15: FVC = " + dataPointInterface), Toast.LENGTH_LONG).show();
            }
        });

        Viewport viewport = lineGraph.getViewport();

        lineGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " L";
                }
            }
        });

        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(5);
        viewport.setMaxX(7);

        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(12);

        viewport.setScrollable(true);

        // bar graph
        GraphView barGraph = (GraphView) findViewById(R.id.barGraph);
        BarGraphSeries<DataPoint> barSeries = new BarGraphSeries<>(new DataPoint[] {
            new DataPoint(0,2),
            new DataPoint(1,5),
            new DataPoint(2,3),
            new DataPoint(3,2)
        });

        barGraph.addSeries(barSeries);

        barGraph.getViewport().setYAxisBoundsManual(true);
        barGraph.getViewport().setMinY(0);
        barGraph.getViewport().setMaxY(6);

        barSeries.setSpacing(50);
        barSeries.setDrawValuesOnTop(true);
        barSeries.setValuesOnTopColor(Color.BLACK);

        return true;
    }

    public void toRecord(View view) {
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        startActivity(intent);
    }



}

