package com.example.amyzhao.graphtest2;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.loopj.android.http.*;

public class GraphActivity extends AppCompatActivity {

    String username;
    String URL;
    float[] fvc;
    float[] fev;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Bundle extra = getIntent().getExtras();
        username = extra.getString("username");
        System.out.println(username);
        URL = "http://spiro.suyash.io/" + username + "/data";
        getDataFromServer(URL, username);

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


    //TODO: Suyash
    //parse data received from server to send to generateGraphs (will need to add args)
    public void parseDatafromServer(String address) {
        //String data = getDataFromServer(address); //do for last 10 points
        //parse ratios from data
        generateGraphs();
    }

    //TODO: Amy
    //Method to get data from server
    public void getDataFromServer(String URL, String user) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //new getInfoTask().execute(username);
            new getInfoTask().execute(username);
        } else {
            // error
            System.out.println("no connection :(");
        }

    }

    private class getInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                if (getInfo(URL)) {
                    return "";
                } else {
                    throw new IOException("error");
                }

            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //textView.setText(result);
            generateGraphs();
        }
    }

    private class postInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                if (postInfo(username)) {
                    return "";
                } else {
                    throw new IOException("error");
                }

            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //textView.setText(result);
            generateGraphs();
        }
    }

    public boolean postInfo(String username) {
        try {
            URL url = new URL("http://colab-sbx-76.oit.duke.edu:8000/pushData");
            String urlParameters="{\"clubhash\":\"100457d41b9-ab22-4825-9393-ac7f6e8ff961\",\"username\":\"anonymous\",\"message\":\"simply awesome\",\"timestamp\":\"2012/11/05 13:00:00\"}";

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            //con.setRequestProperty("Content-Length", "" +
            //Integer.toString(urlParameters.getBytes().length));
            con.setRequestProperty("Content-Language", "en-US");

            con.setDoOutput(true);
            con.setDoInput(true);
            con.setChunkedStreamingMode(0);

            con.connect();

            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            //byte[] buffer = urlParameters.getBytes();
            out.writeBytes(urlParameters);
            out.flush();
            out.close();

            InputStream is = con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println("message="+response.toString());

            con.disconnect();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean getInfo(String URL) {
        try {
            URL url = new URL(URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setRequestMethod("GET");

            con.connect();

            int response = con.getResponseCode();
            System.out.println("response: ");
            System.out.println(response);

            InputStream is = con.getInputStream();


            String contentAsString = "";
            int a;
            while((a = is.read()) != -1) {
                contentAsString = contentAsString + (char) a;
            }

            System.out.println(contentAsString);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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