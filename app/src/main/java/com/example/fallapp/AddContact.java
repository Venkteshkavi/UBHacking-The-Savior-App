package com.example.fallapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.OutputStreamWriter;


public class AddContact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Button save = (Button) findViewById(R.id.save_button);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = ((EditText)findViewById(R.id.name) ).getText().toString();
                String email = ((EditText)findViewById(R.id.email) ).getText().toString();
                String phone = ((EditText)findViewById(R.id.phone) ).getText().toString();
                writeToFile(name+","+email+","+phone+",\n",view.getContext());
                finish();
            }

            private void writeToFile(String data, Context context) {
                try {
                    if(data.contains("delete")){
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", MODE_PRIVATE));
                        outputStreamWriter.write("");
                        outputStreamWriter.close();
                        return;
                    }

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", MODE_APPEND));
                    outputStreamWriter.write(data);
                    outputStreamWriter.close();

                }
                catch (IOException e) {

                }
            }
        });

    }



}
