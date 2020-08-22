package com.example.alifdeltaforce.monitoring;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SandLat extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        ResultCallback<Status> {

    private TextView latiP;
    private TextView longiP;

    private GoogleMap map;
    private GoogleApiClient googleApiClient;

    private static final String TAG = SandLat.class.getSimpleName();

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    static final int REQUEST_LOCATION = 1;

    String PPid;
    String PPname;
    String PPage;
    String PPbith;
    String PUserID;
    double PGeo_lat;
    double PGeo_lon;
    double lat;
    double lon;
    String PTime;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sand_lat);

        PPid = getIntent().getExtras().getString("EXTRA_SESSION_ID");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        latiP = (TextView) findViewById(R.id.latP);
        longiP = (TextView) findViewById(R.id.lonP);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            createGoogleApi();
            mapshow();
            ReadGEO();
            Readdrdr();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:

                break;
        }
    }

    private Marker locationMarker;

    public void PmarkerLocation(LatLng latLng) {
        Log.i(TAG, "markerLocation(" + latLng + ")");

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_m))
                .title("Name : " + PPname);
        if (map != null) {

            if (locationMarker != null)
                locationMarker.remove();
            locationMarker = map.addMarker(markerOptions);
        }
    }

    private void mapshow() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void Readdrdr() {

        databaseReference.child("PATIENT").orderByChild("Pid").equalTo(PPid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    PatientInformation patientInformation = postSnapshot.getValue(PatientInformation.class);

                    PPname = patientInformation.Pname;
                    PPage = patientInformation.Page;
                    PPbith = patientInformation.Pbith;
                    PUserID = patientInformation.UserID;
                    PGeo_lat = patientInformation.Geo_lat;
                    PGeo_lon = patientInformation.Geo_lon;
                    PTime = patientInformation.time;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SandLat.this, "Read Error", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");
        map = googleMap;
        map.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition());
        return false;
    }

    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
        googleApiClient = new GoogleApiClient.Builder(this)
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
        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
        Log.i(TAG, "disConnected()");
    }

    // GoogleApiClient.ConnectionCallbacks connected
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

    //****************************************************************************************************//
    private Marker geoFenceMarker;

    private void PmarkerForGeofence(LatLng gom) {
        Log.i(TAG, "markerForGeofence(" + gom + ")");
        String title = PPid;
        // Define marker options
        MarkerOptions gmarkerOptions = new MarkerOptions()
                .position(gom)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_ttt))
                .title(title);
        float zoom = 17f;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(gom, zoom);
        map.animateCamera(cameraUpdate);

        if (map != null) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = map.addMarker(gmarkerOptions);
            PstartGeofence();
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private float RGEORADIUS; // in meters

    // Create a Geofence
    private Geofence PcreateGeofence(LatLng latLng, float radius) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(GEO_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private GeofencingRequest PcreateGeofenceRequest(Geofence geofence) {
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

        Log.d(TAG, "GeofenceTrasitionService");

        Intent intent = new Intent(this, GeofenceTrasitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void PaddGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }else {
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                request,
                createGeofencePendingIntent()
        ).setResultCallback(this);
        }

    }

    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            PdrawGeofence();
        } else {
            // inform about fail
        }
    }

    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;
    private void PdrawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center( geoFenceMarker.getPosition())
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( RGEORADIUS );
        geoFenceLimits = map.addCircle( circleOptions );
    }


    // Start Geofence creation process
    private void PstartGeofence() {
        Log.i(TAG, "startGeofence()");
        if( geoFenceMarker != null ) {
            Geofence geofence = PcreateGeofence( geoFenceMarker.getPosition(), RGEORADIUS );
            GeofencingRequest geofenceRequest = PcreateGeofenceRequest( geofence );
            PaddGeofence( geofenceRequest );
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }

////***********************************************************************************///
    public void Savelocation(){

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date currentTime = Calendar.getInstance().getTime();
        String time = df.format(currentTime);

        String Pid = PPid;

        databaseReference.child("PATIENT").child(Pid).child("lat").setValue(lat);
        databaseReference.child("PATIENT").child(Pid).child("lon").setValue(lon);
        databaseReference.child("PATIENT").child(Pid).child("time").setValue(time);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();

        latiP.setText(""+location.getLatitude());
        longiP.setText(""+location.getLatitude());

        PmarkerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        Savelocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void ReadGEO() {

        databaseReference.child("Geofence").orderByChild("PID").equalTo(PPid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                   Geobuliding geobuliding = postSnapshot.getValue(Geobuliding.class);

                    double GGeolat = geobuliding.Geolat;
                    double GGlon = geobuliding.Geolon;
                    RGEORADIUS = geobuliding.Georedius;

                    LatLng gom = new LatLng(GGeolat,GGlon);
                    PmarkerForGeofence(gom);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SandLat.this, "Read Error", Toast.LENGTH_SHORT).show();
            }

        });
    }

}




