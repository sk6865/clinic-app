package com.example.walkin_clinic_ui;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;

public class AdminWelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "AdminWelcomeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_admin_welcome);

        String welcomeMessage = "Welcome, " + fieldValidate.username + "!";


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Account account = dataSnapshot.getValue(Account.class);

                    String accountMessage = "Account type: " + account.type ;

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

    //addService method
    public void onAddService (View view) {
        Intent myIntent = new Intent(getBaseContext(), AddService.class);
        startActivity(myIntent);
    }

    //createClinic method
    public void onDeleteAccount (View view) {
        Intent myIntent = new Intent(getBaseContext(), DeleteAccount.class);
        startActivity(myIntent);
    }


    //UpdateService method
    public void onDeleteService (View view) {
        Intent myIntent = new Intent(getBaseContext(), DeleteService.class);
        startActivity(myIntent);
    }

}
