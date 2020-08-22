package com.example.alifdeltaforce.monitoring;

/**
 * Created by Alif Delta Force on 11/21/2017.
 */

public class Geobuliding {

    public String PID;
    public double Geolat;
    public  double Geolon;
    public  float Georedius;


    public Geobuliding(){

    }

    public Geobuliding(String PID, double geolat, double geolon, float georedius) {
        this.PID = PID;
        this.Geolat = geolat;
        this.Geolon = geolon;
        this.Georedius = georedius;

    }
}
