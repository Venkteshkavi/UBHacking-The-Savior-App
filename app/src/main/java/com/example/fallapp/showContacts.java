package com.example.fallapp;

import android.content.Context;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class showContacts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contacts);

        Button ok = (Button) findViewById(R.id.save_button);
        ReadDataFromFile1();

    }

    private void ReadDataFromFile1(){
        String data="";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("config.txt")));
            String line = reader.readLine();
            Log.i("ReadData",line);
            int i = 0;
            while (line !=null ) {
                data += line;
                line = reader.readLine();
                for (String s : line.toString().split(",")) {
                    String buttonID = "contacts" + i ;
                    int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                    TextView contacts = (TextView) findViewById(resID);
                    contacts.setText(s);
                    i+=1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}

