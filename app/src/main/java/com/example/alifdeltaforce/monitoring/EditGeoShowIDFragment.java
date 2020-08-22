package com.example.alifdeltaforce.monitoring;



import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditGeoShowIDFragment extends Fragment implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>
{
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    String CheckPid;
    private GoogleMap mMap;
    String PPPid;
    double Geo_latt;
    double Geo_lonn;
    float EditRedius;
    String Pid;
    private GoogleApiClient googleApiClient;

    public EditGeoShowIDFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        return v;


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        initGMaps();
        createGoogleApi();


        PPPid = getArguments().getString("Pkey");
            Pid = PPPid;
            ReadRedius();

    }

    private void initGMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map11);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap=googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        } else {
            googleMap.setMyLocationEnabled(true);
        }

        databaseReference.child("PATIENT").orderByChild("Pid").equalTo(PPPid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    PatientInformation patientInformation = s.getValue(PatientInformation.class);

                    CheckPid = patientInformation.Pid;

                    LatLng location = new LatLng(patientInformation.lat, patientInformation.lon);
                    googleMap.addMarker(new MarkerOptions().position(location)
                            .title(patientInformation.Pid)
                            .snippet(patientInformation.Pname))
                            .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_m));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    private static final String TAG = MapFragment.class.getSimpleName();

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


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition());
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick(" + latLng + ")");

        Geo_latt =  latLng.latitude;
        Geo_lonn = latLng.longitude;

        mmarkerForGeofence(latLng);
        sstartGeofence();
        SaveGeo();

    }

    private Marker geoFenceMarker;

    // Create a marker for the geofence creation
    private void mmarkerForGeofence(LatLng latLng) {
        Log.i(TAG, "markerForGeofence(" + latLng + ")");
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        if (mMap != null) {
//            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = mMap.addMarker(markerOptions);
        }
    }

    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";

    private Geofence ccreateGeofence(LatLng latLng, float radius) {
        Log.d(TAG, "ccreateGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(GEO_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private GeofencingRequest ccreateGeofenceRequest(Geofence geofence) {
        Log.d(TAG, "ccreateGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private PendingIntent ccreateGeofencePendingIntent() {
        Log.d(TAG, "ccreateGeofencePendingIntent");
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Log.d(TAG, "GeofenceTrasitionService");

        Intent intent = new Intent(getContext(), GeoTran.class);
        return PendingIntent.getService(
                getContext(), GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void aaddGeofence(GeofencingRequest request) {
        Log.d(TAG, "aaddGeofence");

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        } else {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    ccreateGeofencePendingIntent()
            ).setResultCallback(this);
        }
    }

    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            ddrawGeofence();
        } else {
            Toast.makeText(getContext(), "onResult: Faile", Toast.LENGTH_SHORT).show();
        }
    }

    private Circle geoFenceLimits;

   private void ddrawGeofence() {
        Log.d(TAG, "ddrawGeofence()");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center( geoFenceMarker.getPosition())
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius(  EditRedius );
        geoFenceLimits = mMap.addCircle( circleOptions );
    }

    // Start Geofence creation process
    private void sstartGeofence() {
        Log.i(TAG, "startGeofence()");
        if( geoFenceMarker != null ) {
            Geofence geofence = ccreateGeofence( geoFenceMarker.getPosition(),  EditRedius );
            GeofencingRequest geofenceRequest = ccreateGeofenceRequest( geofence );
            aaddGeofence( geofenceRequest );
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }

    private void SaveGeo(){
        databaseReference.child("PATIENT").child(Pid).child("Geo_lat").setValue(Geo_latt);
        databaseReference.child("PATIENT").child(Pid).child("Geo_lon").setValue(Geo_lonn);
        databaseReference.child("Geofence").child(Pid).child("PID").setValue(PPPid);
        databaseReference.child("Geofence").child(Pid).child("Geolat").setValue(Geo_latt);
        databaseReference.child("Geofence").child(Pid).child("Geolon").setValue(Geo_lonn);

    }

    public void ReadRedius(){
        databaseReference.child("Geofence").orderByChild("PID").equalTo(PPPid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Geobuliding geobuliding = s.getValue(Geobuliding.class);

                    EditRedius = geobuliding.Georedius;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "ReadError", Toast.LENGTH_SHORT).show();
            }
        });
    }


}


