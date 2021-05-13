package com.example.walkin_clinic_ui;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CheckWaitTime extends AppCompatActivity {

    List<Appointment> appts;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String clinicID;
    private DatabaseReference clinicDatabase;
    private static final String TAG = "CheckWaitTime";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_check_wait_time);

        appts = new ArrayList<>();
        Intent myIntent = getIntent();
        clinicID = myIntent.getStringExtra("ID");

//      clinicID = getIntent().getStringExtra("EXTRA_SESSION_ID");
      System.out.println("CLINIC " + clinicID);

      //Intent myIntent = getIntent();
    } //closes onCreate method


    public void onClickSubmit (View view) {

        EditText editText = (EditText) findViewById(R.id.editText);
        TextView textView8 = (TextView) findViewById(R.id.textView8);


        String searchDay = ((EditText) findViewById(R.id.editText)).getText().toString();
        boolean validDay = fieldValidate.dayValidate(searchDay);

        if (TextUtils.isEmpty(searchDay)) {
            //System.out.println("else not passed");
            editText.setHint("Please enter a day"); //makes sure day field is not empty

        }
        else if(!validDay){
            editText.setText("");
            editText.setHint("Monday - Sunday.");
            editText.setHintTextColor(getResources().getColor(R.color.hintFail));
        }
        else {
            System.out.println("else passed");
            System.out.println("clinic id is: " + clinicID);

            if (clinicID != null) {
                System.out.println("Clinic ID is: " + clinicID);

                mDatabase = FirebaseDatabase.getInstance().getReference("clinics").child(clinicID).child("appointments");
//                clinicDatabase = FirebaseDatabase.getInstance().getReference("clinics").child(clinicID).child("appointments").child(searchDay);
                mDatabase.child(searchDay).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                        appts.clear();
                        System.out.println("DATA CHANGE");

                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Appointment appointment = postSnapshot.getValue(Appointment.class);
                            appts.add(appointment);
                        } //closes for loop

                        System.out.println("list of appts: " + appts);
                        textView8.setText(((appts.size())*15) + " minutes");

                    } //closes onDataChange


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            } else {
                System.out.println("clinic id is null");
            }//closes if
//            System.out.println(((appts.size())*15) + " minutes");

        } //closes if block
//        System.out.println(((appts.size())*15) + " minutes");
//
//        editText3.setText(((appts.size())*15) + " minutes");
//        System.out.println(((appts.size())*15) + " minutes");

        appts.clear();

    } //closes onClickSubmit


} //closes CheckWaitTime class
