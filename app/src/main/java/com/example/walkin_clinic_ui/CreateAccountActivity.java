package com.example.walkin_clinic_ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;

public class CreateAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "CreateAccountActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        //mDatabase = FirebaseDatabase.getInstance().getReference("accounts");
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Spinner spinner = findViewById(R.id.create_account_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.accounts_create,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected  void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null) {
            // handle user already logged in
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    public void onCreateAccount(View view){

        /* Pulls all needed EditTexts. */
        EditText userText = (EditText)findViewById(R.id.fieldUsernameCreate);
        EditText passwordText = (EditText)findViewById(R.id.fieldPasswordCreate);
        EditText passwordText2 = (EditText)findViewById(R.id.fieldPasswordCreate2);
        EditText emailText = (EditText)findViewById(R.id.fieldEmailCreate);



        /* Retrieved strings used for validation. */
        String username = userText.getText().toString(); // Temporary fix until DB support is working.
        String password = passwordText.getText().toString();
        String passwordConfirm = passwordText2.getText().toString();
        String email = emailText.getText().toString();

        /* Resets the hints after we try new input. Some redundancy, feel free to make a better solution. */
        userText.setHint(getResources().getString(R.string.createUsernameHint));
        userText.setHintTextColor(getResources().getColor(R.color.hintNeutral));
        passwordText.setHint(getResources().getString(R.string.createPasswordHint));
        passwordText.setHintTextColor(getResources().getColor(R.color.hintNeutral));
        passwordText2.setHint(getResources().getString(R.string.createPassword2Hint));
        passwordText2.setHintTextColor(getResources().getColor(R.color.hintNeutral));
        emailText.setHint(getResources().getString(R.string.createEmailHint));
        emailText.setHintTextColor(getResources().getColor(R.color.hintNeutral));

        /* Checks for blank input in either username or password fields, and returns appropriate errors. */
        if(username.length() == 0 || password.length() == 0 || passwordConfirm.length() == 0 || email.length() == 0)
        {
            fieldClear(passwordText);
            fieldClear(passwordText2);

            if(username.length() == 0)
            {
                userText.setHint("Username cannot be blank.");
                userText.setHintTextColor(getResources().getColor(R.color.hintFail));
            }

            if(password.length() == 0 && passwordConfirm.length() == 0)
            {
                passwordText.setHint("Password cannot be blank.");
                passwordText.setHintTextColor(getResources().getColor(R.color.hintFail));
            }

            if(email.length() == 0)
            {
                emailText.setHint("Email address cannot be blank.");
                emailText.setHintTextColor(getResources().getColor(R.color.hintFail));
            }

            return;
        }

        /* Needed because we want to keep validating client-side even if some fields fail, but not
         * continue with the server validation. */
        boolean validateFlag = true;

        /* Calls the client-side user validation method. */
        boolean validUser = fieldValidate.usernameValidate(username);

        /* No sense validating the password if the username fails */
        if(!validUser)
        {
            fieldClear(userText);
            fieldClear(passwordText);
            userText.setHint("Invalid username.");
            userText.setHintTextColor(getResources().getColor(R.color.hintFail));
            validateFlag = false;
        }

        else
        {
            /* Calls the client-side password validation method. */
            boolean validPassword = fieldValidate.passwordValidate(password);

            /* Assume the username is valid if it met our client-side check. The user will find out it
             * isn't once he types in his password correctly. */
            if(!validPassword)
            {
                fieldClear(passwordText);
                fieldClear(passwordText2);
                passwordText.setHint("Incorrect Password.");
                passwordText.setHintTextColor(getResources().getColor(R.color.hintFail));
                validateFlag = false;
            }
        }

        if(password.equals(passwordConfirm) == false)
        {
            fieldClear(passwordText2);
            passwordText2.setHint("Passwords do not match.");
            passwordText2.setHintTextColor(getResources().getColor(R.color.hintFail));
            validateFlag = false;
        }

        boolean validEmail = fieldValidate.emailValidate(email);

        if(!validEmail)
        {
            fieldClear(emailText);
            emailText.setHint("Invalid Email Address.");
            emailText.setHintTextColor(getResources().getColor(R.color.hintFail));
            validateFlag = false;
        }

        /* Client side account type validation. Interim measure. */
        String accountType = ((Spinner)findViewById(R.id.create_account_spinner)).getSelectedItem().toString();
        boolean validAccountType = fieldValidate.accountTypeValidate(accountType);

        if(!validAccountType) // If we haven't selected an account type in the spinner...
        {
            validateFlag = false; // I feel like we don't really need an error message for this, also there's nowhere to put it.
        }

        else
            fieldValidate.accountType = accountType;

        if(!validateFlag)
            return;

        /* If we've gotten this far, we can run server side validation for username and account
        type. Until then, we're stuck with my client side hack.
         */
        fieldValidate.username = username;

        /* This section hashes the password and converts its output to a string. */
        try {

            MessageDigest chomper = MessageDigest.getInstance("SHA-256");
            chomper.update(password.getBytes());

            byte[] passwordBytes = chomper.digest(); // Digest outputs hash as byte array.

            /* Convert bytes to a string so we can prepend the variable byte salt to it. */
            StringBuilder byteConvert = new StringBuilder();
            for (byte b : passwordBytes) {
                byteConvert.append(String.format("%02x", b));
            }

            /* Variables containing unhashed password/intermediaries are nulled so garbage collection
            will remove all instances of the password other than the hash string. */
            password = byteConvert.toString();
            passwordBytes = null;
            chomper = null;
            byteConvert = null;


        } catch(Exception e){ // I'm not sure how we should handle hashing problems.

            fieldClear(passwordText);
            passwordText.setHint("Error Processing Password. Please Contact Technical Support.");
            passwordText.setHintTextColor(getResources().getColor(R.color.hintFail));
        }

        /**
         * TODO
         *
         * Send user account to server to check if it's already in use
         *
         * Send email to server to check if it's already in use.
         *
         * Send salted hash to server.
         *
         * Better error handling for hashing issues.
         */



        if (validateFlag) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Store in database
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String username = ((EditText)findViewById(R.id.fieldUsernameCreate)).getText().toString();
                                String email = ((EditText)findViewById(R.id.fieldEmailCreate)).getText().toString();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username).build();

                                user.updateProfile(profileUpdates);

                                String accountType = ((Spinner)findViewById(R.id.create_account_spinner)).getSelectedItem().toString();

                                writeNewAccount(uid,username,email,accountType);


                            }else{
                                Toast.makeText(CreateAccountActivity.this, "Account Creation failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            Toast.makeText(CreateAccountActivity.this, "Account Created!", Toast.LENGTH_LONG).show();

            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        }


    }

    private void writeNewAccount(String userId, String username, String email, String type) {

            if(type.equals("employee")){

                mDatabase = FirebaseDatabase.getInstance().getReference("users").child("employees");
                EmployeeAccount employeeAccount = new EmployeeAccount(username, email,type, userId);
                mDatabase.child(userId).setValue(employeeAccount);
                mDatabase = FirebaseDatabase.getInstance().getReference("users");

            }else if (type.equals("patient")){
                mDatabase = FirebaseDatabase.getInstance().getReference("users").child("patients");
                PatientAccount patientAccount = new PatientAccount(username, email,type, userId);
                mDatabase.child(userId).setValue(patientAccount);
                mDatabase = FirebaseDatabase.getInstance().getReference("users");
            }else{
                Account account = new Account(username, email,type, userId);
                mDatabase.child(userId).setValue(account);
            }

    }



    /**
     * Clears the field of the provided EditText.
     */
    public void fieldClear(EditText e)
    {
        e.setText("");
    }

}
