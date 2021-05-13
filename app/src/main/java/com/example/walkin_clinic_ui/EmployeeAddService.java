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

public class EmployeeAddService extends AppCompatActivity {

    ListView listViewServices;
    DatabaseReference databaseServices;

    List<Service> services;

    public String newid;

    private FirebaseAuth mAuth;
    private static final String TAG = "EmployeeAddService";
    private DatabaseReference mDatabase;
    private DatabaseReference clinicDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_add_service);

        databaseServices = FirebaseDatabase.getInstance().getReference("services");


        listViewServices = findViewById(R.id.listViewClinics);

        services = new ArrayList<>();

        listViewServices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Service service = services.get(i);

                System.out.println(service.getId());
                showAddServiceDialog(service.getId(),service.getName(), service.getRole());
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseServices.addValueEventListener(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot){

                services.clear();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Service service = postSnapshot.getValue(Service.class);
                    services.add(service);
                }

                ServiceList servicesAdapter = new ServiceList(EmployeeAddService.this, services);
                listViewServices.setAdapter(servicesAdapter);
            }

            public void onCancelled(DatabaseError databaseError){

            }
        });
    }

    private void showAddServiceDialog(final String id, final String serviceName, final String serviceRole) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_service_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonCancel = (Button) dialogView.findViewById(R.id.button_delete_cancel_);
        final Button buttonAdd = (Button) dialogView.findViewById(R.id.button_remove_);

        System.out.println(id);


        dialogBuilder.setTitle("Add Service");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println(id);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("employees").child(uid);

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        EmployeeAccount account = dataSnapshot.getValue(EmployeeAccount.class);

                        String clinicID = account.getClinicID();

                        System.out.println("Clinic ID = " + clinicID);

                        if (clinicID != null) {

                            clinicDatabase = FirebaseDatabase.getInstance().getReference("clinics").child(clinicID).child("services");


                            Service service = new Service(id,serviceName,serviceRole);
                            clinicDatabase.child(id).setValue(service);



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
