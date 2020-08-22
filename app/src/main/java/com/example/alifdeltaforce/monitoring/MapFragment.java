package com.example.alifdeltaforce.monitoring;



import android.Manifest;
import android.content.pm.PackageManager;
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
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
//        LocationListener,
////        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
         {


    public MapFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private GoogleMap mMap;

    String LotitleN;
    String LotitleID;
    double LatF;
    double LongF;

    double GEOL;
    double GEOLO;
    private GoogleApiClient googleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addp, container, false);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        initGMapsM();
        createGoogleApi();
    }

    private void initGMapsM() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map11);
        mapFragment.getMapAsync(this);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);

            }

        }
    }

    private static final String TAG = MapFragment.class.getSimpleName();

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition());
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        String PaID = user.getUid();
        databaseReference.child("PATIENT").orderByChild("UserID").equalTo(PaID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PatientInformation patientInformation = postSnapshot.getValue(PatientInformation.class);

                    LotitleID = patientInformation.Pid;
                    LotitleN = patientInformation.Pname;
                    LatF = patientInformation.lat;
                    LongF = patientInformation.lon;
                    GEOL = patientInformation.Geo_lat;
                    GEOLO = patientInformation.Geo_lon;

                    LatLng LLG = new LatLng(GEOL, GEOLO);
                    markerForGeofenceM(LLG);

                    LatLng location = new LatLng(LatF, LongF);
                    mMap.addMarker(new MarkerOptions().position(location)
                            .title(LotitleID)
                            .snippet(LotitleN))
                            .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_m));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        });

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
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
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

    private Marker geoFenceMarker;

    private void markerForGeofenceM(LatLng LLG) {
        Log.i(TAG, "markerForGeofence(" + LLG + ")");
        String title = LLG.latitude + ", " + LLG.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(LLG)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_ttt))
                .title("" + LotitleN);
        geoFenceMarker = mMap.addMarker(markerOptions);


    }

}