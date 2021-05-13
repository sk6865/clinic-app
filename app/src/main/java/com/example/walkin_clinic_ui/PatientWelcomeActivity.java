package com.example.walkin_clinic_ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;

public class PatientWelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "PatientWelcomeActivity";

    Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_patient_welcome);

        String welcomeMessage = "Welcome, " + fieldValidate.username + "!";


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("patients").child(uid);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        PatientAccount account = dataSnapshot.getValue(PatientAccount.class);

                        String accountMessage = "Account type: " + account.type;


                        ((TextView) findViewById(R.id.account_type)).setText(accountMessage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "onCancelled", databaseError.toException());
                }
            });



            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            welcomeMessage = "Welcome, " + name + "!";
        }else{
            ((TextView)findViewById(R.id.account_type)).setText("Account type: None");
        }


        TextView t = (TextView)findViewById(R.id.stf_welcome_generic);

        /* This should be set dynamically based on user attributes. */
        t.setText(welcomeMessage);

    }


    public void onLogOut(View view){
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void onCompleteClinicProfile (View view) {
        Intent myIntent = new Intent(getBaseContext(), CreateClinic.class);
        startActivity(myIntent);
    }

    public void onViewSchedule (View view) {

        FirebaseDatabase.getInstance().getReference().child("test").child("DonaldTrump").setValue(new Schedule()); // Placeholder for testing purposes until the clinic class is made.
        Intent myIntent = new Intent(getBaseContext(), ScheduleConfirm.class);
        startActivity(myIntent);
    }

    public void onClinicSearch(View view){
        Intent myIntent = new Intent(getBaseContext(), ClinicSearch.class);
        startActivity(myIntent);
    }








}