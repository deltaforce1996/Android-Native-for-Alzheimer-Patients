package com.example.alifdeltaforce.monitoring;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class register extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "register";

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextUsername;
    private Button SingupButton;
    private TextView TextViewlink;

    private ProgressDialog progressDialog;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    String userID;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextUsername = (EditText) findViewById(R.id.editTextUser);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        SingupButton = (Button) findViewById(R.id.sign_up_button);
        TextViewlink = (TextView)findViewById(R.id.textView2);

        SingupButton.setOnClickListener(this);
        TextViewlink.setOnClickListener(this);
    }

    private void registerUser(){
        final String userN = editTextUsername.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password  = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(userN)){
            Toast.makeText(this,"Please enter username",Toast.LENGTH_LONG).show();
            return;
        }else if (!userN.matches("[a-zA-Z ]+")) {

            editTextUsername.requestFocus();
            editTextUsername.setError("PNAME NOT NUMBER");

            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Creating Please Wait...");
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            Toast.makeText(register.this,"CREATE ACCOUNT SUCCESSFUL",Toast.LENGTH_LONG).show();
//                            การส่งข้อมูลข้าม Activity
//                            Intent i = new Intent(register.this,Main2Activity.class);
//                            i.putExtra("username",userN);
//                            i.putExtra("email",email);
//                            startActivity(i);
                            SaveIFT();

                        }else{
                            //display some message here
                            Toast.makeText(register.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                }
                });

    }

    private void SaveIFT(){
        String userN = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        FirebaseUser userid = firebaseAuth.getCurrentUser();
        Userinformation userinformation = new Userinformation(userN,email);


        databaseReference.child("USRE").child(userid.getUid()).setValue(userinformation).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Intent i = new Intent(register.this ,MainActivity.class);
                    startActivity(i);
                    Toast.makeText(register.this,"WELCOME TO MONITERING",Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(register.this, "Fail", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view==SingupButton){
            registerUser();

        }

        if (view==TextViewlink) {
            Intent i = new Intent(getApplicationContext(), login.class);
            startActivity(i);

        }
    }
}
