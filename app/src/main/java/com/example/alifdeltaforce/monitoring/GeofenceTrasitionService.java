package com.example.alifdeltaforce.monitoring;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import static com.google.android.gms.common.zzo.getErrorString;

public class GeofenceTrasitionService extends IntentService {

    private static final String TAG = GeofenceTrasitionService.class.getSimpleName();

    String PPPID;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    double ShowStatus = 0;
    public GeofenceTrasitionService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
         PPPID = PatientActivity.session;
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Handling errors
        if ( geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }

        // Retrieve GeofenceTrasition
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            showNotification();
            ShowStatus = 2;
            databaseReference.child("Alert").child(PPPID).child("ShowStatus").setValue(ShowStatus);
            databaseReference.child("Alert").child(PPPID).child("value").push().setValue(ShowStatus);
        }
    }
    public void showNotification() {
        int i=1;

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification =
                new NotificationCompat.Builder(this) // this is context
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Alert Exit")
                        .setContentText("Nontifacation :( Exit")
                        .setSound(soundUri)
                        .setAutoCancel(true)
                        .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(i++, notification);

    }


}