package com.example.fallapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;




public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    private LocationRequest mLocationRequest;
    String loc;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    flag fg = new flag();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button addContact = (Button) findViewById(R.id.button2);
        Button showContact = (Button) findViewById(R.id.button3);

        fg.setVal(true);

        //Location Service
        startLocationUpdates();


        //Accelerometer Service
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddContact.class);
                startActivity(intent);


            }
        });
        Button sos = (Button) findViewById(R.id.button);
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ToneGenerator ton = new ToneGenerator(AudioManager.STREAM_MUSIC, 400);
                ton.startTone(ToneGenerator.TONE_CDMA_HIGH_SS, 20000);


//                final boolean[] isEmergency = {true};
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("BY MISTAKE ?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "YES ITS AN EMERGENCY",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "NOT AN EMERGENCY",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ton.release();
                                dialog.cancel();
                                fg.setVal(false);
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                new dialogBoxTask().execute();
            }
        });

        showContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, showContacts.class);
                startActivity(intent);
            }
        });


    }

    class dialogBoxTask extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Thread.sleep(3000);
                Thread.sleep(3000);
                Thread.sleep(3000);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (fg.val) {
                String data = ReadDataFromFile();
                Log.e("DATA4",data);
                new sendData().execute(data);
            }
            fg.val = true;
            return null;
        }

        private String ReadDataFromFile(){
            String data="";
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("config.txt")));
                String line = reader.readLine();
                while (line !=null ) {
                    data += line;
                    if(loc != null) {
                        data += loc;
                        data+=",";
                    }
                    line = reader.readLine();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }
    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }


    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());



        loc  = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
        Log.e("location",loc);


        // You can now create a LatLng Object for use with maps

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onSensorChanged(SensorEvent event) {


        final float alpha = 0.8f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        if(linear_acceleration[2] > 20)
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "fall detected",
                    Toast.LENGTH_SHORT);

            toast.show();
            Button button = findViewById(R.id.button);
            button.performClick();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}


class flag {
    public boolean val;
    public void setVal(boolean val) {
        this.val = val;
    }

}

class sendData extends AsyncTask<String,Void,String>{
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

            String data =params[0]; //data to post

            try {

                StringBuilder sbParams = new StringBuilder();
                sbParams.append("&");
                sbParams.append("data=");
                sbParams.append(URLEncoder.encode(data, "UTF-8"));
                Log.e("sending",sbParams.toString());

                URL url = new URL("http://34.68.245.232:3000/sendmessage");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                BufferedOutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(sbParams.toString());
                writer.flush();
                writer.close();
                out.close();
                int responseCode=urlConnection.getResponseCode();
                System.out.println(responseCode);



            } catch (Exception e) {
                System.out.println(e.getMessage());
            }



        return "hello";
    }
}
