package com.example.nikhil.myapplication;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Set;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    SharedPreferences sharedPreferences;

    double lat;
    double longdirk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpMapIfNeeded();



        sharedPreferences = getSharedPreferences("map", MODE_PRIVATE);


        // testing to see if sharedPrefs is working

        String di = sharedPreferences.getString("test", "no");

//        Toast.makeText(this, di, Toast.LENGTH_SHORT).show();

//
//        lat = (double) sharedPreferences.getLong("lat0", 0);
//        longdirk = (double) sharedPreferences.getLong("long0", 0);
//
//        LatLng test = new LatLng(lat, longdirk);
//
//        mMap.addMarker(new MarkerOptions().position(test));

        Set<String> setUp = sharedPreferences.getStringSet("mapKey", null);

        if (setUp != null) {

            Object[] array = new String[4];
            array = setUp.toArray();

            String firstLat = "69";
            String firstLong = "69";

            firstLat = (String) array[0];
            firstLong = (String) array[1];

            Toast.makeText(this, firstLat + " " + firstLong, Toast.LENGTH_LONG).show();

            double firstLate = Double.parseDouble(firstLat);
            double firstLonge = Double.parseDouble(firstLong);

            LatLng dirk = new LatLng(firstLate, firstLonge);

            mMap.addMarker(new MarkerOptions().position(dirk));

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();



        sharedPreferences = getSharedPreferences("map", MODE_PRIVATE);


        // testing to see if sharedPrefs is working

        String di = sharedPreferences.getString("test", "no");

//        Toast.makeText(this, di, Toast.LENGTH_SHORT).show();

//
//        lat = (double) sharedPreferences.getLong("lat0", 0);
//        longdirk = (double) sharedPreferences.getLong("long0", 0);
//
//        LatLng test = new LatLng(lat, longdirk);
//
//        mMap.addMarker(new MarkerOptions().position(test));

        Set<String> setUp = sharedPreferences.getStringSet("mapKey", null);

        if (setUp != null) {

            Object[] array = new String[4];
            array = setUp.toArray();

            String firstLat = "69";
            String firstLong = "69";

            firstLat = (String) array[0];
            firstLong = (String) array[1];

//            Toast.makeText(this, firstLat + " " + firstLong, Toast.LENGTH_LONG).show();

            double firstLate = Double.parseDouble(firstLat);
            double firstLonge = Double.parseDouble(firstLong);

            LatLng dirk = new LatLng(firstLate, firstLonge);

            mMap.addMarker(new MarkerOptions().position(dirk));

            //////////////////////////////////////////////////////

            /// Get the bundle

            Bundle bundle = getIntent().getBundleExtra("bundle");
            double[] doble = bundle.getDoubleArray("positions");

            for (int i = 0; i < doble.length; i++) {
                Log.d("Doubles passed:", Double.toString(doble[i]));
            }

        }


    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

    }
}
