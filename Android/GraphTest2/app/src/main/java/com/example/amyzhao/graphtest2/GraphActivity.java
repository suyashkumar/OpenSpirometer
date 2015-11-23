package com.example.amyzhao.graphtest2;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateFormat;
import java.util.*;

public class GraphActivity extends AppCompatActivity {

    String username;
    String URL;
    String content;
    List<Double> FVC;
    List<Double> FEV;
    private static Context context;
    ArrayList<SpiroData> recData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Bundle extra = getIntent().getExtras();
        username = extra.getString("username");
        System.out.println(username);
        URL = "http://spiro.suyash.io/api/" + username;
        System.out.println(URL);
        getDataFromServer();
        recData = new ArrayList<SpiroData>();
        GraphActivity.context = getApplicationContext();
        FEV = new ArrayList<Double>();
        FVC = new ArrayList<Double>();
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
    // hi suyash, content is the data received from server as a string
    public void parseDatafromServer(String address) {
        //String data = getDataFromServer(); //do for last 10 points
        //parse ratios from data
        try {
            JSONArray jArr = new JSONArray(content);
            for (int i=0;i<jArr.length();i++){
                JSONObject currObj = jArr.getJSONObject(i);
                recData.add(new SpiroData(currObj.toString()));
                System.out.println(currObj.getDouble("FEV"));
                FEV.add(currObj.getDouble("FEV"));
                FVC.add(currObj.getDouble("FVC"));
            }
            System.out.println(FEV);
            System.out.println(FVC);
            //FVC and FEV are arraylists with the data
            
            //System.out.println("Stuff: "+jArr.getJSONObject(0).getString("date"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        generateGraphs(FEV, FVC);
    }

    //TODO: Amy
    //Method to get data from server
    public void getDataFromServer() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
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
                if (getInfo()) {
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
            parseDatafromServer(URL);
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
            //generateGraphs();
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

    private boolean getInfo() {
        try {
            String urlToUse = URL + "/data";
            System.out.println(urlToUse);
            URL url = new URL(urlToUse);
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

            content = contentAsString;

            System.out.println(contentAsString);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean generateGraphs(List<Double> FEV, List<Double> FVC) {

        List<Double> ratio = new ArrayList<Double>();
        for (int i = 0; i < FEV.size(); i++) {
            double r = FEV.get(i)/FVC.get(i);
            ratio.add(r);
        }

        /*HashMap<Integer, Double> map = new HashMap<Integer, Double>();

        int entry = 0;
        String dates[] = new String[10];
        for (int i=FEV.size()-10; i<FEV.size(); i++){
            long unixSeconds = 1372339860; //get from server
            Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
            sdf.setTimeZone(TimeZone.getTimeZone("GMT-5")); // give a timezone reference for formating
            String formattedDate = sdf.format(date);
            dates[entry] = formattedDate;
            double ratio = FEV.get(i)/FVC.get(i);
            map.put(entry, ratio);
            entry++;
        }*/

        int[] dates = new int[]{1372339860,1372426260,1372512660};

        // Line graph
        GraphView lineGraph = (GraphView) findViewById(R.id.lineGraph);
        LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(dates[0], ratio.get(0)),
        });

        for (int i = 1; i < dates.length; i++) {
            lineSeries.appendData(new DataPoint(dates[i], ratio.get(i)), true, 10);
        }

        lineGraph.addSeries(lineSeries);
        //adjust x labels to formattedtime strings from dates array

        lineSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                Toast.makeText(context, (CharSequence) ("FEV/FVC = " + dataPointInterface), Toast.LENGTH_LONG).show();
            }
        });

        Viewport viewport = lineGraph.getViewport();

        final java.text.DateFormat dateTimeFormatter = DateFormat.getDateFormat(this);

        lineGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // transform number to time
                    return dateTimeFormatter.format(new Date((long) value*1000));
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
/*
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(40);
*/
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(2);

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
        intent.putExtra("username", username);
        startActivity(intent);
    }



}