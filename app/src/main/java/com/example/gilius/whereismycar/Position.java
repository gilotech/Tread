package com.example.gilius.whereismycar;

/**
 * Created by gilius on 11/9/14.
 */
public class Position {
    float _longitude;
    float _latitude;
    public Position(float longitude, float latitude ){
        _longitude = longitude;
        _latitude = latitude;
    }

    public float getLongitude() {
        return _longitude;
    }
    public float getLatitude(){
        return _latitude;
    }
}
