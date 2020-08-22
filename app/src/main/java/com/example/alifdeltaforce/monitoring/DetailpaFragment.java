package com.example.alifdeltaforce.monitoring;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class DetailpaFragment extends Fragment implements View.OnClickListener {


    public DetailpaFragment() {

    }
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private List<PatientInformation>result;
    private PatientAdapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button push;

    private TextView showtext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailpa, container,false);
        return inflater.inflate(R.layout.fragment_detailpa, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        push=(Button)view.findViewById(R.id.push_button);
        push.setOnClickListener(this);

        result = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.p_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lim = new LinearLayoutManager(getContext());
        lim.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(lim);

        showtext = view.findViewById(R.id.TextEmptry);

        progressDialog = new ProgressDialog(getContext());

        adapter = new PatientAdapter(result);
        recyclerView.setAdapter(adapter);

        updateList();
        checkEmpty();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 0:
                break;
            case 1:
                removePatient(item.getGroupId());
                Toast.makeText(getContext(), "Delete Data", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void updateList(){
        progressDialog.setMessage("Loading Detail...");
        progressDialog.show();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String PaID = user.getUid();
        FirebaseDatabase.getInstance().getReference().child("PATIENT").orderByChild("UserID").equalTo(PaID).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                result.add(dataSnapshot.getValue(PatientInformation.class));
                adapter.notifyDataSetChanged();
                checkEmpty();
                progressDialog.dismiss();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                PatientInformation patientInformation = dataSnapshot.getValue(PatientInformation.class);

                int index = getItemIdex(patientInformation);

                result.set(index, patientInformation);
                adapter.notifyItemChanged(index);


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                PatientInformation patientInformation = dataSnapshot.getValue(PatientInformation.class);

                int index = getItemIdex(patientInformation);
                result.remove(index);
                adapter.notifyItemRemoved(index);
                checkEmpty();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private int getItemIdex(PatientInformation patientInformation){
        int index = -1;

        for (int i =0; i < result.size();i++) {
            if (result.get(i).Pid.equals(patientInformation.Pid)){
                index = i;
                break;
            }
        }

        return index;
    }

    private void removePatient(int position){
        //ลบข้อมูลจาก Recycler View
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child("PATIENT").child(result.get(position).Pid).removeValue();

    }

//สำหรับการเช็คว่ามีข้อมูลแสดงไหมถ้าไม่มีให้แสดงข้อมูลออกมาเป็น Textview
    private void checkEmpty(){
        if (result.size()==0){
            recyclerView.setVisibility(View.INVISIBLE);
            showtext.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            showtext.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

        AddpFragment addpFragment = new AddpFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, addpFragment).commit();

    }
//********************************************* สำรองในกรณีที่ต้องการข้อมูลจากตารางผู้ป่วยตาราง Geofence ก็จะถูกลบไปด้วย **********************************//
    public void ReadGEO() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String PaID = user.getUid();
        databaseReference.child("Geofence").orderByChild("PID").equalTo(PaID).removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
