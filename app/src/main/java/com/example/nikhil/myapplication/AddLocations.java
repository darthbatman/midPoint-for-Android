package com.example.nikhil.myapplication;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;


public class AddLocations extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {




    // References to GUI objects declared in XML
    EditText addLocationET;
    EditText typeET;

    ImageView addToListViewButton;
    Button submitButton;

    ListView locationsListView;

    double currentInputLat; // what the user just entered, but geocoded
    double currentInputLong; // these are passed to through the sharedPrefs


    LatLng currentLocation;


    // Some variables that may be needed in many functions
    String userLocationInput;   // what the user enters in addLocationET
    ArrayList userLocationInputArray = new ArrayList(); // array to hold all String inputs
    String typeOfPlaceInput; // what the user enters in typeET;

    // Bundle to transfer info - screw the SharedPreferences
    Bundle infoTransferBundle;
    ArrayList<Double> infoTransferArray;

    private GoogleApiClient mGoogleApiClient;

    PlacesListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_locations);

        // connect the Google Places SDK here
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(0xff00838F));

        // Initialize the references to GUI objects
        addLocationET = (EditText) findViewById(R.id.addressEditText);
        typeET = (EditText) findViewById(R.id.typeEditText);
        addToListViewButton = (ImageView) findViewById(R.id.addButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        locationsListView = (ListView) findViewById(R.id.listView);

        userLocationInput = ""; // make sure its not null
//        userLocationInputArray.add(""); // make sure its not null to avoid crashes
        addLocationET.setText(""); // make sure its not null for the same reasons


        infoTransferBundle = new Bundle();
        infoTransferArray = new ArrayList<>();

        adapter = new PlacesListViewAdapter(this, userLocationInputArray);
        locationsListView.setAdapter(adapter);


        // set the onclick listener so that if a user clicks on an item in teh lsitview, they can delete it
        // doot doot
        // thanks mr nikhil
        // np
        locationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddLocations.this, AlertDialog.THEME_HOLO_DARK);

                builder.setTitle("Delete?");
                builder.setMessage("Are you sure you want to delete " + userLocationInputArray.get(position) + "?");

                builder.setPositiveButton("Ok", new AlertDialog.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        // Update the data
                        userLocationInputArray.remove(position); // trouble spot?

                        // todo get rid of this debugging stuff
                        Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();


                        // todo end of debugging stuff

//                        TESTDEBUGgeocode();

                        // update the display
                        adapter.notifyDataSetChanged();


                    } // end of onCLick

                }); // end of setPositive button

                builder.setNegativeButton("No", null);

                builder.show();

            } // end of onItemCLick

        }); // end of setonitemclicklistener



