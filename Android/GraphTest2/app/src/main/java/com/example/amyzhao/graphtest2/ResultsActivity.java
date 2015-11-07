package com.example.amyzhao.graphtest2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //String[] tags = new String[] {"tag1", "tag2", "tag3"};
        //Spinner spinner = (Spinner) findViewById(R.id.spinner);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,android.R.layout.simple_spinner_item);
        //// Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //// Apply the adapter to the spinner
        //spinner.setAdapter(adapter);
        //spinner.setPrompt("Select one");
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

    public void toRecord(View view) {
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        startActivity(intent);
    }

    public void toGraph(View view) {
        Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
        startActivity(intent);
    }
}
