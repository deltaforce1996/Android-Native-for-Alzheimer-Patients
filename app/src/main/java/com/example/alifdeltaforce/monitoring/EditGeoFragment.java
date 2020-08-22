package com.example.alifdeltaforce.monitoring;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
public class EditGeoFragment extends Fragment {

    private EditText EditID;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    int Round= 0;
    int Status = 0;

    public EditGeoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_geo, container, false);
        return inflater.inflate(R.layout.fragment_edit_geo, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditID = (EditText) view.findViewById(R.id.EEPPID);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        CCheckPid();

        Button Editclick = (Button) view.findViewById(R.id.EEC_ID);
        Editclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickandCheckPid();
            }
        });
    }

    ArrayList<String> TestArray = new ArrayList<String>();

    public void CCheckPid(){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("PATIENT").orderByChild("UserID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    PatientInformation patientInformation = s.getValue(PatientInformation.class);

                    TestArray.add(patientInformation.Pid);
                    Round= TestArray.size() ;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ClickandCheckPid(){

        String PID = EditID.getText().toString().trim();

        for (int Z = 0; Z < Round ; Z++) {
         if(PID.equalsIgnoreCase(TestArray.get(Z))) {
            EditGeoShowIDFragment editGeoShowIDFragment = new EditGeoShowIDFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Pkey", PID);
            editGeoShowIDFragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment, editGeoShowIDFragment).commit();
            Status=1;
            }
        }

        if (Status<1){
            Toast.makeText(getContext(), "PatientID not match : "+PID, Toast.LENGTH_SHORT).show();
        }

    }
}
