package com.example.gilius.whereismycar;

import java.util.ArrayList;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class wimc extends Activity implements
        SensorEventListener {

    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private static final String locationsList = "locationsList";
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView locationsText;
    private TextView positionText;
    private TextView sensorText;
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
        setContentView(R.layout.activity_wimc);


        locationsText = (TextView)this.findViewById(R.id.locationsText);
        positionText = (TextView)this.findViewById(R.id.positionText);
        sensorText = (TextView)this.findViewById(R.id.sensorText);
        //locationsText.setText("");

        //  Initialize the sensor motion analysis
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        //  Initialising the GPS locator
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new myLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);

        Toast.makeText(this, "Motion Tracking launched", Toast.LENGTH_LONG).show();
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
                    int n = locations.size();
                    Toast.makeText(getApplicationContext(), "Saving this location.", Toast.LENGTH_SHORT).show();
                    if(n == 1){
                        locationsText.setText("1 LOCATION SAVED.");
                    }
                    else if(n > 1){
                        locationsText.setText(n + " LOCATIONS SAVED.");
                    }
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
        Sensor mySensor = event.sensor;
        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            sensorText.setText("X: " + x + "\nY: " + y + "\nZ: " + z);
            long curTime = System.currentTimeMillis();
            long diffTime = (curTime - lastUpdate);
            if(diffTime > 500){
                lastUpdate = curTime;
                if( ( Math.abs(y) > 5 )){ //    The person is standing after being seated
                    if(seatingTime > 0) {
                        if(seatingTime > 20)
                            locationSaved = false;
                        seatingTime = 0;
                    }
                    positionText.setText("STANDING");
                    double dx = last_x - x;
                    double dy = last_y - y;
                    if(Math.abs(dx) > 1) { // The person is in motion
                        nSteps++;
                        positionText.setText("WALKING");
                    }
                }
                else{
                    positionText.setText("SEATING");
                    seatingTime++;
                    if(seatingTime > 20) {
                        nSteps = 0;
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    public void loadMap(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putParcelableArrayListExtra(locationsList, locations);
        startActivity(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    protected void onPause(){
        super.onPause();
        //sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume(){
        super.onPause();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}