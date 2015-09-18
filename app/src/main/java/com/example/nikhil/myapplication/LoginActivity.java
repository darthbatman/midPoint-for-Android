package com.example.nikhil.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.net.URISyntaxException;

//import io.socket.*;
import io.socket.client.IO;
import io.socket.client.Socket;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button mLoginButton = (Button) findViewById(R.id.button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Actually do stuff
                // TODO This is just a dummy login thing
                Intent intent = new Intent(getApplicationContext(), AddLocations.class);
                startActivity(intent);

            }
        });


        // Connect to server
        Socket mSocket;

        try {
            mSocket = IO.socket("http://mytest-darthbatman.rhcloud.com");
//            mSocket = IO.socket("http://chat.socket.io");
            mSocket.connect();

            // auto log in
//        mSocket.emit("add user", "Alias Parker");

        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }



    } // end of onCreate();



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
