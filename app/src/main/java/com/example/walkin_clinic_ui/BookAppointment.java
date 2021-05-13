package com.example.walkin_clinic_ui;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.CalendarView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class BookAppointment extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String clinicID;
    DatabaseReference databaseAppointments;
    private DatabaseReference clinicDatabase;
    private static final String TAG = "BookApp";
    List<Appointment> appointments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_book_appt);

        databaseAppointments = FirebaseDatabase.getInstance().getReference("appointments");
        appointments = new ArrayList<>();


        // THIS LINE ACCEPTS THE CLINIC ID THAT IS PASSED FROM THE CLINIC SEARCH ACTIVITY

        clinicID = getIntent().getStringExtra("EXTRA_SESSION_ID");
        System.out.println("CLINIC " + clinicID);


        Intent myIntent = getIntent();

    } //closes onCreate method


    public void onClickAdd(View view) {

        EditText editTextAppointmentName = (EditText) findViewById(R.id.editTextName);
        EditText editTextAppointmentDay = (EditText) findViewById(R.id.spn_day);
        EditText editTextAppointmentHour = (EditText) findViewById(R.id.spn_hour);
        EditText editTextAppointmentMinute = (EditText) findViewById(R.id.spn_min);
        EditText editTextAppointmentAM = (EditText) findViewById(R.id.spn_ampm);

        String name = editTextAppointmentName.getText().toString();
        String day = editTextAppointmentDay.getText().toString();
        String hour = editTextAppointmentHour.getText().toString();
        String minute = editTextAppointmentMinute.getText().toString();
        String am = editTextAppointmentAM.getText().toString();

        boolean validDay = fieldValidate.dayValidate(day);
        boolean validHour = fieldValidate.hourValidate(hour);
        boolean validMinute = fieldValidate.minuteValidate(minute);
        boolean validAM = fieldValidate.amValidate(am);

        if (TextUtils.isEmpty(name)) {
            editTextAppointmentName.setText("");
            editTextAppointmentName.setHint("Please enter a name");
            editTextAppointmentName.setHintTextColor(getResources().getColor(R.color.hintFail));
        }

        DatabaseReference users = FirebaseDatabase.getInstance().getReference();
        DatabaseReference refUsers = users.child("users").child("patients");
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                boolean flag = false;
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String username = ds.child("username").getValue(String.class);
//                    if (username.equals(name)) {
//                        flag = true;
//                    }
//                }
//                if (!flag) {
//                    editTextAppointmentName.setText("");
//                    editTextAppointmentName.setHint("Please enter an existing patient.");
//                    editTextAppointmentName.setHintTextColor(getResources().getColor(R.color.hintFail));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        };
//        refUsers.addListenerForSingleValueEvent(eventListener);

        if (TextUtils.isEmpty(day) || !validDay) {
            editTextAppointmentDay.setText("");
            editTextAppointmentDay.setHint("Please enter a valid day.");
            editTextAppointmentDay.setHintTextColor(getResources().getColor(R.color.hintFail));
        }

        if (TextUtils.isEmpty(hour) || !validHour) {
            editTextAppointmentHour.setText("");
            editTextAppointmentHour.setHint("Please enter a valid hour.");
            editTextAppointmentHour.setHintTextColor(getResources().getColor(R.color.hintFail));
        }
        if(TextUtils.isEmpty(minute) || !validMinute){
            editTextAppointmentMinute.setText("");
            editTextAppointmentMinute.setHint("Please enter a date and time that the walk in clinic is open (15 minute increments).");
            editTextAppointmentMinute.setHintTextColor(getResources().getColor(R.color.hintFail));
        }

        if (TextUtils.isEmpty(am) ||!validAM ) {
            editTextAppointmentAM.setText("");
            editTextAppointmentAM.setHint("Please enter AM or PM.");
            editTextAppointmentAM.setHintTextColor(getResources().getColor(R.color.hintFail));
        }
        else {
            if (clinicID != null) {
                DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference refSchedule = scheduleRef.child("clinics").child(clinicID).child("schedule");
                ValueEventListener eventListener2 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ScheduleDay schedule = dataSnapshot.child(day).getValue(ScheduleDay.class);
                        if (!schedule.isClosed) {



                            float startTimeHr = Float.parseFloat(schedule.startHour );
                            float startTimeMin = Float.parseFloat(schedule.startMinute)/60;
                            if(startTimeHr==12){
                                startTimeHr=0;
                            }
                            if(schedule.startAMPM.equals("PM")){
                                startTimeHr += 12;
                            }
                            float startTime = startTimeHr +startTimeMin;

                            float enteredTimeHr = Float.parseFloat(hour);
                            if(enteredTimeHr==12){
                                enteredTimeHr=0;
                            }
                            if(am.equals("PM")){
                                enteredTimeHr += 12;
                            }
                            float enteredTimeMin = Float.parseFloat(minute)/60;
                            float enteredTime = enteredTimeHr + enteredTimeMin;

                            float endTimeHr = Float.parseFloat(schedule.endHour);

                            if(endTimeHr==12){
                                endTimeHr=0;
                            }
                            if(schedule.endAMPM.equals("PM")){
                                endTimeHr += 12;
                            }
                            float endTimeMin = Float.parseFloat(schedule.endMinute)/60;
                            float endTime = endTimeHr + endTimeMin;


                            System.out.println("entered time" + enteredTime);

                            System.out.println("end time " + endTime);

                            System.out.println("start time " + startTime);


                            if (enteredTime < startTime || enteredTime > endTime) {
                                editTextAppointmentHour.setText("");
                                editTextAppointmentHour.setHint("Please enter a time that the walk in clinic is open.");
                                editTextAppointmentHour.setHintTextColor(getResources().getColor(R.color.hintFail));
                                editTextAppointmentMinute.setText("");
                                editTextAppointmentMinute.setHint("Please enter a time that the walk in clinic is open.");
                                editTextAppointmentMinute.setHintTextColor(getResources().getColor(R.color.hintFail));
                            }
                            else{
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();
                                clinicDatabase = FirebaseDatabase.getInstance().getReference("clinics").child(clinicID).child("appointments");
                                Appointment appointment = new Appointment(name, day, hour, minute, am);
                                clinicDatabase.child(appointment.getDay()).setValue(appointment);
                                System.out.println("Clinic ID = " + clinicID);
                                Toast.makeText(BookAppointment.this, "Appointment Booked", Toast.LENGTH_LONG).show();

                            }

                        }
                        else{
                            editTextAppointmentDay.setText("");
                            editTextAppointmentDay.setHint("Clinic is closed.");
                            editTextAppointmentDay.setHintTextColor(getResources().getColor(R.color.hintFail));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                refSchedule.addListenerForSingleValueEvent(eventListener2);

            }

        }
    }
}



//closes BookAppointment Class