////no longer required
//        // get user's location
//        getUserLocation();
    }



    private LatLng getUserLocation(){

        String towers;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        towers = lm.getBestProvider(criteria, false);

        Location userLocation = lm.getLastKnownLocation(towers);

        if (userLocation != null) {

            String lat = Double.toString(userLocation.getLatitude());
            String longd = Double.toString(userLocation.getLongitude());

            Log.d("User's Location: ", "Lat: " + lat + " Long: " + longd);

            return new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

        }

        else
            Log.d("User Location", "Failed to get user's location");

        return null;

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    // Clears the listview
    public void onClearButtonClick(View view) {

        userLocationInputArray.clear();

        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userLocationInputArray);
        locationsListView.setAdapter(adapter);

    }



    // Button listener for the "addToListViewButton"
    public void onAddButtonClick(View view) {

        // 1. Get the user's input
        userLocationInput = addLocationET.getText().toString();
        userLocationInputArray.add(userLocationInput);

        // Create an ArrayAdapter to convert array into ListView & attach it to the ListView

//  ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_places, userLocationInputArray);

        // updoot
        adapter.notifyDataSetChanged();


        // Prevents crashes (sike nah)
        if (!(userLocationInput.equals("")) || (userLocationInput != null)) {


            // For some reason, the fact that I was geocoding in an AsyncTask made the app crash.
            // I had to move it back onto the main thread (unfortunately).
            // It would have been more efficient running in the background.
            // Maybe later I can get it to stop crashing on the background thread.

            // Access the GeocoderTask to geocode
//             GeocodeTask geocoderTask = new GeocodeTask();
//             geocoderTask.execute();

//            TESTDEBUGgeocode();

        }
        // clear the EditText
        addLocationET.setText("");
    }



    public LatLng TESTDEBUGgeocode (String geocodeThis) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        if (!(geocodeThis.equals("")) && geocodeThis != null) {

            try {

                try {
                    // List of addresses, we only use 1
                    List<Address> addressList = geocoder.getFromLocationName(geocodeThis, 1);

                    // if u dont not have results
                    if (addressList != null) {

//                        Toast.makeText(getApplicationContext(), "AddressList not null", Toast.LENGTH_LONG).show();

                        // Get the first (and only) address in this list
                        Address address = addressList.get(0);

                        // If you don't not have results (ok)
                        if (address != null) {

                            Log.d("GeocoderTask: ", "Address is not null");

                            // Get the latitude and longitude from the address object
                            double DEBUGextraVariablesSuckLat = address.getLatitude();
                            double DEBUGextraVariablesSuckLong = address.getLongitude();

                            Log.i("Lat/Lng of Geocoded: ", Double.toString(DEBUGextraVariablesSuckLat) + " " + Double.toString(DEBUGextraVariablesSuckLong));

                            return new LatLng(DEBUGextraVariablesSuckLat, DEBUGextraVariablesSuckLong);

//                            // add that cheese to the array
//                            infoTransferArray.add(DEBUGextraVariablesSuckLat);
//                            infoTransferArray.add(DEBUGextraVariablesSuckLong);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            catch (RuntimeException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "No results found for that location!", Toast.LENGTH_LONG).show();
            }
        }

        return null;
    }



    // Submit button event handler
    public void onSubmitButtonClick(View view) {

        // check if user inputted anything for the type of place they want to visit
        if (typeET.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter a type of place you want to visit!", Toast.LENGTH_SHORT).show();
            return;
        }


        // get the user's input for what type of place they want to visit (restaurant, etc)
        typeOfPlaceInput = typeET.getText().toString();

        ///////////////
        Log.i("UserLocaiotnArr", Integer.toString(userLocationInputArray.size()));

        for (int i = 0; i < userLocationInputArray.size(); i++) {
            Log.i("\n\n\nContent of Useray", (String) userLocationInputArray.get(i));
        }

        ///////////////

        Intent intent = new Intent(this, MapsActivity.class);

        if (infoTransferArray != null) {
            // Turn the ArrayList of coordinates we stored everything in to an Array.
            double[] doubleArray = new double[infoTransferArray.size()];


            for (int i = 0; i < infoTransferArray.size(); i++) {
                doubleArray[i] = infoTransferArray.get(i);
            }

            // testing stuff below
            ArrayList<Double> coordinateArrayList = new ArrayList<Double>();

            for (int i = 0; i < userLocationInputArray.size(); i++) {
                // iterate through all places in userLocationInputArray
                // geocode them
                // store them in a double array
                // pass that cheese

                String place = (String) userLocationInputArray.get(i);

                if (place.equals("Your location")) {
                    // add users current location
                    coordinateArrayList.add(currentLocation.latitude);
                    coordinateArrayList.add(currentLocation.longitude);

                }

                else {
                    // geocode their address bruh
                    LatLng latLng = TESTDEBUGgeocode(place);

                    coordinateArrayList.add(latLng.latitude);
                    coordinateArrayList.add(latLng.longitude);
                }

            }

            // convert to array
            double[] doblear = new double[coordinateArrayList.size()];
            for (int i = 0; i < coordinateArrayList.size(); i++) {
                doblear[i] = coordinateArrayList.get(i);
            }



//
//           // Turn the arraylist of names into an array
//           String[] namesArray = new String[userLocationInputArray.size()];
//
//           for (int i = 0; i < userLocationInputArray.size(); i++) {
//               namesArray[i] = (String) userLocationInputArray.get(i);
//               Log.d("Places Entered: ", namesArray[i]);
//           }


//            infoTransferBundle.putDoubleArray("positions", doubleArray);
            infoTransferBundle.putDoubleArray("positions", doblear);
            infoTransferBundle.putStringArrayList("place_names", userLocationInputArray);
            infoTransferBundle.putString("type", typeOfPlaceInput);

            intent.putExtra("bundle", infoTransferBundle);
        }
        startActivity(intent);

    }



    // Your Location button handler, adds your location to the arraylist
    public void onYourLocationButtonClick(View view) {

        currentLocation = getUserLocation();
        userLocationInputArray.add("Your location");
        adapter.notifyDataSetChanged();

    }


    // Menu item click handler
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // show the mof's profile overview
        if (id == R.id.action_viewprofile) {
            Intent intent = new Intent (this, UserProfileOverviewActivity.class);
            startActivity(intent);
        }

        // log that billa out like a log
        else if (id == R.id.action_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



    // create the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_locations, menu);
        return true;
    }


    // Connection failed while trying to connect to the Google Places Api
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // connected to the Google Places Api
    @Override
    public void onConnected(Bundle bundle) {

    }

    // connection suspended to the google places api
    @Override
    public void onConnectionSuspended(int i) {

    }


    // Geocoding AsyncTask
    public class GeocodeTask extends AsyncTask<Void, Void, Void> {

        double extraVariablesSuckLat;
        double extraVariablesSuckLong;

        @Override
        protected Void doInBackground(Void... params) {

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            if (!(userLocationInput.equals("")) && userLocationInput != null) {

                try {

                    try {
                        // List of addresses, we only use 1
                        List<Address> addressList = geocoder.getFromLocationName(userLocationInput, 1);

                        // if u dont not have results
                        if (addressList != null) {

                            Toast.makeText(getApplicationContext(), "AddressList not null", Toast.LENGTH_LONG).show();

                            // Get the first (and only) address in this list
                            Address address = addressList.get(0);

                            // If you don't not have results (ok)
                            if (address != null) {

                                Log.d("GeocoderTask: ", "Address is not null");

                                // Get the latitude and longitude from the address object
                                extraVariablesSuckLat = address.getLatitude();
                                extraVariablesSuckLong = address.getLongitude();

                                Log.i("Lat/Lng of Geocoded: ", Double.toString(extraVariablesSuckLat) + " " + Double.toString(extraVariablesSuckLong));

                                // add that cheese to the array
                                infoTransferArray.add(extraVariablesSuckLat);
                                infoTransferArray.add(extraVariablesSuckLong);
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                catch (RuntimeException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "No results found for that location!", Toast.LENGTH_LONG).show();
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {

            // I could maybe probably delete this...

            currentInputLat = extraVariablesSuckLat;
            currentInputLong = extraVariablesSuckLong;


        }
    }

}
