package com.example.amyzhao.graphtest2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
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
        toResults();
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

        String address = "something"; //TODO: change to real address
        String response = postData(address, data);
    }

        //TODO: Amy
        //Method to post latest data to server
        public String postData(String address, String data){
            String response = "some response from server";
            return response;
        }






    //Note: changed to do automatically on onComplete instead of with button (took out View arg)

    public void toResults() {

        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        startActivity(intent);
    }

}
