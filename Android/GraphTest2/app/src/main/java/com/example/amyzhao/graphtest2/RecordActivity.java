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
import android.widget.EditText;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.util.UUID;
import android.os.Handler;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
public class RecordActivity extends AppCompatActivity {

    String username;
    String URL;
    String content;
    double FEV;
    double FVC;
    String date;

    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    StringBuffer receivedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Bundle extra = getIntent().getExtras();
        username = extra.getString("username");
        System.out.println(username);
        URL = "http://spiro.suyash.io/api/" + username;
        System.out.println(URL);

        receivedData =new StringBuffer();
        Button openButton = (Button)findViewById(R.id.connect);

        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                }
                catch (IOException ex) { }
            }
        });


    }

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            //
            // myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("HC-06")) {
                    mmDevice = device;
                    break;
                }
            }
        }
        //myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        //beginListenForData();
        //myLabel.setText("Bluetooth Opened");

    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
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

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        System.out.println("beginning to listen for data");
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[4096];
        workerThread = new Thread(new Runnable() {
            public void run() {
                System.out.println("starting worker");
                while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0) {
                            System.out.println("bytes are available");
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    System.out.println(data);

//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            TextView test = (TextView) findViewById(R.id.testText);
//                                            test.setText(data);
//                                        }
//                                    });

                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            //myLabel.setText(data);
                                            receivedData.append(data);
                                            System.out.println(data);

                                            String[] tags = getTags();
                                            int[] parsedData = parseData(data.toString());
                                            System.out.println("testing from thread");
                                            System.out.println(Arrays.toString(tags));
                                            System.out.println(Arrays.toString(parsedData));
                                            calculateAndSend(parsedData, tags);
                                            // Call Future functions
                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    //split by commas
    public String[] getTags(){
        EditText tagText = (EditText) findViewById(R.id.tags);
        String tagString = tagText.getText().toString();
        String[] tags =tagString.split("\\s*,\\s*");
        System.out.println("getTags result:");
        System.out.println(Arrays.toString(tags));
        return tags;
    }

    //TODO: Suyash
    public void record(View view){
        //Bluetooth async task
        //on post-execute:
        int[] buffer = new int[400]; //get from arduino

        try {
            mmOutputStream.write('0');
            System.out.println("wrote");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("caught");
        }

        System.out.println("listening from record");
        beginListenForData();
    }


    public int[] parseData(String data) {
        String[] parsed =data.split("\\s*,\\s*");

        int[] ret = new int[parsed.length];
        for(int j = 0; j < ret.length; j++) {
            ret[j] = Integer.parseInt(parsed[j]);
        }

        System.out.println("parseData result:");
        System.out.println(Arrays.toString(ret));
        return ret;
    }

   public void calculateAndSend(int[] buffer, String[] tags){
        double k = 2.7470; //calibration constant
        FVC = 0;
        FEV = 0;
        List<Double> flowRates = new ArrayList<Double>();
        for (int i = 0; i < 400; i++) {
            double voltage = ((double) buffer[i]) * 5 / 1023;
            double flowRate = voltage * k;
            flowRates.add(flowRate);
            FVC += (flowRate * 0.02);  //sum(Qdt)
            if (i < 50) {            //first second
                FEV += (flowRate * 0.02);
            }
        }

       int dateInt = (int) (System.currentTimeMillis()/1000L);
       date=Integer.toString(dateInt);

        //TODO: Suyash
        //compile string of all necessary data (flowRates, FVC, FEV, ratio, tags)
       SpiroData currentDataObject = new SpiroData();
       currentDataObject.FEV = FEV;
       currentDataObject.FVC = FVC;
       currentDataObject.data = flowRates;
       currentDataObject.date = date;
       List <String> tagList = new ArrayList<String>();
       for (String tag: tags){
          tagList.add(tag);
       }
        currentDataObject.tags = tagList;



       System.out.println("flowrates");
       System.out.println(flowRates.toString());

       System.out.println("FEV");
        String fevString = Double.toString(FEV);
        System.out.println(fevString);

       System.out.println("FVC");
        String fvcString = Double.toString(FVC);
        System.out.println(fvcString);
        System.out.println(Arrays.toString(tags));

       //TODO: Suyash change content to actual thing
        String data = "blabla";
       content = currentDataObject.toJSONString();
       System.out.println("JSON POST String: "+content);
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
            try {
                closeBT();
            }
            catch (IOException ex) { }
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
        intent.putExtra("FEV", FEV);
        intent.putExtra("FVC", FVC);
        startActivity(intent);
    }

}
