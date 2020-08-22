package com.example.alifdeltaforce.monitoring;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity implements View.OnClickListener {

    private Button Singuplink;
    private Button singinButton;
    private ProgressDialog progressDialog;
    private EditText Email;
    private EditText Password;
    private FirebaseAuth firebaseAuth;
    private Button join;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        Singuplink = (Button)findViewById(R.id.btn_signup);
        singinButton =(Button)findViewById(R.id.btn_login);
        join = (Button)findViewById(R.id.JoinB);
        Email = (EditText)findViewById(R.id.editTextEmail);
        Password = (EditText)findViewById(R.id.editTextpassword);

        progressDialog = new ProgressDialog(this);

        Singuplink.setOnClickListener(this);
        singinButton.setOnClickListener(this);
        join.setOnClickListener(this);

        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    public  void userlogin(String email, String password ){

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Check Please Wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);

                        }else {
                            Toast.makeText(login.this,"Login Error",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view==singinButton){
            userlogin(Email.getText().toString(), Password.getText().toString());
        }
        if (view==Singuplink) {
            Intent i = new Intent(getApplicationContext(), register.class);
            startActivity(i);
        }
        if (view==join){
            Intent i = new Intent(getApplicationContext(), PatientActivity.class);
            startActivity(i);
        }
    }
}
