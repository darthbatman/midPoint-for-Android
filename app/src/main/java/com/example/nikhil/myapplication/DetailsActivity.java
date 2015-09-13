package com.example.nikhil.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends ActionBarActivity {

    String rawJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView test = (TextView) findViewById(R.id.test);

        // get the JSON data passed through a bundle
        try {

            rawJSON = getIntent().getExtras().getBundle("jsonBundle").getString("rawJSON");
            test.setText(rawJSON);

            try {
                parseAndDisplayJson(rawJSON);
            }
            catch (JSONException e) {
                e.printStackTrace();
            } // end of JSONException try/catch

        }

        catch (NullPointerException e) {

            e.printStackTrace();
            Toast.makeText(this, "NullPointerException with JSON", Toast.LENGTH_SHORT).show();

        } // end of NullPointerException try/catch



    } // end of onCreate



    // Parses the JSON and displays it on the screen
    private void parseAndDisplayJson(String json) throws JSONException {

        JSONObject root = new JSONObject(json);
        JSONArray jsonArray = root.getJSONArray("results");
        JSONObject firstResult = jsonArray.getJSONObject(0);
        String name = firstResult.getString("name");

        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();



    } // end of parseAndDisplayJSON





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);

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
}