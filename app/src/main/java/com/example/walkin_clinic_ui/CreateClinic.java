package com.example.walkin_clinic_ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CreateClinic extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private static final String TAG = "CreateClinic";
    private DatabaseReference mDatabase;
    private DatabaseReference clinicDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_clinic);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("employees").child(uid);

        EditText editTextClinicName = (EditText) findViewById(R.id.fieldClinicName);
        EditText editTextClinicInsurance = (EditText) findViewById(R.id.fieldClinicInsurance);
        EditText editTextClinicAddress = (EditText) findViewById(R.id.fieldClinicAddress);
        EditText editTextClinicPhoneNumber = (EditText) findViewById(R.id.fieldPhoneNumber);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EmployeeAccount account = dataSnapshot.getValue(EmployeeAccount.class);

                String clinicID = account.getClinicID();

                if (clinicID != null) {

                    clinicDatabase = FirebaseDatabase.getInstance().getReference("clinics").child(clinicID);

                    clinicDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

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



                            String  name = clinic.getName();
                            String insurance = clinic.getInsuranceType();
                            String address = clinic.getAddress();
                            String  phoneNumber =  clinic.getPhoneNumber();

                            editTextClinicPhoneNumber.setHint(phoneNumber);
                            editTextClinicName.setHint(name);
                            editTextClinicInsurance.setHint(insurance);
                            editTextClinicAddress.setHint(address);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "onCancelled", databaseError.toException());
                        }
                    });
                }
            }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "onCancelled", databaseError.toException());
                }
            });

    }



    public void onClickSubmit(View view){

        EditText editTextClinicName = (EditText) findViewById(R.id.fieldClinicName);
        EditText editTextClinicInsurance = (EditText) findViewById(R.id.fieldClinicInsurance);
        EditText editTextClinicAddress = (EditText) findViewById(R.id.fieldClinicAddress);
        EditText editTextClinicPhoneNumber = (EditText) findViewById(R.id.fieldPhoneNumber);

        String name = editTextClinicName.getText().toString();
        String insurance = editTextClinicInsurance.getText().toString();
        String address = editTextClinicAddress.getText().toString();
        String phoneNumber = editTextClinicPhoneNumber.getText().toString();
        boolean validAddress = fieldValidate.addressValidate(address);
        boolean validPhone = fieldValidate.phoneValidate(phoneNumber);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("employees").child(uid);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EmployeeAccount account = dataSnapshot.getValue(EmployeeAccount.class);

                String clinicID =  account.getClinicID() ;

                boolean pass = true;

                if(clinicID == null){

                    System.out.println(name);
                    System.out.println(name.equals(""));

                    if (name.equals("")|| name.equals(" ") || TextUtils.isEmpty(name)) {
                        editTextClinicName.setText("");
                        editTextClinicName.setHint("Please enter a clinic name");
                        editTextClinicName.setHintTextColor(getResources().getColor(R.color.hintFail));
                        pass = false;


                    } if (insurance.equals("")|| insurance.equals(" ")||TextUtils.isEmpty(insurance)) {


                        pass = false;
                        editTextClinicInsurance.setText("");
                        editTextClinicInsurance.setHint("Please enter an insurance type");
                        editTextClinicInsurance.setHintTextColor(getResources().getColor(R.color.hintFail));

                    } if (address.equals("")|| address.equals(" ")||TextUtils.isEmpty(address)) {

                        pass = false;
                        editTextClinicAddress.setText("");
                        editTextClinicAddress.setHint("Please enter an address. Ex. 123 Street");
                        editTextClinicAddress.setHintTextColor(getResources().getColor(R.color.hintFail));

                    }else if(!validAddress)
                    {
                        pass = false;
                        editTextClinicAddress.setText("");
                        editTextClinicAddress.setHint("Correct Format: 123 Sesame St");
                        editTextClinicAddress.setHintTextColor(getResources().getColor(R.color.hintFail));
                    }


                    if (phoneNumber.equals("")|| phoneNumber.equals(" ")||TextUtils.isEmpty(phoneNumber)) {

                        pass = false;
                        editTextClinicPhoneNumber.setText("");
                        editTextClinicPhoneNumber.setHint("Please enter a phone number. Ex. 1234567891");
                        editTextClinicPhoneNumber.setHintTextColor(getResources().getColor(R.color.hintFail));

                    }else if(!validPhone)
                    {
                        pass = false;
                        editTextClinicPhoneNumber.setText("");
                        editTextClinicPhoneNumber.setHint("Phone number  must have 10 digits.");

                        editTextClinicPhoneNumber.setHintTextColor(getResources().getColor(R.color.hintFail));
                    }




                    if (pass){


                        WalkinClinic clinic = new WalkinClinic(name, insurance, phoneNumber, address);
                        clinicDatabase = FirebaseDatabase.getInstance().getReference("clinics");
                        String id = clinicDatabase.push().getKey();
                        clinicDatabase.child(id).setValue(clinic);

                        account.setClinicID(id);
                        mDatabase.setValue(account);

                        Toast.makeText(CreateClinic.this, "Clinic Created", Toast.LENGTH_LONG).show();
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();

                    }


                }else{

                    clinicDatabase = FirebaseDatabase.getInstance().getReference("clinics").child(clinicID);

                    clinicDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


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

                           boolean update = true;



                                if (!TextUtils.isEmpty(name)) {
                                    clinic.setName(name);
                                }
                                if (!TextUtils.isEmpty(insurance)) {
                                    clinic.setInsuranceType(insurance);
                                }
                                if (!TextUtils.isEmpty(address)) {

                                    boolean validAddress = fieldValidate.addressValidate(address);

                                    if (!validAddress) {
                                        editTextClinicAddress.setText("");
                                        editTextClinicAddress.setHint("Please enter an address. Ex. 123 Street");
                                        editTextClinicAddress.setHintTextColor(getResources().getColor(R.color.hintFail));
                                        update = false;
                                    }else{
                                        clinic.setAddress(address);
                                    }
                                }
                                if (!TextUtils.isEmpty(phoneNumber)) {


                                    boolean validPhone = fieldValidate.phoneValidate(phoneNumber);

                                    if (!validPhone) {
                                        editTextClinicPhoneNumber.setText("");
                                        editTextClinicPhoneNumber.setHint("Phone number must have 10 digits.");
                                        editTextClinicPhoneNumber.setHintTextColor(getResources().getColor(R.color.hintFail));
                                        update = false;
                                    }else{
                                        clinic.setPhoneNumber(phoneNumber);
                                    }


                                }

                                if (update == true){
                                    clinicDatabase.setValue(clinic);
                                    Toast.makeText(CreateClinic.this, "Clinic Updated", Toast.LENGTH_LONG).show();
                                    Intent returnIntent = new Intent();
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "onCancelled", databaseError.toException());
                        }
                    });



                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });


    }


}
