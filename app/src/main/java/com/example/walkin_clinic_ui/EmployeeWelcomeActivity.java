package com.example.walkin_clinic_ui;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployeeWelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "EmployeeWelcomeActivity";
    private EmployeeAccount employee;
    Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_welcome_screen);

        String welcomeMessage = "Welcome, " + fieldValidate.username + "!";


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Button addService = (Button) findViewById(R.id.addService);
        Button deleteService = (Button) findViewById(R.id.deleteService);

        addService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployeeWelcomeActivity.this, EmployeeAddService.class));
            }
        });

        deleteService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployeeWelcomeActivity.this, EmployeeDeleteService.class));
            }
        });

        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("employees").child(uid);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    employee = dataSnapshot.getValue(EmployeeAccount.class);

                    String accountMessage = "Account type: " + employee.type ;

                    btn = (Button) findViewById(R.id.btnCompleteProfile);

                    ((TextView)findViewById(R.id.account_type)).setText(accountMessage);

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

    /**
     * Allows employee to set up or edit his clinic profile.
     * @param view
     */
    public void onCompleteClinicProfile (View view) {
        Intent myIntent = new Intent(getBaseContext(), CreateClinic.class);
        startActivity(myIntent);
    }

    /**
     * Allows user to view/change a clinic schedule if a profile has been set up.
     *
     * @param view
     */
    public void onViewSchedule (View view) {

        if(employee.getClinicID() == null)
            Toast.makeText(EmployeeWelcomeActivity.this, "You must set up a clinic before you can view the schedule.", Toast.LENGTH_LONG).show();

        else {
            /* Creates new intent for schedule viewing page, then passes it clinic id. */
            String ClinicID = employee.getClinicID();
            Intent myIntent = new Intent(getBaseContext(), ScheduleConfirm.class);
            myIntent.putExtra("ID", ClinicID);
            startActivity(myIntent);
        }
    }


    public void onAddServices (View view) {

        if(employee.getClinicID() == null)
            Toast.makeText(EmployeeWelcomeActivity.this, "You must set up a clinic before you can view the schedule.", Toast.LENGTH_LONG).show();

        else {
            /* Creates new intent for schedule viewing page, then passes it clinic id. */
            String ClinicID = employee.getClinicID();
            Intent myIntent = new Intent(getBaseContext(), EmployeeAddService.class);
            myIntent.putExtra("ID", ClinicID);
            startActivity(myIntent);
        }
    }







}