package com.example.walkin_clinic_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.Toast;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.spinnerLogin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.accounts_login,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void OnCreateAccountButton(View view){
        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivityForResult (intent,0);
    }


    /**
     * This method gets called when the login button gets clicked. Does all possible client side
     * validation, then sends the information to the server for validation.
     *
     * Currently uses email/password to login. We may change to username/password later.
     */
    public void onClickLogin(View view)
    {
        EditText userText = (EditText)findViewById(R.id.fieldUsernameLogin);
        EditText passwordText = (EditText)findViewById(R.id.fieldPasswordLogin);

        String username = userText.getText().toString();
        String password = passwordText.getText().toString();

        /* Resets the hints after we try new input. Some redundancy, feel free to make a better solution. */
        userText.setHint(getResources().getString(R.string.loginUsernameHint));
        userText.setHintTextColor(getResources().getColor(R.color.hintNeutral));
        passwordText.setHint(getResources().getString(R.string.loginPasswordHint));
        passwordText.setHintTextColor(getResources().getColor(R.color.hintNeutral));

        /* Checks for blank input in either username or password fields, and returns appropriate errors. */
        if(username.length() == 0 || password.length() == 0)
        {
            if(username.length() == 0)
            {
                userText.setHint("Email address cannot be blank.");
                userText.setHintTextColor(getResources().getColor(R.color.hintFail));
                fieldClear(passwordText);
            }

            if(password.length() == 0)
            {
                passwordText.setHint("Password cannot be blank.");
                passwordText.setHintTextColor(getResources().getColor(R.color.hintFail));
            }

            return;
        }

        /* Calls the client-side email validation method. */
        boolean validUser = fieldValidate.emailValidate(username);

        /* No sense validating the password if the username fails */
        if(!validUser)
        {
            fieldClear(userText);
            fieldClear(passwordText);
            userText.setHint("Invalid email address.");
            userText.setHintTextColor(getResources().getColor(R.color.hintFail));
            return;
        }

        /* Calls the client-side password validation method. */
        boolean validPassword = fieldValidate.passwordValidate(password);

        /* Assume the username is valid if it met our client-side check. The user will find out it
         * isn't once he types in his password correctly. */
        if(!validPassword)
        {
            fieldClear(passwordText);
            passwordText.setHint("Incorrect Password.");
            passwordText.setHintTextColor(getResources().getColor(R.color.hintFail));
            return;
        }

        /* Client side account type validation. Interim measure. */
        String accountType = ((Spinner)findViewById(R.id.spinnerLogin)).getSelectedItem().toString();
        boolean validAccountType = fieldValidate.accountTypeValidate(accountType);

        if(!validAccountType) // If we haven't selected an account type in the spinner...
        {
            return; // I feel like we don't really need an error message for this, also there's nowhere to put it.
        }

        else
            fieldValidate.accountType = accountType;

        /* If we've gotten this far, we can run server side validation. */

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
         * Send user string to server for validation
         *
         * Receive variable byte salt from server and prepend it to hash before sending it out.
         *
         * Send salted hash to server.
         *
         * Better error handling for hashing issues.
         */


        /**
         * IF THE LOGIN CREDENTIALS ARE VALID, LAUNCH THE WELCOME SCREEN
         */
        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    String uid = user.getUid();

                    String accountType = ((Spinner)findViewById(R.id.spinnerLogin)).getSelectedItem().toString();

                    if(accountType.equals("employee")) {

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("employees").child(uid);



                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {

                                    EmployeeAccount employeeAccount = dataSnapshot.getValue(EmployeeAccount.class);

                                    String userType = employeeAccount.type;

                                    if (!userType.equals("employee")) {
                                        Toast.makeText(MainActivity.this, "Login failed. You do not have an employee account.", Toast.LENGTH_LONG).show();
                                        FirebaseAuth.getInstance().signOut();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Login Success!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), EmployeeWelcomeActivity.class);
                                        startActivityForResult(intent, 0);
                                    }
                                }else{Toast.makeText(MainActivity.this, "Login failed. Your account has been deleted.", Toast.LENGTH_LONG).show();}
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "onCancelled", databaseError.toException());
                            }
                        });

                    }else if(accountType.equals("patient")) {

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("patients").child(uid);

                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                PatientAccount account = dataSnapshot.getValue(PatientAccount.class);
                                if(dataSnapshot.exists()) {

                                    String userType = account.getType();

                                    if (!userType.equals("patient")) {
                                        Toast.makeText(MainActivity.this, "Login failed. You do not have a patient account.", Toast.LENGTH_LONG).show();
                                        FirebaseAuth.getInstance().signOut();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Login Success!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), PatientWelcomeActivity.class);
                                        startActivityForResult(intent, 0);
                                    }
                                }else{Toast.makeText(MainActivity.this, "Login failed. Your account has been deleted.", Toast.LENGTH_LONG).show();}
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "onCancelled", databaseError.toException());
                            }
                        });

                    }


                } else {
                    Toast.makeText(MainActivity.this, "Login failed!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Clears the field of the provided EditText.
     */
    public void fieldClear(EditText e)
    {
        e.setText("");
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showAdminLoginDialog(View view) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.admin_login_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextUsername = (EditText) dialogView.findViewById(R.id.editTextUsername);
        final EditText editTextPassword  = (EditText) dialogView.findViewById(R.id.editTextPassword);
        final Button buttonCancel = (Button) dialogView.findViewById(R.id.button_cancel_login);
        final Button buttonLogin = (Button) dialogView.findViewById(R.id.button_admin_login);

        dialogBuilder.setTitle("Login as an Admin");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextUsername.getText().toString() + "@admin.com";
                String pass = editTextPassword.getText().toString();

                try {

                    MessageDigest chomper = MessageDigest.getInstance("SHA-256");
                    chomper.update(pass.getBytes());

                    byte[] passwordBytes = chomper.digest(); // Digest outputs hash as byte array.

                    /* Convert bytes to a string so we can prepend the variable byte salt to it. */
                    StringBuilder byteConvert = new StringBuilder();
                    for (byte b : passwordBytes) {
                        byteConvert.append(String.format("%02x", b));
                    }

            /* Variables containing unhashed password/intermediaries are nulled so garbage collection
            will remove all instances of the password other than the hash string. */
                    pass = byteConvert.toString();
                    passwordBytes = null;
                    chomper = null;
                    byteConvert = null;


                } catch(Exception e){ // I'm not sure how we should handle hashing problems.

                    fieldClear(editTextPassword);
                    editTextPassword.setHint("Error Processing Password. Please Contact Technical Support.");
                    editTextPassword.setHintTextColor(getResources().getColor(R.color.hintFail));
                }


                if (!TextUtils.isEmpty(name)) {

                    mAuth.signInWithEmailAndPassword(name, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                String uid = user.getUid();



                                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                                mDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Account account = dataSnapshot.getValue(Account.class);

                                        String userType =  account.type ;

                                        if (!userType.equals("admin")){
                                            Toast.makeText(MainActivity.this, "Login failed!", Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().signOut();
                                        }else{
                                            Toast.makeText(MainActivity.this, "Login Success!", Toast.LENGTH_LONG).show();
                                            //Intent intent = new Intent(getApplicationContext(), EmployeeWelcomeActivity.class);
                                            Intent intent = new Intent(getApplicationContext(), AdminWelcomeActivity.class);
                                            startActivityForResult (intent,0);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "onCancelled", databaseError.toException());
                                    }
                                });

                            } else {
                                Toast.makeText(MainActivity.this, "Login failed!", Toast.LENGTH_LONG).show();
                                fieldClear(editTextPassword);
                                editTextPassword.setHint("Incorrect Password.");
                                editTextPassword.setHintTextColor(getResources().getColor(R.color.hintFail));
                                fieldClear(editTextUsername);
                                editTextUsername.setHint("Incorrect Username.");
                                editTextUsername.setHintTextColor(getResources().getColor(R.color.hintFail));
                            }
                        }
                    });
                    b.dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deleteProduct(productId);
                b.dismiss();
            }
        });
    }

}
