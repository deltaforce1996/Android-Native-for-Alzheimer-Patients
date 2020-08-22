package com.example.alifdeltaforce.monitoring;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientActivity extends AppCompatActivity {

    private Button Connect;
    private EditText LinkAdmin;
    private TextView Link;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    int Round= 0;
    int StatusR = 0;
    public static String session;

    ArrayList<String> TTestArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        Connect = (Button)findViewById(R.id.C_ID);
        LinkAdmin = (EditText)findViewById(R.id.PPID);
        Link = (TextView)findViewById(R.id.link);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        CPCheckPid();
    }

    private void SendDataToAc(){

        session = LinkAdmin.getText().toString().trim();
        if (TextUtils.isEmpty(session)){
//            Toast.makeText(PatientActivity.this, "Test : "+TTestArray , Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Input your paopleID", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int Z = 0; Z < Round ; Z++) {
            if(session.equalsIgnoreCase(TTestArray.get(Z))) {

                Intent intent = new Intent(getBaseContext(), SandLat.class);
                intent.putExtra("EXTRA_SESSION_ID", session);
                startActivity(intent);
                finish();
                StatusR=1;
            }
        }

        if (StatusR<1){
            Toast.makeText(this, "PatientID not match : "+ session, Toast.LENGTH_SHORT).show();
        }


    }

    public void Connection(View view){
        SendDataToAc();
    }

    public void ToMain(View view){

        Intent i = new Intent(getApplicationContext(), login.class);
        startActivity(i);
    }

    public void CPCheckPid(){

        databaseReference.child("PATIENT").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    PatientInformation patientInformation = s.getValue(PatientInformation.class);

                    TTestArray.add(patientInformation.Pid);
                    Round= TTestArray.size() ;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
