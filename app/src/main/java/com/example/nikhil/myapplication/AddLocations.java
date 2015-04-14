package com.example.nikhil.myapplication;import android.content.Intent;import android.content.SharedPreferences;import android.location.Address;import android.location.Geocoder;import android.os.AsyncTask;import android.support.v7.app.ActionBarActivity;import android.os.Bundle;import android.util.Log;import android.view.Menu;import android.view.MenuItem;import android.view.View;import android.widget.ArrayAdapter;import android.widget.EditText;import android.widget.Button;import android.widget.ListAdapter;import android.widget.ListView;import android.widget.Toast;import org.json.JSONArray;import org.json.JSONException;import org.json.JSONObject;import java.io.BufferedReader;import java.io.IOException;import java.io.InputStream;import java.io.InputStreamReader;import java.net.HttpURLConnection;import java.net.URL;import java.util.ArrayList;import java.util.HashSet;import java.util.List;import java.util.Locale;import java.util.Set;import com.google.android.gms.maps.model.LatLng;public class AddLocations extends ActionBarActivity {    // SharedPreferences Object to move coordinates from Activity to Activity    SharedPreferences sharedPreferences;    SharedPreferences.Editor sharedPrefsEditor;    // References to GUI objects declared in XML    EditText addLocationET;    EditText typeET;    Button addToListViewButton;    Button submitButton;    ListView locationsListView;    int globalCounter;  // used to generate new keys for the SharedPrefs (CANT ADD ARRAYS!@!@?!?@!?@)    double currentInputLat; // what the user just entered, but geocoded    double currentInputLong; // these are passed to through the sharedPrefs    // Some global (type) variables that may be needed in many functions    String userLocationInput;   // what the user enters in addLocationET    ArrayList userLocationInputArray = new ArrayList(); // array to hold all String inputs    Set<String> passThisThroughSharedPrefs;    // Bundle to transfer info - screw the SharedPreferences    Bundle infoTransferBundle;    ArrayList<Double> infoTransferArray;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_add_locations);        // Initialize the references to GUI objects        addLocationET = (EditText) findViewById(R.id.addressEditText);        typeET = (EditText) findViewById(R.id.typeEditText);        addToListViewButton = (Button) findViewById(R.id.addButton);        submitButton = (Button) findViewById(R.id.submitButton);        locationsListView = (ListView) findViewById(R.id.listView);        userLocationInput = ""; // make sure its not null        // Initialize SharedPreferences & Editor        sharedPreferences = getSharedPreferences("map", MODE_PRIVATE);        sharedPrefsEditor = getSharedPreferences("map", MODE_PRIVATE).edit();        // Clear the memory in the SharedPreferences object        sharedPrefsEditor.clear();        sharedPrefsEditor.apply();        //init        passThisThroughSharedPrefs = new HashSet<>();        globalCounter = 0;        infoTransferBundle = new Bundle();        infoTransferArray = new ArrayList<>();    }    // Button listener for the "addToListViewButton"    // This function does the following:    // 1. Get the user's input    // 2. Put it into the ListView    // 3. Geocode the user's input into a LatLng    // 4. Store said LatLng into a SharedPreferences - this same SharedPrefs will be used to plot the pins on the map later.    public void onAddButtonClick(View view) {        Log.i("Global Counter: ", Integer.toString(globalCounter));        // 1. Get the user's input        userLocationInput = addLocationET.getText().toString();//        // Add to the ArrayList        userLocationInputArray.add(userLocationInput);        // Create an ArrayAdapter to convert array into ListView & attach it to the ListView        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userLocationInputArray);        locationsListView.setAdapter(adapter);        // Access the GeocoderTask to geocode        GeocodeTask geocoderTask = new GeocodeTask();        geocoderTask.execute();        // Now I create the key names - each name must be UNIQUE (done with the counter)        String latKey = "lat" + Integer.toString(globalCounter);        String longKey = "long" + Integer.toString(globalCounter);        Log.i("LatKey: ", latKey);        Log.i("LongKey: ", longKey);        Log.i("Lat/Lng Test [Ignore] ", Double.toString(currentInputLat) + Double.toString(currentInputLong));////        sharedPrefsEditor.putLong(latKey, (long) currentInputLat);//        sharedPrefsEditor.putLong(longKey, (long) currentInputLong);////        sharedPrefsEditor.putString("test", "test");////        sharedPrefsEditor.apply();//////        Log.i("latitude in sharedPrefs", Long.toString(sharedPreferences.getLong(latKey, 696969)));        // Increment the counter for the unique key-gen algorithm (sounds fancier than it is)        globalCounter++;    }    // Submit button event handler   public void onSubmitButtonClick(View view) {       // Turn the ArrayList we stored everything in to an Array.       double[] doubleArray = new double[infoTransferArray.size()];       for (int i = 0; i < infoTransferArray.size(); i++) {           doubleArray[i] = infoTransferArray.get(i);       }       Intent intent = new Intent(this, MapsActivity.class);       infoTransferBundle.putDoubleArray("positions", doubleArray);       intent.putExtra("bundle", infoTransferBundle);       startActivity(intent);   }    // Geocoding AsyncTask    public class GeocodeTask extends AsyncTask<Void, Void, Void> {        double extraVariablesSuckLat;        double extraVariablesSuckLong;        @Override        protected Void doInBackground(Void... params) {            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());            try {                // List of addresses, we only use 1                List<Address> addressList = geocoder.getFromLocationName(userLocationInput, 1);                // Get the first (and only) address in this list                Address address = addressList.get(0);                // Get the latitude and longitude from the address object                extraVariablesSuckLat = address.getLatitude();                extraVariablesSuckLong = address.getLongitude();                Log.i("Lat/Lng of Geocoded: ", Double.toString(extraVariablesSuckLat) + " " + Double.toString(extraVariablesSuckLong));//                // Add the lats and longs to the sharedprefs through here//                String latKey = "lat" + Integer.toString(globalCounter);//                String longKey = "long" + Integer.toString(globalCounter);                String latString = Double.toString(extraVariablesSuckLat);                String longString = Double.toString(extraVariablesSuckLong);                // Add the lats and longs to the Set                passThisThroughSharedPrefs.add(latString);                passThisThroughSharedPrefs.add(longString);                ArrayList<String> arrayList = new ArrayList<String>();                arrayList.add("Hello");                sharedPrefsEditor.putStringSet("mapKey", passThisThroughSharedPrefs);                sharedPrefsEditor.putLong("lat0", (long) extraVariablesSuckLat);                sharedPrefsEditor.putLong("long0", (long) extraVariablesSuckLong);                sharedPrefsEditor.putString("test", "test");                sharedPrefsEditor.apply();                //////////////////////////////////////////                // add that cheese to the array                infoTransferArray.add(extraVariablesSuckLat);                infoTransferArray.add(extraVariablesSuckLong);            }            catch (IOException e) {                e.printStackTrace();            }                return null;        }        @Override        protected void onPostExecute(Void aVoid) {            currentInputLat = extraVariablesSuckLat;            currentInputLong = extraVariablesSuckLong;        }    }}