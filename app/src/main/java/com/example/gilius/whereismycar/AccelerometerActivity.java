package com.example.gilius.whereismycar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.AsyncTask;
import android.os.Bundle;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class AccelerometerActivity extends Activity implements
        SensorEventListener {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView stepsText;
    private TextView positionText;
    private long lastUpdate = 0;
    private float last_x = 0;
    private float last_y = 0;
    private float last_z = 0;
    private int nSteps = 0;
    private int seatingTime = 0;
    private boolean locationSaved = false;
    private ArrayList<LatLng> locations = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        stepsText = (TextView)findViewById(R.id.stepsText);
        positionText = (TextView)findViewById(R.id.positionText);
        //positionText.setText("");
        stepsText.setText("0");
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new myLocationListener();
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        Toast.makeText(this, "Motion Tracking launched", Toast.LENGTH_LONG);
    }
    class myLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            if(location != null){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng coordinates = new LatLng(latitude, longitude);
                if(nSteps > 5 && !locationSaved) {
                    locations.add(coordinates);
                    locationSaved = true;
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
    @Override
    public void onSensorChanged(SensorEvent event){
        TextView xtext = (TextView)findViewById(R.id.xText);
        TextView ytext = (TextView)findViewById(R.id.yText);
        TextView ztext = (TextView)findViewById(R.id.zText);

        Sensor mySensor = event.sensor;
        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            xtext.setText(x + "");
            ytext.setText(y + "");
            ztext.setText(z + "");

            long curTime = System.currentTimeMillis();
            long diffTime = (curTime - lastUpdate);
            if(diffTime > 500){
                lastUpdate = curTime;
                if((y < (-5) || (y > 5)) && (x > -6 && x < 6)){
                    if(seatingTime > 0) {
                        seatingTime = 0;
                        locationSaved = false;
                    }
                    positionText.setText("Position: Standing");
                    if((last_y < -5 || last_y > 5) && ((x > 0 && last_x < 0) || (x < 0 && last_x > 0))) {
                        nSteps++;
                        positionText.setText("Motion: Walking");
                    }

                }
                else{
                    positionText.setText("Position: Seating");
                    seatingTime++;
                    if(seatingTime > 20) {
                        nSteps = 0;

                    }
                }

                stepsText.setText(nSteps+" steps");
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume(){
        super.onPause();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
