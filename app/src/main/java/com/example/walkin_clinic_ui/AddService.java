package com.example.walkin_clinic_ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddService extends AppCompatActivity {

    DatabaseReference databaseServices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        databaseServices = FirebaseDatabase.getInstance().getReference("services");


    }

    public void onClickAdd(View view) {


        EditText editTextServiceName = (EditText) findViewById(R.id.editTextName);
        EditText editTextServiceRole = (EditText) findViewById(R.id.editTextRole);

        String name = editTextServiceName.getText().toString();
        String role = editTextServiceRole.getText().toString();

        boolean validService = fieldValidate.serviceValidate(name);
        boolean validRole = fieldValidate.roleValidate(role);

        if (TextUtils.isEmpty(name)) {

            editTextServiceName.setText("");
            editTextServiceName.setHint("Please enter a name");
            editTextServiceName.setHintTextColor(getResources().getColor(R.color.hintFail));

        } else if (TextUtils.isEmpty(role) && !TextUtils.isEmpty(name)) {

            editTextServiceRole.setText("");
            editTextServiceRole.setHint("Please enter a role");
            editTextServiceRole.setHintTextColor(getResources().getColor(R.color.hintFail));

        } else if(!validService)
        {
            fieldClear(editTextServiceName);
            fieldClear(editTextServiceRole);
            editTextServiceName.setHint("Service can only contain letters");
            editTextServiceName.setHintTextColor(getResources().getColor(R.color.hintFail));
            return;
        }

        else if(!validRole)
        {
            fieldClear(editTextServiceName);
            fieldClear(editTextServiceRole);
            editTextServiceRole.setHint("Role must be Staff, Doctor or Nurse");
            editTextServiceRole.setHintTextColor(getResources().getColor(R.color.hintFail));
            return;
        }
        else {

            String id = databaseServices.push().getKey();
            Service service = new Service(id, name, role);

            databaseServices.child(id).setValue(service);

            editTextServiceName.setText("");
            editTextServiceRole.setText("");

            editTextServiceName.setHint("What is the service?");
            editTextServiceName.setHintTextColor(getResources().getColor(R.color.hintNeutral));
            editTextServiceRole.setHint("Who provides this service?");
            editTextServiceRole.setHintTextColor(getResources().getColor(R.color.hintNeutral));

            Toast.makeText(AddService.this, "Service Added", Toast.LENGTH_LONG).show();
        }


    }

    public void onClickCancel(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Clears the field of the provided EditText.
     */
    public void fieldClear(EditText e)
    {
        e.setText("");
    }

}



