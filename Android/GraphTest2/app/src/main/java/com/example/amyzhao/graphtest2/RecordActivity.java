package com.example.amyzhao.graphtest2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class RecordActivity extends AppCompatActivity {

    String username;
    String URL;
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Bundle extra = getIntent().getExtras();
        username = extra.getString("username");
        System.out.println(username);
        URL = "http://spiro.suyash.io/api/" + username;
        System.out.println(URL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
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


    //split by commas
    public String[] getTags(){
        EditText tagText = (EditText) findViewById(R.id.tags);
        String tagString = tagText.getText().toString();
        String[] tags =tagString.split("\\s*,\\s*");
        return tags;
    }

    //TODO: Suyash
    public void record(View view){
        String[] tags = getTags();
        //Bluetooth async task
        //on post-execute:
        int[] buffer = new int[400]; //get from arduino
        calculateAndSend(buffer, tags);
    }

    public void calculateAndSend(int[] buffer, String[] tags){
        double k = 2.4; //calibration constant
        int FVC = 0;
        int FEV = 0;
        double[] flowRates = new double[400];
        for (int i = 0; i < 400; i++) {
            double voltage = buffer[i] * 5 / 1023;
            double flowRate = voltage * k;
            flowRates[i] = flowRate;
            FVC += flowRate * 0.02;  //sum(Qdt)
            if (i < 50) {            //first second
                FEV += flowRate * 0.02;
            }
        }

        //TODO: Suyash
        //compile string of all necessary data (flowRates, FVC, FEV, ratio, tags)
        String data = "blabla";
        content = data;
        postDataToServer();
    }

    //TODO: Amy
    //Method to post data to server
    public void postDataToServer() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new postInfoTask().execute(username);
        } else {
            // error
            System.out.println("no connection :(");
        }
     }

    private class postInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                if (true) {
                    return postInfo();
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
            toResults();
        }
    }

    public String postInfo() {
        try {
            String urlToUse = URL + "/pushData";
            System.out.println(urlToUse);
            URL url = new URL(urlToUse);
            //String urlParameters="{\"clubhash\":\"100457d41b9-ab22-4825-9393-ac7f6e8ff961\",\"username\":\"anonymous\",\"message\":\"simply awesome\",\"timestamp\":\"2012/11/05 13:00:00\"}";

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
            out.writeBytes(content);
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
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }


    //Note: changed to do automatically on onComplete instead of with button (took out View arg)

    public void toResults() {
        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

}
