package com.example.alifdeltaforce.monitoring;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddpFragment extends Fragment implements View.OnClickListener {

String Pid;
    public AddpFragment() {

    }

    private EditText EIDpatient;
    private EditText ENamepatient;
    private EditText EAgepatient;
    private EditText EBirthDaypatient;
    private Button BSave_Datapatient;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ดังนั้นเจ้าของบล็อกจะใช้ onCreateView(...) เพื่อกำหนด Layout ที่ต้องการ แล้วค่อยไปกำหนด View ที่จะเรียกใช้ใน onViewCreated(...) แทน
        View view = inflater.inflate(R.layout.fragment_addp, container, false);
        return inflater.inflate(R.layout.fragment_addp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EIDpatient = (EditText) view.findViewById(R.id.IDpatient);
        ENamepatient = (EditText) view.findViewById(R.id.Namepatient);
        EAgepatient = (EditText) view.findViewById(R.id.Agepatient);
        EBirthDaypatient = (EditText) view.findViewById(R.id.BirthDaypatient);
        BSave_Datapatient = (Button) view.findViewById(R.id.Save_Datapatient);

        BSave_Datapatient.setOnClickListener(this);

        progressDialog = new ProgressDialog(getContext());

        // ในการเรียกใช้ Firebase จำเป็นจะต้อง เรียกใช้ สามบรรทัดนี้เพื่อ Instance()
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


    }
    int ShowStatus = 0;

    // Method ของการเพิ่มข้อมูลใน Firebase Realtime Database
    private void Addp() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

         Pid = EIDpatient.getText().toString().trim();
        String Pname = ENamepatient.getText().toString().trim();
        String Page = EAgepatient.getText().toString().trim();
        String Pbith = EBirthDaypatient.getText().toString().trim();
        String UserID = user.getUid();
        double lat = 0;
        double lon = 0;
        String time = "";
        double Geo_lat =0.0 ;
        double Geo_lon = 0.0;

        if (TextUtils.isEmpty(Pid)) {
            Toast.makeText(getContext(), "Please enter ID", Toast.LENGTH_LONG).show();

        }else if (!Pid.matches("[0-9]+")){

            EIDpatient.requestFocus();
            EIDpatient.setError("PID NOT CHARTER");

            return;
        }

        if (TextUtils.isEmpty(Pname)){
            Toast.makeText(getContext(), "Please enter Name", Toast.LENGTH_SHORT).show();
        }else if (!Pname.matches("[a-zA-Z ]+")){

            ENamepatient.requestFocus();
            ENamepatient.setError("PNAME NOT NUMBER");

            return;
        }

        if (TextUtils.isEmpty(Page)) {
            Toast.makeText(getContext(), "Please enter Age", Toast.LENGTH_LONG).show();

        }else if (!Pid.matches("[0-9]+")){

            EIDpatient.requestFocus();
            EIDpatient.setError("PAGE NOT CHARTER");

            return;
        }

        if (TextUtils.isEmpty(Pbith)) {
            Toast.makeText(getContext(), "Please enter Birthday", Toast.LENGTH_LONG).show();
            return;
        }

        databaseReference.child("Alert").child(Pid).child("PaID").setValue(Pid);
        databaseReference.child("Alert").child(Pid).child("UUsereID").setValue(user.getUid());
        databaseReference.child("Alert").child(Pid).child("ShowStatus").setValue(ShowStatus);
        PatientInformation patientInformation = new PatientInformation(Pid, Pname, Page, Pbith, UserID, lat, lon, time,Geo_lat,Geo_lon );
                    //        เราทำการเพิ่ม .child(Pid) เพื่อทำการสร้าง pramry key

        databaseReference.child("PATIENT").child(Pid).setValue(patientInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    AddgFragment addgFragment = new AddgFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("Pkey", Pid);
                    addgFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment, addgFragment).commit();
                } else {
                    Toast.makeText(getContext(), "ERRORRRRR", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public void onClick(View view) {
        if (view == BSave_Datapatient) {
            Addp();
        }
    }
}
