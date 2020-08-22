package com.example.alifdeltaforce.monitoring;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    double ddd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        ReadFirseForAlert();


        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, login.class));
        }

    }
    //เพิ่ม logout
    private void signOut() {
        firebaseAuth.signOut();
        finish();
        //starting login activity
        startActivity(new Intent(this, login.class));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout){
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {

            //คำสั่งสำหรับไสลหน้า
            setTitle("Map");
            MapFragment mapFragment = new MapFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment, mapFragment).commit();

        } else if (id == R.id.nav_addp) {

            setTitle("Add Patient");
            AddpFragment addpFragment = new AddpFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment, addpFragment).commit();

        } else if (id == R.id.nav_addg) {

            setTitle("Manage Geo");
            EditGeoFragment editGeoFragment = new EditGeoFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment, editGeoFragment).commit();

                //เพิ่ม logout
        } else if (id == R.id.nav_logout) {

            signOut();
                   //end
        } else if (id == R.id.nav_dep) {

            setTitle("Detail Patient");
            DetailpaFragment detailpaFragment = new DetailpaFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment, detailpaFragment).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

       private void ReadFirseForAlert(){
           FirebaseUser user = firebaseAuth.getCurrentUser();

           databaseReference.child("Alert").orderByChild("UUsereID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                       Coodinate coodinate = postSnapshot.getValue(Coodinate.class);
                       ddd = coodinate.ShowStatus;

                       if (ddd != 0){
                           showNotification();
                       }
                   }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {
                   Toast.makeText(MainActivity.this, "Read Error", Toast.LENGTH_SHORT).show();
               }

           });

       }


    public void showNotification() {
           int i=0;
           int count = 1;

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date currentTime = Calendar.getInstance().getTime();
        String time = df.format(currentTime);


        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                        mBuilder.setContentTitle("Alert Exit : "+time );
                        mBuilder.setContentText("Nontifacation :( Exit");
                        mBuilder.setSound(soundUri);
//                        .setPriority(Notification.PRIORITY_MAX)

                        mBuilder.setAutoCancel(true);
//                        .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(i++, mBuilder.build());

    }




}
