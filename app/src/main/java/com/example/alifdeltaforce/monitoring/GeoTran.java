package com.example.alifdeltaforce.monitoring;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;


/**
 * Created by Alif Delta Force on 11/20/2017.
 */

public class GeoTran extends IntentService {

    private static final String TAG = GeofenceTrasitionService.class.getSimpleName();

    public GeoTran () {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();


        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {

        }
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ){

        }
    }





}
