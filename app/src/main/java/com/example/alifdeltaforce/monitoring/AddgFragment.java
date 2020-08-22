package com.example.alifdeltaforce.monitoring;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddgFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener, ResultCallback<Status>
 {

    private static final String TAG = AddgFragment.class.getSimpleName();

     private GoogleMap map;
     private GoogleApiClient googleApiClient;
     private Button pushGeo;

     private SeekBar seekBareditG;

     String Pid;
     double Geo_latt;
     double  Geo_lonn;

     private FirebaseDatabase firebaseDatabase;
     private FirebaseAuth firebaseAuth;
     private DatabaseReference databaseReference;


    public AddgFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addp, container, false);
        return inflater.inflate(R.layout.fragment_addg, container, false);


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        seekBareditG = (SeekBar)view.findViewById(R.id.seekEditG);


        Pid = getArguments().getString("Pkey");

        initGMaps();
        createGoogleApi();

        pushGeo=(Button)view.findViewById(R.id.push_geo);
        pushGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGeofence();
                SaveGeo();
                SaveGeoforreadwithpatient();
            }
        });

        seekBareditG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int min = 100;
                seekBareditG.setMax(200);
                float value = min + (i);
               float rediusZ = value;
                GEOFENCE_RADIUS = rediusZ;
//                Toast.makeText(getContext(), ""+rediusZ, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

     private void initGMaps() {
         SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map12);
         mapFragment.getMapAsync(this);
     }

     // Callback called when Map is ready
     @Override
     public void onMapReady(GoogleMap googleMap) {
         Log.d(TAG, "onMapReady()");
         map = googleMap;
         map.setOnMapClickListener(this);
         map.setOnMarkerClickListener(this);
         if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                 PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

             return;
         }
         map.setMyLocationEnabled(true);


     }

     @Override
     public void onMapClick(LatLng latLng) {
         Log.d(TAG, "onMapClick(" + latLng + ")");
         markerForGeofence(latLng);

         Geo_latt =latLng.latitude;
         Geo_lonn = latLng.longitude;

     }


     @Override
     public boolean onMarkerClick(Marker marker) {
         Log.d(TAG, "onMarkerClickListener: " + marker.getPosition());
         return false;
     }

     private void createGoogleApi() {
         Log.d(TAG, "createGoogleApi()");
         if (googleApiClient == null) {
             googleApiClient = new GoogleApiClient.Builder(getContext())
                     .addConnectionCallbacks(this)
                     .addOnConnectionFailedListener(this)
                     .addApi(LocationServices.API)
                     .build();

         }
     }

     @Override
     public void onStart() {
         super.onStart();


         googleApiClient.connect();
         Log.i(TAG, "onStart()");

     }

     @Override
     public void onDestroy() {
         super.onDestroy();


         googleApiClient.disconnect();
         Log.i(TAG, "disConnected()");
     }


     @Override
     public void onConnected(@Nullable Bundle bundle) {
         Log.i(TAG, "onConnected()");
     }


     @Override
     public void onConnectionSuspended(int i) {
         Log.w(TAG, "onConnectionSuspended()");
     }


     @Override
     public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
         Log.w(TAG, "onConnectionFailed()");
     }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

     private Marker geoFenceMarker;

     private void markerForGeofence(LatLng latLng) {
         Log.i(TAG, "markerForGeofence(" + latLng + ")");
         String title = latLng.latitude + ", " + latLng.longitude;
         // Define marker options
         MarkerOptions markerOptions = new MarkerOptions()
                 .position(latLng)
                 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                 .title(title);


         if (map != null) {
             // Remove last geoFenceMarker
             if (geoFenceMarker != null)
                 geoFenceMarker.remove();

             geoFenceMarker = map.addMarker(markerOptions);

         }
     }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private static final long GEO_DURATION = 60 * 60 * 1000;
     private static final String GEOFENCE_REQ_ID = "My Geofence";
     private float GEOFENCE_RADIUS = 100.0f; // in meters

     private Geofence createGeofence(LatLng latLng, float radius) {
         Log.d(TAG, "createGeofence");
         return new Geofence.Builder()
                 .setRequestId(GEOFENCE_REQ_ID)
                 .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                 .setExpirationDuration(GEO_DURATION)
                 .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                         | Geofence.GEOFENCE_TRANSITION_EXIT)
                 .build();
     }

     private GeofencingRequest createGeofenceRequest(Geofence geofence) {
         Log.d(TAG, "createGeofenceRequest");
         return new GeofencingRequest.Builder()
                 .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                 .addGeofence(geofence)
                 .build();
     }

     private PendingIntent geoFencePendingIntent;

     private final int GEOFENCE_REQ_CODE = 0;

     private PendingIntent createGeofencePendingIntent() {
         Log.d(TAG, "createGeofencePendingIntent");
         if (geoFencePendingIntent != null)
             return geoFencePendingIntent;


         Intent intent = new Intent(getContext(), GeoTran.class);
         return PendingIntent.getService(
                 getContext(), GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
     }

     private void addGeofence(GeofencingRequest request) {
         Log.d(TAG, "addGeofence");
         if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

             return;
         }
             LocationServices.GeofencingApi.addGeofences(
                     googleApiClient,
                     request,
                     createGeofencePendingIntent()
             ).setResultCallback(this);
         }


     public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

         if (requestCode == 0) {if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
         }
         }
     }


     public void onResult(@NonNull Status status) {
         Log.i(TAG, "onResult: " + status);
         if ( status.isSuccess() ) {
             drawGeofence();
         } else {
             Toast.makeText(getContext(), "Con't draw Geo fence", Toast.LENGTH_SHORT).show();
         }
     }

     private Circle geoFenceLimits;

     private void drawGeofence() {
         Log.d(TAG, "drawGeofence()");

         if ( geoFenceLimits != null )
             geoFenceLimits.remove();

         CircleOptions circleOptions = new CircleOptions()
                 .center( geoFenceMarker.getPosition())
                 .strokeColor(Color.argb(50, 255, 135, 40))
                 .fillColor( Color.argb(100, 255, 135, 40) )
                 .radius( GEOFENCE_RADIUS );
         geoFenceLimits = map.addCircle( circleOptions );
     }

     private void startGeofence() {
         Log.i(TAG, "startGeofence()");
         if( geoFenceMarker != null ) {
             Geofence geofence = createGeofence( geoFenceMarker.getPosition(), GEOFENCE_RADIUS );
             GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
             addGeofence( geofenceRequest );
         } else {
             Log.e(TAG, "Geofence marker is null");
         }
     }

     private void SaveGeo(){

         databaseReference.child("PATIENT").child(Pid).child("Geo_lat").setValue(Geo_latt);
         databaseReference.child("PATIENT").child(Pid).child("Geo_lon").setValue(Geo_lonn);

     }

     private void SaveGeoforreadwithpatient(){
         FirebaseUser user = firebaseAuth.getCurrentUser();
         databaseReference.child("Geofence").child(Pid).child("PID").setValue(Pid);
         databaseReference.child("Geofence").child(Pid).child("Geolat").setValue(Geo_latt);
         databaseReference.child("Geofence").child(Pid).child("Geolon").setValue(Geo_lonn);
         databaseReference.child("Geofence").child(Pid).child("Georedius").setValue(GEOFENCE_RADIUS);
         databaseReference.child("Geofence").child(Pid).child("User_id").setValue(user.getUid());


     }



 }
