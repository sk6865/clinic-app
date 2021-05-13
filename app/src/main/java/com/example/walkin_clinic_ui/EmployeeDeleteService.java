package com.example.walkin_clinic_ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDeleteService extends AppCompatActivity {

    ListView listViewServices;
    DatabaseReference databaseServices;

    List<Service> services;

    public String newid;
    private DatabaseReference clinicDatabase;

    private FirebaseAuth mAuth;

    private static final String TAG = "EmployeeDeleteService";
    private DatabaseReference mDatabase;
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_remove_service);



        listViewServices = findViewById(R.id.listViewClinics);

        services = new ArrayList<>();

        listViewServices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Service service = services.get(i);

                System.out.println(service.getId());
                showRemoveServiceDialog(service.getId());
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("employees").child(uid);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EmployeeAccount account = dataSnapshot.getValue(EmployeeAccount.class);

                String clinicID = account.getClinicID();


                if (clinicID != null) {

                    databaseServices = FirebaseDatabase.getInstance().getReference("clinics").child(clinicID).child("services");

                    databaseServices.addValueEventListener(new ValueEventListener() {

                        public void onDataChange(DataSnapshot dataSnapshot){

                            services.clear();

                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                                Service service = postSnapshot.getValue(Service.class);
                                services.add(service);
                            }

                            ServiceList servicesAdapter = new ServiceList(EmployeeDeleteService.this, services);
                            listViewServices.setAdapter(servicesAdapter);
                        }

                        public void onCancelled(DatabaseError databaseError){

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

    private void showRemoveServiceDialog(final String id) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.remove_service_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonCancel = (Button) dialogView.findViewById(R.id.button_delete_cancel_);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.button_remove_);



        dialogBuilder.setTitle("Add Service");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("employees").child(uid);

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        EmployeeAccount account = dataSnapshot.getValue(EmployeeAccount.class);

                        String clinicID = account.getClinicID();

                        if (clinicID != null) {

                            clinicDatabase = FirebaseDatabase.getInstance().getReference("clinics").child(clinicID).child("services");

                            databaseServices.child(id).removeValue();
                            Toast.makeText(getApplicationContext(), "Service Deleted", Toast.LENGTH_LONG).show();

                            b.dismiss();



                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });



                Toast.makeText(getApplicationContext(), "Service Added", Toast.LENGTH_LONG).show();

                b.dismiss();
            }


        });


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });
    }


}
