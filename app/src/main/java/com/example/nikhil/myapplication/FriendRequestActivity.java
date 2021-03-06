package com.example.nikhil.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FriendRequestActivity extends ActionBarActivity {

    Socket mSocket;

    // lsitview stuff
    ListView friendRequestLV;
    ArrayList<String> friendRequests;
    ArrayAdapter<String> mAdapter;

    // sharedprefs
    SharedPreferences prefs;
    final String FILENAME = "aguero";
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        // initialize stuff
        friendRequestLV = (ListView) findViewById(R.id.friendRequestLV);
        friendRequests = new ArrayList<String>();


        // Change the cheeky color of the action bar
        ActionBar barz = getSupportActionBar();
        barz.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196F3")));



        // sharedprefs
        prefs = getSharedPreferences(FILENAME, 0);
        username = prefs.getString("username", "ERROR");



        try {
            connectToServer();
            extractFriendRequests();
            displayMessages();

        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // listener to accept and decline requests
        setupListviewListener();

    }


    // connect to server
    private void connectToServer() throws URISyntaxException {
        mSocket = IO.socket("http://mytest-darthbatman.rhcloud.com");
        mSocket.connect();
    }


    // collect the data
    private void extractFriendRequests() {

        // ask for messages
        mSocket.emit("check messages", username);

        // listen for a response
        mSocket.on("new messages", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONArray arr = (JSONArray) args[0];

                try {
                    for (int i = 0; i < arr.length(); i++) {
                        Log.d("FriendRequestAct", arr.getString(i));

                        // copy the JSONARRAY into the arraylist
                        friendRequests.add(arr.getString(i));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }); // end of listener


    } // end of extract friend requests


    // set up onclick listener for listview
    private void setupListviewListener() {

        friendRequestLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final int pos = position;

                // build an alert dialog to handle the stuff
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendRequestActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

                builder.setTitle("What do you want to do?");

                // accept the request
                builder.setPositiveButton("Accept Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // who sent the request
                        String theirName = friendRequests.get(pos);
                        theirName = theirName.replace(" would like to be friends.", ""); // delete that extra stuff

//                        Toast.makeText(getApplicationContext(), theirName, Toast.LENGTH_SHORT).show();
                        Log.d("Accepted request from ", theirName);

                        // emit the accept request event
                        mSocket.emit("accept friend request", username, theirName);

                        // delete the message
                        mSocket.emit("rm msg", username, pos);

                        // update the arraylist
                        friendRequests.remove(pos);
                        mAdapter.notifyDataSetChanged();

                    }
                }); // end of onclick listener

                // delete the request
                builder.setNegativeButton("Delete Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // delete the message
                        mSocket.emit("rm msg", username, pos);

                        // update the arraylist
                        friendRequests.remove(pos);
                        mAdapter.notifyDataSetChanged();

                    }
                }); // end of onclick listener


                builder.create();
                builder.show();

            }

        }); // end of listener

    } // end of setupListviewListener



    // display the stuff
    private void displayMessages() {

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendRequests);
        friendRequestLV.setAdapter(mAdapter);

    }



}
