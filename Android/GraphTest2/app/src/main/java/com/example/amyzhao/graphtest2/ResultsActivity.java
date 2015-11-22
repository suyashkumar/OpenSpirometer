package com.example.amyzhao.graphtest2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;




public class ResultsActivity extends AppCompatActivity {

    String URL;
    String username;
    String content;
    double FEV;
    double FVC;
    double ratio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Bundle extra = getIntent().getExtras();
        username = extra.getString("username");
        FEV = extra.getDouble("FEV");
        FVC = extra.getDouble("FVC");
        ratio = FEV/FVC;
        System.out.println(username);
        URL = "http://spiro.suyash.io/api/" + username;
        System.out.println(URL);
        updateUI();
        //getDataFromServer();


    //time stuff--probably better way?
//        Calendar c = Calendar.getInstance();
//        Date time = c.getTime();
//        int date = c.get(Calendar.DAY_OF_MONTH);
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int hour = c.get(Calendar.HOUR_OF_DAY);
//        int minutes = c.get(Calendar.MINUTE);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
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

    //TODO: Suyash
    //Parse data received from server to get ratio, FVC, FEV
    public void parseDatafromServer(String address) {
        //parse ratio, FVC, FEV from data
        double ratio=0;
        double FVC=0;
        double FEV=0;
        updateUI();
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
            //parseDatafromServer(URL);
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

    public void updateUI(){
        TextView FEVText = (TextView) findViewById(R.id.FEV);
        TextView FVCText = (TextView) findViewById(R.id.FVC);
        TextView ratioText = (TextView) findViewById(R.id.ratio);
        FEVText.setText(String.valueOf(FEV));
        FVCText.setText(String.valueOf(FVC));
        ratioText.setText(String.valueOf(ratio));
    }

    public void toRecord(View view) {
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void toGraph(View view) {
        Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
