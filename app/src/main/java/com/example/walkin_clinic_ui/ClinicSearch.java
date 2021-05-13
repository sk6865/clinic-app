package com.example.walkin_clinic_ui;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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

public class ClinicSearch extends AppCompatActivity {

    private DatabaseReference clinicDatabase = FirebaseDatabase.getInstance().getReference("clinics");;
    ListView listViewClinics;
    List<WalkinClinic> clinics;
    List<String> clinicIDs;
    private String clinicID;

    private Spinner daySpinner;
    private Spinner openHourSpinner;
    private Spinner openMinuteSpinner;
    private Spinner openAMPMSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_search);


        listViewClinics = findViewById(R.id.listViewCLinicsSearch);

        clinics = new ArrayList<>();

        clinicIDs = new ArrayList<>();

        listViewClinics.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                WalkinClinic clinic = clinics.get(i);
                clinicID = clinicIDs.get(i);

                showClinicSearchDialog(clinicID);
                return true;
            }
        });

        daySpinner = findViewById(R.id.dayClinic);
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(this,R.array.search_sched_day,android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);


        openHourSpinner = findViewById(R.id.openHourClinic);
        ArrayAdapter<CharSequence> openHourAdapter = ArrayAdapter.createFromResource(this,R.array.search_sched_hours,android.R.layout.simple_spinner_item);
        openHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        openHourSpinner.setAdapter(openHourAdapter);

        openMinuteSpinner = findViewById(R.id.openMinutesClinic);
        ArrayAdapter<CharSequence> openMinuteAdapter = ArrayAdapter.createFromResource(this,R.array.search_sched_minutes,android.R.layout.simple_spinner_item);
        openMinuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        openMinuteSpinner.setAdapter(openMinuteAdapter);

        openAMPMSpinner = findViewById(R.id.openAMPMClinic);
        ArrayAdapter<CharSequence> openAMPMAdapter = ArrayAdapter.createFromResource(this,R.array.search_sched_ampm,android.R.layout.simple_spinner_item);
        openAMPMAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        openAMPMSpinner.setAdapter(openAMPMAdapter);
    }






    private void showClinicSearchDialog(final String clinicID) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.clinic_search_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonCancelSearch = (Button) dialogView.findViewById(R.id.button_search_cancel_);

        final Button buttonBook = (Button) dialogView.findViewById(R.id.btn_book_appt);


        dialogBuilder.setTitle("Clinic Search");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent (getBaseContext(), BookAppointment.class);
                myIntent.putExtra("EXTRA_SESSION_ID", clinicID);
                startActivity(myIntent);

                b.dismiss();
            }
        });

    }








    public void onClickSearch(View view){

        clinicDatabase = FirebaseDatabase.getInstance().getReference("clinics");

        String searchAddress = ((EditText) findViewById(R.id.search_address)).getText().toString();
        String searchService = ((EditText) findViewById(R.id.search_service)).getText().toString();

        String day = daySpinner.getSelectedItem().toString();
        String hours = openHourSpinner.getSelectedItem().toString();
        String minute = openMinuteSpinner.getSelectedItem().toString();
        String amPm = openAMPMSpinner.getSelectedItem().toString();

        boolean clinicOpenTime = (!day.equals("--")) && (!hours.equals("--")) && (!minute.equals("--")) && (!amPm.equals("--"));

        System.out.println("Clinic Open Time = "+ clinicOpenTime);


        //EVERY FIELD IS ENTERED

        if ((!TextUtils.isEmpty(searchAddress)) && (!TextUtils.isEmpty(searchService)) && clinicOpenTime) {

            clinicDatabase.addValueEventListener(new ValueEventListener() {

                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                    clinics.clear();


                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){


                        String address = postSnapshot.child("address").getValue(String.class);
                        System.out.println(address);

                        if(searchAddress.equals(address)){

                            String insurance = postSnapshot.child("insuranceType").getValue(String.class);
                            String name = postSnapshot.child("name").getValue(String.class);
                            Schedule schedule = postSnapshot.child("schedule").getValue(Schedule.class);
                            String phoneNumber = postSnapshot.child("phoneNumber").getValue(String.class);

                            System.out.println(name);

                            ArrayList<Service> services = new ArrayList<Service>();
                            boolean hasService = false;

                            for(DataSnapshot serviceSnapshot: postSnapshot.child("services").getChildren()){

                                Service service = serviceSnapshot.getValue(Service.class);

                                String serviceName = service.getName();

                                if(searchService.equals(serviceName)){
                                    hasService = true;
                                }

                                services.add(service);

                            }

                            boolean correctStartTime = false;

                            if (day.equals("Monday") && hours.equals((schedule.Monday.startHour)) && minute.equals((schedule.Monday.startMinute)) && amPm.equals((schedule.Monday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Tuesday") && hours.equals((schedule.Tuesday.startHour)) && minute.equals((schedule.Tuesday.startMinute)) && amPm.equals((schedule.Tuesday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Wednesday") && hours.equals((schedule.Wednesday.startHour)) && minute.equals((schedule.Wednesday.startMinute)) && amPm.equals((schedule.Wednesday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Thursday") && hours.equals((schedule.Thursday.startHour)) && minute.equals((schedule.Thursday.startMinute)) && amPm.equals((schedule.Thursday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Friday") && hours.equals((schedule.Friday.startHour)) && minute.equals((schedule.Friday.startMinute)) && amPm.equals((schedule.Friday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Saturday") && hours.equals((schedule.Saturday.startHour)) && minute.equals((schedule.Saturday.startMinute)) && amPm.equals((schedule.Saturday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Sunday") && hours.equals((schedule.Sunday.startHour)) && minute.equals((schedule.Sunday.startMinute)) && amPm.equals((schedule.Sunday.startAMPM))){

                            }

                            if(hasService && correctStartTime){
                                WalkinClinic clinic = new WalkinClinic(name,insurance, phoneNumber, address);
                                clinic.setSchedule(schedule);
                                clinic.setServices(services);

                                clinics.add(clinic);
                                String id = postSnapshot.getKey();
                                clinicIDs.add(id);
                            }

                        }

                    }

                    System.out.println(clinics.size());
                    if(clinics.isEmpty()){
                        Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_LONG).show();
                    }

                    ClinicList clinicsAdapter = new ClinicList(ClinicSearch.this, clinics);
                    listViewClinics.setAdapter(clinicsAdapter);
                }

                public void onCancelled(@NonNull DatabaseError databaseError){
                    throw databaseError.toException();
                }
            });
        }

        // IF Address and Service are entered

        else if ((!TextUtils.isEmpty(searchAddress)) && (!TextUtils.isEmpty(searchService)) && !clinicOpenTime) {

            clinicDatabase.addValueEventListener(new ValueEventListener() {

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    clinics.clear();


                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {


                        String address = postSnapshot.child("address").getValue(String.class);
                        System.out.println(address);

                        if (searchAddress.equals(address)) {

                            String insurance = postSnapshot.child("insuranceType").getValue(String.class);
                            String name = postSnapshot.child("name").getValue(String.class);
                            Schedule schedule = postSnapshot.child("schedule").getValue(Schedule.class);
                            String phoneNumber = postSnapshot.child("phoneNumber").getValue(String.class);

                            System.out.println(name);

                            ArrayList<Service> services = new ArrayList<Service>();
                            boolean hasService = false;

                            for (DataSnapshot serviceSnapshot : postSnapshot.child("services").getChildren()) {

                                Service service = serviceSnapshot.getValue(Service.class);

                                String serviceName = service.getName();

                                if (searchService.equals(serviceName)) {
                                    hasService = true;
                                }

                                services.add(service);

                            }



                            if (hasService) {
                                WalkinClinic clinic = new WalkinClinic(name, insurance, phoneNumber, address);
                                clinic.setSchedule(schedule);
                                clinic.setServices(services);


                                clinics.add(clinic);

                                String id = postSnapshot.getKey();
                                clinicIDs.add(id);
                            }

                        }

                    }

                    System.out.println(clinics.size());
                    if(clinics.size() == 0){
                        Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_LONG).show();
                    }

                    ClinicList clinicsAdapter = new ClinicList(ClinicSearch.this, clinics);
                    listViewClinics.setAdapter(clinicsAdapter);
                }

                public void onCancelled(@NonNull DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        }

        //Address and Opening time are entered

        else if ((!TextUtils.isEmpty(searchAddress)) && (TextUtils.isEmpty(searchService)) && clinicOpenTime) {

            clinicDatabase.addValueEventListener(new ValueEventListener() {

                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                    clinics.clear();


                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){


                        String address = postSnapshot.child("address").getValue(String.class);
                        System.out.println(address);

                        if(searchAddress.equals(address)){

                            String insurance = postSnapshot.child("insuranceType").getValue(String.class);
                            String name = postSnapshot.child("name").getValue(String.class);
                            Schedule schedule = postSnapshot.child("schedule").getValue(Schedule.class);
                            String phoneNumber = postSnapshot.child("phoneNumber").getValue(String.class);

                            System.out.println(name);

                            ArrayList<Service> services = new ArrayList<Service>();


                            for(DataSnapshot serviceSnapshot: postSnapshot.child("services").getChildren()){

                                Service service = serviceSnapshot.getValue(Service.class);

                                services.add(service);

                            }

                            boolean correctStartTime = false;

                            if (day.equals("Monday") && hours.equals((schedule.Monday.startHour)) && minute.equals((schedule.Monday.startMinute)) && amPm.equals((schedule.Monday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Tuesday") && hours.equals((schedule.Tuesday.startHour)) && minute.equals((schedule.Tuesday.startMinute)) && amPm.equals((schedule.Tuesday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Wednesday") && hours.equals((schedule.Wednesday.startHour)) && minute.equals((schedule.Wednesday.startMinute)) && amPm.equals((schedule.Wednesday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Thursday") && hours.equals((schedule.Thursday.startHour)) && minute.equals((schedule.Thursday.startMinute)) && amPm.equals((schedule.Thursday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Friday") && hours.equals((schedule.Friday.startHour)) && minute.equals((schedule.Friday.startMinute)) && amPm.equals((schedule.Friday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Saturday") && hours.equals((schedule.Saturday.startHour)) && minute.equals((schedule.Saturday.startMinute)) && amPm.equals((schedule.Saturday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Sunday") && hours.equals((schedule.Sunday.startHour)) && minute.equals((schedule.Sunday.startMinute)) && amPm.equals((schedule.Sunday.startAMPM))){

                            }

                            if(correctStartTime){
                                WalkinClinic clinic = new WalkinClinic(name,insurance, phoneNumber, address);
                                clinic.setSchedule(schedule);
                                clinic.setServices(services);

                                clinics.add(clinic);

                                String id = postSnapshot.getKey();
                                clinicIDs.add(id);
                            }

                        }

                    }

                    System.out.println(clinics.size());
                    if(clinics.size() == 0){
                        Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_LONG).show();
                    }

                    ClinicList clinicsAdapter = new ClinicList(ClinicSearch.this, clinics);
                    listViewClinics.setAdapter(clinicsAdapter);
                }

                public void onCancelled(@NonNull DatabaseError databaseError){
                    throw databaseError.toException();
                }
            });
        }

        //Only Address is entered

        else if ((!TextUtils.isEmpty(searchAddress)) && (TextUtils.isEmpty(searchService)) && !clinicOpenTime) {

            clinicDatabase.addValueEventListener(new ValueEventListener() {

                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                    clinics.clear();


                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){


                        String address = postSnapshot.child("address").getValue(String.class);
                        System.out.println(address);

                        if(searchAddress.equals(address)){

                            String insurance = postSnapshot.child("insuranceType").getValue(String.class);
                            String name = postSnapshot.child("name").getValue(String.class);
                            Schedule schedule = postSnapshot.child("schedule").getValue(Schedule.class);
                            String phoneNumber = postSnapshot.child("phoneNumber").getValue(String.class);

                            System.out.println(name);

                            ArrayList<Service> services = new ArrayList<Service>();
                            boolean hasService = false;

                            for(DataSnapshot serviceSnapshot: postSnapshot.child("services").getChildren()){

                                Service service = serviceSnapshot.getValue(Service.class);

                                String serviceName = service.getName();


                                services.add(service);

                            }




                                WalkinClinic clinic = new WalkinClinic(name,insurance, phoneNumber, address);
                                clinic.setSchedule(schedule);
                                clinic.setServices(services);

                                clinics.add(clinic);

                            String id = postSnapshot.getKey();
                            clinicIDs.add(id);


                        }

                    }


                    System.out.println(clinics.size());
                    if(clinics.size() == 0){
                        Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_LONG).show();
                    }

                    ClinicList clinicsAdapter = new ClinicList(ClinicSearch.this, clinics);
                    listViewClinics.setAdapter(clinicsAdapter);
                }

                public void onCancelled(@NonNull DatabaseError databaseError){
                    throw databaseError.toException();
                }
            });
        }

        //Service and opening time are entered

        else if ((TextUtils.isEmpty(searchAddress)) && (!TextUtils.isEmpty(searchService)) && clinicOpenTime) {

            clinicDatabase.addValueEventListener(new ValueEventListener() {

                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                    clinics.clear();


                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){


                        String address = postSnapshot.child("address").getValue(String.class);


                        if(true){

                            String insurance = postSnapshot.child("insuranceType").getValue(String.class);
                            String name = postSnapshot.child("name").getValue(String.class);
                            Schedule schedule = postSnapshot.child("schedule").getValue(Schedule.class);
                            String phoneNumber = postSnapshot.child("phoneNumber").getValue(String.class);


                            ArrayList<Service> services = new ArrayList<Service>();
                            boolean hasService = false;

                            for(DataSnapshot serviceSnapshot: postSnapshot.child("services").getChildren()){

                                Service service = serviceSnapshot.getValue(Service.class);

                                String serviceName = service.getName();

                                if(searchService.equals(serviceName)){
                                    hasService = true;
                                }

                                services.add(service);

                            }

                            boolean correctStartTime = false;

                            if (day.equals("Monday") && hours.equals((schedule.Monday.startHour)) && minute.equals((schedule.Monday.startMinute)) && amPm.equals((schedule.Monday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Tuesday") && hours.equals((schedule.Tuesday.startHour)) && minute.equals((schedule.Tuesday.startMinute)) && amPm.equals((schedule.Tuesday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Wednesday") && hours.equals((schedule.Wednesday.startHour)) && minute.equals((schedule.Wednesday.startMinute)) && amPm.equals((schedule.Wednesday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Thursday") && hours.equals((schedule.Thursday.startHour)) && minute.equals((schedule.Thursday.startMinute)) && amPm.equals((schedule.Thursday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Friday") && hours.equals((schedule.Friday.startHour)) && minute.equals((schedule.Friday.startMinute)) && amPm.equals((schedule.Friday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Saturday") && hours.equals((schedule.Saturday.startHour)) && minute.equals((schedule.Saturday.startMinute)) && amPm.equals((schedule.Saturday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Sunday") && hours.equals((schedule.Sunday.startHour)) && minute.equals((schedule.Sunday.startMinute)) && amPm.equals((schedule.Sunday.startAMPM))){

                            }

                            if(hasService && correctStartTime){
                                WalkinClinic clinic = new WalkinClinic(name,insurance, phoneNumber, address);
                                clinic.setSchedule(schedule);
                                clinic.setServices(services);

                                clinics.add(clinic);

                                String id = postSnapshot.getKey();
                                clinicIDs.add(id);
                            }

                        }

                    }

                    System.out.println(clinics.size());
                    if(clinics.size() == 0){
                        Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_LONG).show();
                    }

                    ClinicList clinicsAdapter = new ClinicList(ClinicSearch.this, clinics);
                    listViewClinics.setAdapter(clinicsAdapter);
                }

                public void onCancelled(@NonNull DatabaseError databaseError){
                    throw databaseError.toException();
                }
            });
        }

        //Only Service is entered

        else if ((TextUtils.isEmpty(searchAddress)) && (!TextUtils.isEmpty(searchService)) && !clinicOpenTime) {

            clinicDatabase.addValueEventListener(new ValueEventListener() {

                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                    clinics.clear();


                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){


                        String address = postSnapshot.child("address").getValue(String.class);
                        System.out.println(address);

                        if(true){

                            String insurance = postSnapshot.child("insuranceType").getValue(String.class);
                            String name = postSnapshot.child("name").getValue(String.class);
                            Schedule schedule = postSnapshot.child("schedule").getValue(Schedule.class);
                            String phoneNumber = postSnapshot.child("phoneNumber").getValue(String.class);

                            System.out.println(name);

                            ArrayList<Service> services = new ArrayList<Service>();
                            boolean hasService = false;

                            for(DataSnapshot serviceSnapshot: postSnapshot.child("services").getChildren()){

                                Service service = serviceSnapshot.getValue(Service.class);

                                String serviceName = service.getName();

                                if(searchService.equals(serviceName)){
                                    hasService = true;
                                }

                                services.add(service);

                            }


                            if(hasService){
                                WalkinClinic clinic = new WalkinClinic(name,insurance, phoneNumber, address);
                                clinic.setSchedule(schedule);
                                clinic.setServices(services);

                                clinics.add(clinic);

                                String id = postSnapshot.getKey();
                                clinicIDs.add(id);
                            }

                        }

                    }

                    System.out.println(clinics.size());
                    if(clinics.size() == 0){
                        Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_LONG).show();
                    }

                    ClinicList clinicsAdapter = new ClinicList(ClinicSearch.this, clinics);
                    listViewClinics.setAdapter(clinicsAdapter);
                }

                public void onCancelled(@NonNull DatabaseError databaseError){
                    throw databaseError.toException();
                }
            });
        }

        //Only opening time is entered

        else if ((TextUtils.isEmpty(searchAddress)) && (TextUtils.isEmpty(searchService)) && !clinicOpenTime) {

            clinicDatabase.addValueEventListener(new ValueEventListener() {

                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                    clinics.clear();


                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){


                        String address = postSnapshot.child("address").getValue(String.class);
                        System.out.println(address);

                        if(true){

                            String insurance = postSnapshot.child("insuranceType").getValue(String.class);
                            String name = postSnapshot.child("name").getValue(String.class);
                            Schedule schedule = postSnapshot.child("schedule").getValue(Schedule.class);
                            String phoneNumber = postSnapshot.child("phoneNumber").getValue(String.class);

                            System.out.println(name);

                            ArrayList<Service> services = new ArrayList<Service>();

                            for(DataSnapshot serviceSnapshot: postSnapshot.child("services").getChildren()){

                                Service service = serviceSnapshot.getValue(Service.class);

                                String serviceName = service.getName();



                                services.add(service);

                            }

                            boolean correctStartTime = false;

                            if (day.equals("Monday") && hours.equals((schedule.Monday.startHour)) && minute.equals((schedule.Monday.startMinute)) && amPm.equals((schedule.Monday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Tuesday") && hours.equals((schedule.Tuesday.startHour)) && minute.equals((schedule.Tuesday.startMinute)) && amPm.equals((schedule.Tuesday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Wednesday") && hours.equals((schedule.Wednesday.startHour)) && minute.equals((schedule.Wednesday.startMinute)) && amPm.equals((schedule.Wednesday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Thursday") && hours.equals((schedule.Thursday.startHour)) && minute.equals((schedule.Thursday.startMinute)) && amPm.equals((schedule.Thursday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Friday") && hours.equals((schedule.Friday.startHour)) && minute.equals((schedule.Friday.startMinute)) && amPm.equals((schedule.Friday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Saturday") && hours.equals((schedule.Saturday.startHour)) && minute.equals((schedule.Saturday.startMinute)) && amPm.equals((schedule.Saturday.startAMPM))){
                                correctStartTime = true;
                            }else if (day.equals("Sunday") && hours.equals((schedule.Sunday.startHour)) && minute.equals((schedule.Sunday.startMinute)) && amPm.equals((schedule.Sunday.startAMPM))){

                            }

                            if(correctStartTime){
                                WalkinClinic clinic = new WalkinClinic(name,insurance, phoneNumber, address);
                                clinic.setSchedule(schedule);
                                clinic.setServices(services);

                                clinics.add(clinic);

                                String id = postSnapshot.getKey();
                                clinicIDs.add(id);
                            }

                        }

                    }

                    System.out.println(clinics.size());
                    if(clinics.size() == 0){
                        Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_LONG).show();
                    }

                    ClinicList clinicsAdapter = new ClinicList(ClinicSearch.this, clinics);
                    listViewClinics.setAdapter(clinicsAdapter);
                }

                public void onCancelled(@NonNull DatabaseError databaseError){
                    throw databaseError.toException();
                }
            });
        }

        //EVERY FIELD IS ENTERED

        else if ((TextUtils.isEmpty(searchAddress)) && (TextUtils.isEmpty(searchService)) && clinicOpenTime) {

            clinicDatabase.addValueEventListener(new ValueEventListener() {

                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                    clinics.clear();


                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){


                        String address = postSnapshot.child("address").getValue(String.class);
                        System.out.println(address);

                        if(true) {

                            String insurance = postSnapshot.child("insuranceType").getValue(String.class);
                            String name = postSnapshot.child("name").getValue(String.class);
                            Schedule schedule = postSnapshot.child("schedule").getValue(Schedule.class);
                            String phoneNumber = postSnapshot.child("phoneNumber").getValue(String.class);

                            System.out.println(name);

                            ArrayList<Service> services = new ArrayList<Service>();

                            for (DataSnapshot serviceSnapshot : postSnapshot.child("services").getChildren()) {

                                Service service = serviceSnapshot.getValue(Service.class);

                                String serviceName = service.getName();

                                services.add(service);

                            }

                            boolean correctStartTime = false;

                            if (day.equals("Monday") && hours.equals((schedule.Monday.startHour)) && minute.equals((schedule.Monday.startMinute)) && amPm.equals((schedule.Monday.startAMPM))) {
                                correctStartTime = true;
                            } else if (day.equals("Tuesday") && hours.equals((schedule.Tuesday.startHour)) && minute.equals((schedule.Tuesday.startMinute)) && amPm.equals((schedule.Tuesday.startAMPM))) {
                                correctStartTime = true;
                            } else if (day.equals("Wednesday") && hours.equals((schedule.Wednesday.startHour)) && minute.equals((schedule.Wednesday.startMinute)) && amPm.equals((schedule.Wednesday.startAMPM))) {
                                correctStartTime = true;
                            } else if (day.equals("Thursday") && hours.equals((schedule.Thursday.startHour)) && minute.equals((schedule.Thursday.startMinute)) && amPm.equals((schedule.Thursday.startAMPM))) {
                                correctStartTime = true;
                            } else if (day.equals("Friday") && hours.equals((schedule.Friday.startHour)) && minute.equals((schedule.Friday.startMinute)) && amPm.equals((schedule.Friday.startAMPM))) {
                                correctStartTime = true;
                            } else if (day.equals("Saturday") && hours.equals((schedule.Saturday.startHour)) && minute.equals((schedule.Saturday.startMinute)) && amPm.equals((schedule.Saturday.startAMPM))) {
                                correctStartTime = true;
                            } else if (day.equals("Sunday") && hours.equals((schedule.Sunday.startHour)) && minute.equals((schedule.Sunday.startMinute)) && amPm.equals((schedule.Sunday.startAMPM))) {

                            }

                            if (correctStartTime) {
                                WalkinClinic clinic = new WalkinClinic(name, insurance, phoneNumber, address);
                                clinic.setSchedule(schedule);
                                clinic.setServices(services);

                                clinics.add(clinic);

                                String id = postSnapshot.getKey();
                                clinicIDs.add(id);
                            }
                        }

                    }

                    System.out.println(clinics.size());
                    if(clinics.size() == 0){
                        Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_LONG).show();
                    }


                    ClinicList clinicsAdapter = new ClinicList(ClinicSearch.this, clinics);
                    listViewClinics.setAdapter(clinicsAdapter);
                }

                public void onCancelled(@NonNull DatabaseError databaseError){
                    throw databaseError.toException();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_LONG).show();
        }


    }


    /*public void onBookAppt (View view, String clinicId) {
         Intent myIntent = new Intent (getBaseContext(), BookAppointment.class);
        myIntent.putExtra("EXTRA_SESSION_ID", clinicId);
         startActivity(myIntent);
    }*/

    public void onCheckTime (View view) {
        Intent myIntent = new Intent (getBaseContext(), CheckWaitTime.class);
        myIntent.putExtra("ID", clinicID);
        startActivity(myIntent);
    }

    public void onRateClinic (View view) {
        Intent myIntent = new Intent (getBaseContext(), RateClinic.class);
        myIntent.putExtra("ID", clinicID); // Should be set to right ID when we open dialog box.
        startActivity(myIntent);
    }

}
