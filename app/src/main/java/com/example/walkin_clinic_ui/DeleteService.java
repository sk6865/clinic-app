package com.example.walkin_clinic_ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeleteService extends AppCompatActivity {
    //update services page

    ListView listViewServices;
    DatabaseReference databaseServices;

    List<Service> services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_service);

        databaseServices = FirebaseDatabase.getInstance().getReference("services");


        listViewServices = findViewById(R.id.listViewClinics);

        services = new ArrayList<>();

        listViewServices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Service service = services.get(i);
                showUpdateServiceDialog(service.getId(),service.getName(), service.getRole());
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

                ServiceList servicesAdapter = new ServiceList(DeleteService.this, services);
                listViewServices.setAdapter(servicesAdapter);
            }

            public void onCancelled(DatabaseError databaseError){

            }
        });
    }


    public void onClickCancel(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    private void showUpdateServiceDialog(final String id, final String serviceName, final String serviceRole) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_service_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextServiceName = (EditText) dialogView.findViewById(R.id.editTextUpdateName);
        final EditText editTextServiceRole  = (EditText) dialogView.findViewById(R.id.editTextUpdateRole);
        final Button buttonCancel = (Button) dialogView.findViewById(R.id.button_delete_cancel_);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.button_delete__service);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.button_remove_);


        editTextServiceName.setHint(serviceName);
        editTextServiceRole.setHint(serviceRole);



        dialogBuilder.setTitle("Update Service");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextServiceName.getText().toString();
                String role = editTextServiceRole.getText().toString();

                if(TextUtils.isEmpty(name) && (!TextUtils.isEmpty(role))){

                    if(!fieldValidate.roleValidate(role)) {

                        editTextServiceRole.setText("");

                        Toast.makeText(getApplicationContext(), "Invalid Role", Toast.LENGTH_LONG).show();
                    }

                    else {

                        Service service = new Service(id, serviceName, role);
                        databaseServices.child(id).setValue(service);
                        Toast.makeText(getApplicationContext(), "Service updated", Toast.LENGTH_LONG).show();

                        editTextServiceName.setText("");
                        editTextServiceRole.setText("");
                        b.dismiss();
                    }

                }else if (TextUtils.isEmpty(role) && (!TextUtils.isEmpty(name))){

                    if(!fieldValidate.serviceValidate(name)) {

                        editTextServiceName.setText("");

                        Toast.makeText(getApplicationContext(), "Invalid Service", Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        Service service = new Service(id, name,serviceRole);
                        databaseServices.child(id).setValue(service);
                        Toast.makeText(getApplicationContext(), "Service updated", Toast.LENGTH_LONG).show();

                        editTextServiceName.setText("");
                        editTextServiceRole.setText("");
                        b.dismiss();
                    }

                }else if ((!TextUtils.isEmpty(role)) && (!TextUtils.isEmpty(name))){

                    if(fieldValidate.serviceValidate(name) && fieldValidate.roleValidate(role)) {
                        Service service = new Service(id, name, role);
                        databaseServices.child(id).setValue(service);
                        Toast.makeText(getApplicationContext(), "Service updated", Toast.LENGTH_LONG).show();

                        editTextServiceName.setText("");
                        editTextServiceRole.setText("");
                        b.dismiss();
                    }

                    else
                    {
                        editTextServiceName.setText("");
                        editTextServiceRole.setText("");

                        Toast.makeText(getApplicationContext(), "Invalid Service/Role", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Service updated", Toast.LENGTH_LONG).show();

                    editTextServiceName.setText("");
                    editTextServiceRole.setText("");
                    b.dismiss();
                }
            }});

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                databaseServices.child(id).removeValue();
                Toast.makeText(getApplicationContext(), "Service Deleted", Toast.LENGTH_LONG).show();

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

