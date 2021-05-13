package com.example.walkin_clinic_ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SetSchedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private DatabaseReference mDatabase;
    private Schedule clinicSchedule;
    private WalkinClinic clinic;
    private String clinicID;

    private Spinner daySpinner;
    private Spinner openHourSpinner;
    private Spinner openMinuteSpinner;
    private Spinner openAMPMSpinner;
    private Spinner closeHourSpinner;
    private Spinner closeMinuteSpinner;
    private Spinner closeAMPMSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_schedule);

        Intent intent = getIntent();
        clinicID = intent.getStringExtra("ID"); // Pulls the clinic ID passed from the previous activity.

        /* Pulls the corresponding clinic object out of the DB. */
        mDatabase = FirebaseDatabase.getInstance().getReference().child("clinics").child(clinicID);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /* Pull class from DB and gets chedule from it. */


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

                ArrayList<Comment> dBcomments = new ArrayList<Comment>();

                for(DataSnapshot postSnapshot: dataSnapshot.child("comments").getChildren()){
                    Comment comment = postSnapshot.getValue(Comment.class);
                    dBcomments.add(comment);
                }

                WalkinClinic clinic = new WalkinClinic(dBname,dBinsurance, dBphoneNumber, dBaddress);
                clinic.setSchedule(dBschedule);
                clinic.setServices(dBservices);
                clinic.setComments(dBcomments);


                clinicSchedule = clinic.getSchedule();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Ahoy", "onCancelled", databaseError.toException());
            }
        });

        /* Fire up the spinners! These are needed to get/set times for each day. */
        daySpinner = findViewById(R.id.dayClinic);
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(this,R.array.sched_day,android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);
        daySpinner.setOnItemSelectedListener(this);

        openHourSpinner = findViewById(R.id.openHourClinic);
        ArrayAdapter<CharSequence> openHourAdapter = ArrayAdapter.createFromResource(this,R.array.sched_hours,android.R.layout.simple_spinner_item);
        openHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        openHourSpinner.setAdapter(openHourAdapter);

        openMinuteSpinner = findViewById(R.id.openMinutesClinic);
        ArrayAdapter<CharSequence> openMinuteAdapter = ArrayAdapter.createFromResource(this,R.array.sched_minutes,android.R.layout.simple_spinner_item);
        openMinuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        openMinuteSpinner.setAdapter(openMinuteAdapter);

        openAMPMSpinner = findViewById(R.id.openAMPMClinic);
        ArrayAdapter<CharSequence> openAMPMAdapter = ArrayAdapter.createFromResource(this,R.array.sched_ampm,android.R.layout.simple_spinner_item);
        openAMPMAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        openAMPMSpinner.setAdapter(openAMPMAdapter);

        closeHourSpinner = findViewById(R.id.closeHour);
        ArrayAdapter<CharSequence> closeHourAdapter = ArrayAdapter.createFromResource(this,R.array.sched_hours,android.R.layout.simple_spinner_item);
        closeHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        closeHourSpinner.setAdapter(closeHourAdapter);

        closeMinuteSpinner = findViewById(R.id.closeMinutes);
        ArrayAdapter<CharSequence> closeMinuteAdapter = ArrayAdapter.createFromResource(this,R.array.sched_minutes,android.R.layout.simple_spinner_item);
        closeMinuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        closeMinuteSpinner.setAdapter(closeMinuteAdapter);

        closeAMPMSpinner = findViewById(R.id.closeAMPM);
        ArrayAdapter<CharSequence> closeAMPMAdapter = ArrayAdapter.createFromResource(this,R.array.sched_ampm,android.R.layout.simple_spinner_item);
        closeAMPMAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        closeAMPMSpinner.setAdapter(closeAMPMAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        /* gets the current day so we can update remaining spinners accordingly. */
        ScheduleDay cd = clinicSchedule.retrieveDay(daySpinner.getSelectedItem().toString());

        /* used to ensure correct spinners for AM/PM are set. */
        int startIndex = 0;
        int closeIndex = 0
                ;
        if(cd.startAMPM.equals("PM"))
            startIndex++;

        if(cd.endAMPM.equals("PM"))
            closeIndex++;

        openHourSpinner.setSelection(Integer.parseInt(cd.startHour) - 1);
        openMinuteSpinner.setSelection(Integer.parseInt(cd.startMinute));
        openAMPMSpinner.setSelection(startIndex);

        closeHourSpinner.setSelection(Integer.parseInt(cd.endHour) - 1);
        closeMinuteSpinner.setSelection(Integer.parseInt(cd.endMinute));
        closeAMPMSpinner.setSelection(closeIndex);

        buttonFlip(cd.isClosed); // Sets the button that lets you open/close to its proper state.
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Opens/closes the clinic for this day.
     * @param view
     */
    public void onClose(View view)
    {
        String day = ((Spinner) findViewById(R.id.dayClinic)).getSelectedItem().toString();

        clinicSchedule.closeFlip(day);

        boolean closedThisDay = clinicSchedule.retrieveDay(day).isClosed;

        buttonFlip(closedThisDay);
    }

    /**
     * Changes the button to allow user to open/close clinic for the day depending on its
     * current state.
     *
     * @param isClosed If the clinic is closed for the day (true) or not.
     */
    public void buttonFlip(boolean isClosed)
    {
        if(isClosed)
            ((Button)findViewById(R.id.btn_closeDay)).setText("Open Clinic For This Day");

        else
            ((Button)findViewById(R.id.btn_closeDay)).setText("Close Clinic For This Day");
    }

    /**
     * Pulls the times from the spinners and updates the given schedule day accordingly.
     *
     * @param view
     */
    public void onSet(View view) {

        String day = ((Spinner) findViewById(R.id.dayClinic)).getSelectedItem().toString();

        ScheduleDay current = clinicSchedule.retrieveDay(day);

        /* Unroll all the fields and make sure opening time is before closing time. */
        String openHour = ((Spinner) findViewById(R.id.openHourClinic)).getSelectedItem().toString();
        String openMinutes = ((Spinner)findViewById(R.id.openMinutesClinic)).getSelectedItem().toString();
        String openAMPM = ((Spinner)findViewById(R.id.openAMPMClinic)).getSelectedItem().toString();

        String closeHour = ((Spinner) findViewById(R.id.closeHour)).getSelectedItem().toString();
        String closeMinutes = ((Spinner)findViewById(R.id.closeMinutes)).getSelectedItem().toString();
        String closeAMPM = ((Spinner)findViewById(R.id.closeAMPM)).getSelectedItem().toString();

        int startTime = Integer.parseInt(openHour + openMinutes);
        int endTime = Integer.parseInt(closeHour + closeMinutes);

        if(openAMPM.equals("PM") && startTime < 1200)
            startTime += 1200;

        if(openAMPM.equals("AM") && startTime > 1159)
            startTime -= 1200;

        if(closeAMPM.equals("PM") && endTime < 1200)
            endTime += 1200;

        if(closeAMPM.equals("AM") && endTime > 1159)
            endTime -= 1200;

        /* We change nothing if the there isn't some time between opening and closing. */
        if(endTime <= startTime)
            Toast.makeText(SetSchedule.this, "Opening time is not before closing time!", Toast.LENGTH_LONG).show();


        /* Update the current day and use it to update the schedule object. */
        else {
            current.startHour = openHour;
            current.startMinute = openMinutes;
            current.startAMPM = openAMPM;

            current.endHour = closeHour;
            current.endMinute = closeMinutes;
            current.endAMPM = closeAMPM;

            clinicSchedule.updateDay(current);
        }

    }

    /**
     * Once we've finished, the changes are written to the DB and we move to a new confirmation window.
     *
     * The Clinic ID is passed as before. The ideal would have been to make the clinic schedule
     * parcelable, but I decided that was too much of a pain to bother with now.
     *
     * @param view
     */
    public void onFinish(View view)
    {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /* Pull class from DB and gets chedule from it. */

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

                ArrayList<Comment> dBcomments = new ArrayList<Comment>();

                for(DataSnapshot postSnapshot: dataSnapshot.child("comments").getChildren()){
                    Comment comment = postSnapshot.getValue(Comment.class);
                    dBcomments.add(comment);
                }

                WalkinClinic clinic = new WalkinClinic(dBname,dBinsurance, dBphoneNumber, dBaddress);
                clinic.setSchedule(dBschedule);
                clinic.setServices(dBservices);
                clinic.setComments(dBcomments);

                clinic.setSchedule(clinicSchedule);
                mDatabase.setValue(clinic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Ahoy", "onCancelled", databaseError.toException());
            }
        });


        Intent intent = new Intent(getApplicationContext(), ScheduleConfirm.class);
        intent.putExtra("ID", clinicID);
        startActivityForResult (intent,0);
        finish();
    }
}
