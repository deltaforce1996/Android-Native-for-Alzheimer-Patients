package com.example.alifdeltaforce.monitoring;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.security.auth.callback.Callback;

public class Main2Activity extends AppCompatActivity {

//    private TextView EE;
//    private TextView GG;
//    String pnane;
//
//    private FirebaseDatabase firebaseDatabase;
//    private FirebaseAuth firebaseAuth;
//    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);

//        EE = (TextView) findViewById(R.id.textViewUser);
//        GG = (TextView) findViewById(R.id.textViewEmail);
//
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference();
//        SRead();
////        TOM();

    }

//    public void SRead(){
//
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        databaseReference.child("USER").child(user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//
//                    Userinformation userinformation = postSnapshot.getValue(Userinformation.class);
//
//                   pnane = userinformation.email;
////                    EE.setText(Semail);
//                    Toast.makeText(Main2Activity.this, "44", Toast.LENGTH_SHORT).show();
//
//
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(Main2Activity.this, "555555555", Toast.LENGTH_SHORT).show();
//            }
//
//        });
//    }
//
//    public void TOM(){
//
//        EE.setText(pnane);
//    }
////
////    @Override
////    protected void onStart() {
////        super.onStart();
////        Read();
////
////    }

}
