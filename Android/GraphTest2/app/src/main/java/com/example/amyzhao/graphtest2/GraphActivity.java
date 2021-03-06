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
import android.widget.TextView;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.*;

public class GraphActivity extends AppCompatActivity {

    String username;
    String URL;
    String content;
    List<Double> FVC;
    List<Double> FEV;
    List<Integer> dates;
    List<List<Double>> dataArray;
    List<List<String>> tagArray;
    private static Context context;
    ArrayList<SpiroData> recData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Bundle extra = getIntent().getExtras();
        username = extra.getString("username");
        URL = "http://spiro.suyash.io/api/" + username;
        getDataFromServer();
        recData = new ArrayList<SpiroData>();
        GraphActivity.context = getApplicationContext();
        FEV = new ArrayList<Double>();
        FVC = new ArrayList<Double>();
        dates = new ArrayList<Integer>();
        dataArray = new ArrayList<List<Double>>();
        tagArray = new ArrayList<List<String>>();

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


    //parse data received from server to send to generateGraphs (will need to add args)
    public void parseDatafromServer(String address) {
        //parse ratios from data
        try {
            JSONArray jArr = new JSONArray(content);
            for (int i=0;i<jArr.length();i++){
                JSONObject currObj = jArr.getJSONObject(i);
                recData.add(new SpiroData(currObj.toString()));

                FEV.add(currObj.getDouble("FEV"));
                FVC.add(currObj.getDouble("FVC"));
                dates.add(Integer.parseInt(currObj.getString("date")));

                JSONObject paramObj = currObj.getJSONObject("params");
                JSONArray tagJSON = paramObj.getJSONArray("tags");

                ArrayList<String> currentTagList = new ArrayList<String>();
                for (int j = 0; j<tagJSON.length(); j++){
                    currentTagList.add(tagJSON.getString(j));
                }
                tagArray.add(currentTagList);

                JSONArray dataJSON = currObj.getJSONArray("data");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (FEV.size()!=0) {
            generateGraphs(FEV, FVC);
            updateUI();
        }

    }

    public void updateUI() {
        TextView fev1 = (TextView) findViewById(R.id.fev1Recent);
        TextView fvc = (TextView) findViewById(R.id.fvcRecent);
        //TextView fev1norm = (TextView) findViewById(R.id.fev1Normal);
        //TextView fvcnorm = (TextView) findViewById(R.id.fvcNormal);
        TextView date = (TextView) findViewById(R.id.date);

        Double fev1Val = round(FEV.get(FEV.size()-1), 2);
        Double fvcVal = round(FVC.get(FVC.size()-1), 2);
        String dateVal = dateToString(dates.get(dates.size()-1));

        String fevText = "FEV1: " + Double.toString(fev1Val);
        String fvcText = "FVC: " + Double.toString(fvcVal);
        String fevNormText = "FEV1 (Normal Range): 2.5 - 6.0 L";
        String fvcNormText = "FVC (Normal Range): 3.0 - 7.0 L";
        String dateText = "Date: " + dateVal;

        fev1.setText(fevText + " L");
        fvc.setText(fvcText + " L");
        //fev1norm.setText(fevNormText);
        //fvcnorm.setText(fvcNormText);
        date.setText(dateText);
    }

    public String dateToString(Integer timestamp) {
        int unixSeconds = timestamp;
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //Method to get data from server
    public void getDataFromServer() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new getInfoTask().execute(username);
        } else {
            // error
            System.out.println("No connection.");
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

    private boolean getInfo() {
        try {

            String urlToUse = URL + "/data";
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
            StringBuilder buf = new StringBuilder();
            int a;
            while((a = is.read()) != -1) {
                buf.append((char)a);
            }

            content = buf.toString();
            System.out.println("content:"+content);

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
            double r = 100*FEV.get(i)/FVC.get(i);
            ratio.add(r);
        }

        // Line graph
        GraphView lineGraph = (GraphView) findViewById(R.id.lineGraph);
        final HashMap<Integer, List<String>> tagMap = new HashMap<Integer, List<String>>();
        LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {
                //new DataPoint(dates.get(0), ratio.get(0)),
        });
        if (tagArray.size()>0) tagMap.put(dates.get(0), tagArray.get(0));

        for (int i = 0; i < dates.size(); i++) {
            lineSeries.appendData(new DataPoint(dates.get(i), ratio.get(i)), true, dates.size());
            if (tagArray.size()>0) tagMap.put(dates.get(i), tagArray.get(i));
        }

        lineSeries.setDrawDataPoints(true);
        lineSeries.setDataPointsRadius(5);
        lineGraph.addSeries(lineSeries);

        lineSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                if (tagArray.size()>0) {
                    List<String> tags = tagMap.get((int) dataPointInterface.getX());
                    if (tags != null && tags.size() > 0) {
                        StringBuilder t = new StringBuilder();
                        for (int i = 0; i < tags.size(); i++) {
                            t.append(tags.get(i));
                            if (i < tags.size()-1) {
                                t.append(", ");
                            }
                        }
                        if (t.length() > 0) {
                            Toast.makeText(context, (CharSequence) (round(dataPointInterface.getY(), 2) + "% - " + t.toString()), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, (CharSequence) (round(dataPointInterface.getY(), 2) + "% - no tags"), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else{
                    Toast.makeText(context, (CharSequence) (dataPointInterface.getY() + "%"), Toast.LENGTH_LONG).show();
                }
            }
        });

        Viewport viewport = lineGraph.getViewport();

        Locale l = new Locale("en", "US");
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\nHH:mm");
        lineGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // transform number to time
                    return sdf.format(new Date((long) value * 1000));
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        lineGraph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
        lineGraph.getGridLabelRenderer().setVerticalAxisTitle("FEV1/FVC (%)");
        lineGraph.getGridLabelRenderer().setLabelHorizontalHeight(40);

        double next_hour = viewport.getMaxX(true) - (viewport.getMaxX(true) % 300) + 300;
        double prev_hour = next_hour-2700;

        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(prev_hour);
        viewport.setMaxX(next_hour);

        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(100);

        viewport.setScrollable(true);

        // flow rate graph
        GraphView flowGraph = (GraphView) findViewById(R.id.barGraph);
        List<Double> currentData = recData.get(recData.size()-1).data;
        LineGraphSeries<DataPoint> flowSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, currentData.get(0)),
        });
        for (int i = 1; i < currentData.size(); i++) {
            flowSeries.appendData(new DataPoint(0.02*i, currentData.get(i)), true, currentData.size());

        }

        flowGraph.addSeries(flowSeries);
        flowGraph.getGridLabelRenderer().setHorizontalAxisTitle("Time (s)");
        flowGraph.getGridLabelRenderer().setVerticalAxisTitle("Flow Rate (L/s)");

        return true;
    }

    public void toRecord(View view) {
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

}