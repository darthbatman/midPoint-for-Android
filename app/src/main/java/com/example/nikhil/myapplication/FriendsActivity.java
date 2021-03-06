package com.example.nikhil.myapplication;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FriendsActivity extends ActionBarActivity {

    Socket mSocket;

    // friends
    ListView friendsListView;
    ArrayList<String> friends;
    Set<String> friendsSet;
    ArrayAdapter<String> adapter;

    // sharedpreferences stuff
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    final String FILENAME = "aguero";

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

//        // sharedprefs
//        sharedPrefs = getSharedPreferences(FILENAME, 0);
//        username = sharedPrefs.getString("username", "ERROR");
//
//        // listview stuff
//        friendsListView = (ListView) findViewById(R.id.friendLV);
//
//        friendsSet = new HashSet<>();
//        friends = new ArrayList<String>();
//
//
//        adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, friends);
//
//        friendsListView.setAdapter(adapter);
//
//        // Change the cheeky color of the action bar
//        ActionBar barz = getSupportActionBar();
//        barz.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196F3")));
//
//        // CONNECT TO SERVER
//        try {
//            mSocket = IO.socket("http://mytest-darthbatman.rhcloud.com");
//            mSocket.connect();
//
////            Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
//
//            extractFriends();
//            adapter.notifyDataSetChanged();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // sharedprefs
        sharedPrefs = getSharedPreferences(FILENAME, 0);
        username = sharedPrefs.getString("username", "ERROR");

        // listview stuff
        friendsListView = (ListView) findViewById(R.id.friendLV);

        friendsSet = new HashSet<>();
        friends = new ArrayList<String>();


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, friends);

        friendsListView.setAdapter(adapter);

        // Change the cheeky color of the action bar
        ActionBar barz = getSupportActionBar();
        barz.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196F3")));

        // CONNECT TO SERVER
        try {
            mSocket = IO.socket("http://mytest-darthbatman.rhcloud.com");
            mSocket.connect();

//            Toast.makeText(this, username, Toast.LENGTH_SHORT).show();

            extractFriends();
            adapter.notifyDataSetChanged();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    // access the friend data
    private void extractFriends() throws Exception {

        // emit the event that requests friends (sounds pretty sad...)
        mSocket.emit("want friends", username);

        // =) tfw u receive friends
        mSocket.on("some friends", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONArray arr; // pirates!
                try {
                    arr = (JSONArray) args[0];

                    // log da friends
                    for (int i = 0; i < arr.length(); i++) {

                        String o = arr.getString(i);

                        Log.d("FRIENDS(Activity)", o);
                        // ama
                        if (!o.contains("would like to be friends"))
                            friends.add(o);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } // end of call
        }); // end of friend listener

    } // end of extract friends




}
