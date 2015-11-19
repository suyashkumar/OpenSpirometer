package com.example.amyzhao.graphtest2;

import android.content.Intent;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;




public class ResultsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        String address = "something"; //TODO: change to real address
        getDatafromServer(address);


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
    public void getDatafromServer(String address) {
        String data = getData(address); //do for last 10 points
        //parse ratio, FVC, FEV from data
        double ratio=0;
        double FVC=0;
        double FEV=0;
        updateUI(ratio, FVC, FEV);
    }

    //TODO: Amy
    //Method to get data from server
    public String getData(String address){
        String data = "response from server";
        return data;
    }

    public void updateUI(double ratio, double FVC, double FEV){
        TextView FEVText = (TextView) findViewById(R.id.FEV);
        TextView FVCText = (TextView) findViewById(R.id.FVC);
        TextView ratioText = (TextView) findViewById(R.id.ratio);
        FEVText.setText(String.valueOf(FEV));
        FVCText.setText(String.valueOf(FVC));
        ratioText.setText(String.valueOf(ratio));
    }

    public void toRecord(View view) {
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        startActivity(intent);
    }

    public void toGraph(View view) {
        Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
        startActivity(intent);
    }
}
