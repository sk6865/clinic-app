package com.example.walkin_clinic_ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScheduleConfirm extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Schedule clinicSchedule;
    private String ClinicID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_confirm);

        Intent myIntent = getIntent();
        ClinicID = myIntent.getStringExtra("ID"); // Retrieves clinic ID from previous activity.

        mDatabase = FirebaseDatabase.getInstance().getReference().child("clinics").child(ClinicID); // Retrieves the target clinic.

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /* Pull class from DB and then use it to set text views. */
                String dBaddress = dataSnapshot.child("address").getValue(String.class);
                String dBinsurance = dataSnapshot.child("insuranceType").getValue(String.class);
                String dBname = dataSnapshot.child("name").getValue(String.class);
                Schedule dBschedule = dataSnapshot.child("schedule").getValue(Schedule.class);
                String dBphoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);

                ArrayList<Service> dBservices = new ArrayList<Service>();

                for(DataSnapshot postSnapshot: dataSnapshot.child("services").getChildren()){
                    Service service = postSnapshot.getValue(Service.class);
                    dBservices.add(service);
                }

                WalkinClinic clinic = new WalkinClinic(dBname,dBinsurance, dBphoneNumber, dBaddress);
                clinic.setSchedule(dBschedule);
                clinic.setServices(dBservices);

                
                clinicSchedule = clinic.getSchedule();
                ((TextView)findViewById(R.id.confMonday)).setText(clinicSchedule.Monday.toString());
                ((TextView)findViewById(R.id.confTuesday)).setText(clinicSchedule.Tuesday.toString());
                ((TextView)findViewById(R.id.confWednesday)).setText(clinicSchedule.Wednesday.toString());
                ((TextView)findViewById(R.id.confThursday)).setText(clinicSchedule.Thursday.toString());
                ((TextView)findViewById(R.id.confFriday)).setText(clinicSchedule.Friday.toString());
                ((TextView)findViewById(R.id.confSaturday)).setText(clinicSchedule.Saturday.toString());
                ((TextView)findViewById(R.id.confSunday)).setText(clinicSchedule.Sunday.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Ahoy", "onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * Passes the clinic ID to the change clinic schedule activity, then kills this activity.
     *
     * @param view
     */
    public void OnChange(View view)
    {
        Intent intent = new Intent(getApplicationContext(), SetSchedule.class);
        intent.putExtra("ID", ClinicID);

        startActivityForResult (intent,0);
        finish();
    }

    /**
     * Returns us to the welcome screen.
     *
     * @param view
     */
    public void OnConfirm(View view)
    {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
