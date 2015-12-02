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
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        ratio = FEV/FVC*100;
        URL = "http://spiro.suyash.io/api/" + username;
        updateUI();
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

    //Parse data received from server to get ratio, FVC, FEV
    public void parseDatafromServer(String address) {
        double ratio=0;
        double FVC=0;
        double FEV=0;
        updateUI();
    }

    public void updateUI(){
        TextView FEVText = (TextView) findViewById(R.id.FEV);
        TextView FVCText = (TextView) findViewById(R.id.FVC);
        TextView ratioText = (TextView) findViewById(R.id.ratio);

        Double fev1Val = round(FEV, 2);
        Double fvcVal = round(FVC, 2);
        ratio = round(ratio, 2);

        FEVText.setText(Double.toString(fev1Val));
        FVCText.setText(Double.toString(fvcVal));
        ratioText.setText(String.valueOf(ratio));

        if(ratio < 70) {
            TextView warning = (TextView) findViewById(R.id.warning);
            warning.setVisibility(View.VISIBLE);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void toRecord(View view) {
        TextView warning = (TextView) findViewById(R.id.warning);
        warning.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void toGraph(View view) {
        TextView warning = (TextView) findViewById(R.id.warning);
        warning.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
