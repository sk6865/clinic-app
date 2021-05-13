package com.example.walkin_clinic_ui;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.security.MessageDigest;

import static android.os.SystemClock.sleep;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import android.content.Context;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.FixMethodOrder;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.runners.MethodSorters;

import java.security.MessageDigest;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDevice {

    /* If the mock app initalization fails, this flag disables all tests related to the emulated DB. */
    public boolean notWorking = false;

    /* Used for emulated firebase db. */
    //FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("http://10.0.2.2:9000?ns=walk-in-clinic-c322f");
    //FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://walk-in-clinic-c322f.firebaseio.com/");
    //protected FirebaseDatabase mDatabase;
    //protected DatabaseReference mDatabaseRef = mDatabase.getInstance().getReference().child("test");

   // protected FirebaseDatabase mDatabase;
    //protected DatabaseReference mDatabaseRef;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = mDatabase.getInstance().getReference().child("test");

    /* Needed to store retrieved login data. */
    TestAccount currentAccount;
    boolean fetchedAccount = false;

    TestAccount loginAccount = null;
    boolean loggedIn = false;

    boolean duplicateAccount = false;

    boolean listenerComplete = false;

    /* used to test admin deletion. */
    private String patientID;
    private String employeeID;

    //Start test script: firebase emulators:exec --only functions,database "./testdir/test.sh"

    /* Clears DB after testing. DO NOT USE WITH PRODUCTION DB, IT DELETES EVERYTHING! */
    //mDatabase.getReference().setValue(null);


    /* The DB handling methods follow. */


    /**
     * Creates the given account in the emulated DB, provided the given email address doesn't already exist..
     *
     * @param account The account to be created.
     *
     * @return True if creation is successful, false if it isn't.
     */
    public boolean createAccount(TestAccount account) {

        Query userSearch = mDatabaseRef.child("users");

        final String email = account.email;

        ValueEventListener val = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TestAccount test = snapshot.getValue(TestAccount.class);
                        if(email.equals(test.email))
                        {
                            duplicateAccount = true;
                            break;

                        }
                    }
                }

                listenerComplete = true; // used to exit loop once we're done querying.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };

        userSearch.addValueEventListener(val);

        while(!listenerComplete) // Waits for loop iteration to finish.
        {
            sleep(1000);
        }

        userSearch.removeEventListener(val);

        userSearch = null;
        val = null;

        listenerComplete = false; // Resets listener flag for next method call.


        if(duplicateAccount)
        {
            duplicateAccount = false;
            return duplicateAccount;
        }

        mDatabaseRef.child("users").child(account.uid).setValue(account);

        return true;

    }

    /**
     * Retrieves given account from the DB based on UID.
     *
     * @param uid The uid used to search for the user.
     * @return Returns true if account found, false if not.
     */
    public boolean fetchAccount(String uid)
    {
        DatabaseReference user = mDatabaseRef.child("users").child(uid);

        if(user == null)
            return false;

        ValueEventListener loginListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Updates the current account with the one retrieved from DB.
                currentAccount = dataSnapshot.getValue(TestAccount.class);

                if(currentAccount != null)
                    fetchedAccount = true;

                listenerComplete = true;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Triggered if DB read fails.
                fetchedAccount = false;
                // ...

                listenerComplete = true;
            }
        };

        user.addValueEventListener(loginListener);

        while(!listenerComplete) // Waits for loop iteration to finish.
        {
            sleep(1000);
        }

        user.removeEventListener(loginListener);

        listenerComplete = false;
        loginListener = null;
        user = null;

        if(fetchedAccount)
        {
            fetchedAccount = false;
            return true;
        }

        else
            return false;
    }

    /**
     * Simulates login function on the basis of provided username and password.
     *
     * @param email Login email
     * @param password Login password
     * @return true if successfully logged in, false if not.
     */
    public boolean loginAccount(String email, String password)
    {
        if(loggedIn)
            return true;

        /* Searches the entire user tree. */
        Query userSearch = mDatabaseRef.child("users");

        final String email2 = email;
        final String password2 = password;

        ValueEventListener val = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TestAccount test = snapshot.getValue(TestAccount.class);
                        if(email2.equals(test.email) && password2.equals(test.password))
                        {
                            loginAccount = test;
                            loggedIn = true;
                            listenerComplete = true;
                            break;

                        }
                    }
                }

                listenerComplete = true; // used to exit loop once we're done querying.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                loggedIn = false;
            }

        };

        userSearch.addValueEventListener(val);

        while(!listenerComplete) // Waits for loop iteration to finish.
        {
            sleep(1000);
        }

        userSearch.removeEventListener(val);

        listenerComplete = false; // Resets listener flag for next method call.
        userSearch = null;
        val = null;

        return loggedIn;
    }

    /**
     * Simulates logging out. Compares user attempting to logout with currently logged in user,
     * then logs him out if so.
     *
     * @param account Account to be logged out.
     * @return True if successfully logged out, false if not.
     */
    public boolean logOut(TestAccount account)
    {
        if(account == null || loginAccount == null)
            return false;

        if(loginAccount.equals(account))
        {
            loginAccount = null;
            loggedIn = false;
            return true;
        }

        else
            return false;

    }

    /**
     * Checks to see if the specified account exists, then tries to delete it.
     *
     * @param uid The uid of the targeted account.
     * @return True if successfully deleted, false if not found or not deleted.
     */
    public boolean deleteAccount(String uid)
    {
        if(!fetchAccount(uid)) // Exits if we can't find targeted account.
            return false;

        mDatabaseRef.child("users").child(uid).removeValue(); // Removes account.

        if(!fetchAccount(uid)) // If the account is now gone, we've succeeded.
            return true;

        else
            return false;
    }



    /**
     * Several helper methods for the test functions follow.
     */


    /**
     * This method hashes a password.
     *
     * @param password The password to be hashed.
     *
     * @return The hashed password.
     */
    public String passwordHash(String password)
    {
        // Hashes password
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


        } catch (Exception e) { // Fails if we have a hashing error.

            return null;
        }

        return password;
    }

    /**
     * Tests user accounts with unhashed passwords to see if they comply with the rules.
     *
     * @param account The test account to be tested.
     * @return True if valid, false if not.
     */
    public boolean validateAccount(TestAccount account)
    {
        if(fieldValidate.usernameValidate(account.username) & fieldValidate.passwordValidate(account.password) &
                fieldValidate.emailValidate(account.email) & fieldValidate.accountTypeValidate(account.type))
        {
            return true;
        }

        else
            return false;
    }

    /**
     * Randomly generates a set of user account credentials based on current specifications.
     *
     * @param hashPassword Determines whether we hash the password for testing.
     * @param accountType Determines whether we want to create a patient or employee acount.
     * @return A TestAccount fo use in unit testing.
     */
    public TestAccount userGen(String accountType, boolean hashPassword) {

        boolean useLetters = true;
        boolean useNumbers = true;
        int usernameLength = 10; // Username is between 1 and 16 characters currently.
        int uidLength = 28; // UIDs are 28 chars long and contain lowercase letters, uppercase letters, and numbers.

        //Generates a random username based on the specified length
        String username = RandomStringUtils.random(usernameLength, useLetters, useNumbers);

        // Generates an email based on the username and a random domain. Odds of collisions are low
        // enough that I don't think it's worth checking for them ATM.
        String email = username;
        email += "@" + RandomStringUtils.random(usernameLength, useLetters, !useNumbers) + ".com";

        /* Generates a mostly random password based on the password requirements specififed. Will add
        letters and/or numbers if they are specified in the password rules.
         */
        String password = RandomStringUtils.random(fieldValidate.numPasswordCharacters, useLetters & (fieldValidate.requirePasswordUpperCase |
                fieldValidate.requirePasswordLowerCase), useNumbers & fieldValidate.requirePasswordNumbers);

        if (fieldValidate.requirePasswordLowerCase) // Adds an extra upper case letter to make sure we have at least one.
            password += "z";

        if (fieldValidate.requirePasswordUpperCase) // Adds an extra upper case letter to make sure we have at least one.
            password += "A";

        if (fieldValidate.requirePasswordSymbols) // Adds a symbol if we need them because the string generation method doesn't make them.
            password += "$";

        if(fieldValidate.requirePasswordNumbers) // Adds one number to make sure we have one.
            password += "9";

        /* Generates UID for database insertion. */
        String uid = RandomStringUtils.random(uidLength, true, true);

        // Ensures that all randomly generated user attributes are validated, except account type + uid which are manually set/ don't matter.
        if (!(fieldValidate.usernameValidate(username) & fieldValidate.passwordValidate(password) & fieldValidate.emailValidate(email)))
            return null;

        if(hashPassword)
            password = passwordHash(password); // hashes password.

        TestAccount account = new TestAccount(username, password, email, accountType, uid);

        return account;
    }

    public boolean FirebaseInit()
    {

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        /* Attempts to initialize the mock app. Disables all emulated DB tests if it fails. */
        boolean initSuccess = true;

        try {
            FirebaseApp.initializeApp(context);
        }
        catch(Exception e)
        {
            initSuccess = false;
            notWorking = true;
        }

        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());

        return initSuccess;
    }

    /* Here are the testing methods. */

    /**
     * Tests DB functionality. Currently tailored to public read/write rules.
     *
     * TODO: More tests.
     *
     */
    @Test
    public void test02DBValidation()
    {
        if(notWorking)
            return;

        /* Populates test categories */
        TestAccount testPatient = userGen("patient", true);
        TestAccount testEmployee = userGen("employee", true);

        assertFalse("Can login as patient with bad credentials.", loginAccount(testPatient.email, testPatient.password));
        assertFalse("Shouldn't be able to log out user.", logOut(testPatient));

        assertFalse("Can login as employee with bad credentials.", loginAccount(testEmployee.email, testEmployee.password));
        assertFalse("Shouldn't be able to log out user.", logOut(testEmployee));

        /* Add more tests later. */
    }

    /**
     * This tests the patient functionality.
     *
     * Step 1: Randomly generate patient.
     *
     * Step 2: Create Firebase account with given credentials.
     *
     * Step 3: Log into Firebase account.
     *
     * TODO: Testing for patient functionality once implemented.
     */
    @Test
    public void test03Patient()
    {
        if(notWorking)
            return;

        /* Generates Account. */
        TestAccount testAccount = userGen("patient", true);

        boolean accountCreated = createAccount(testAccount);

        assertTrue("Error during patient account creation.", accountCreated);

        if(!accountCreated)
            return;

        boolean loggedIn = loginAccount(testAccount.email, testAccount.password);

        assertTrue("Could not log in as patient", loggedIn);

        //mDatabaseRef.child("users").child(testAccount.uid).setValue(testAccount);

        logOut(testAccount);

        patientID = testAccount.uid;

        assertTrue("Error deleting patient account.", deleteAccount(patientID));

        /**
         * TODO
         *
         * Test patient functionality once it is implemented.
         */
    }

    /**
     * This tests the employee functionality.
     *
     * Step 1: Randomly generate employee.
     *
     * Step 2: Create Firebase account with given credentials.
     *
     * Step 3: Log into Firebase account.
     *
     * TODO: Testing for employee functionality once implemented.
     */
    @Test
    public void test04Employee()
    {
        if(notWorking)
            return;

        /* Generates Account. */
        TestAccount testAccount = userGen("employee", true);

        boolean accountCreated = createAccount(testAccount);

        assertTrue("Error during employee account creation.", accountCreated);

        if(!accountCreated)
            return;

        boolean loggedIn = loginAccount(testAccount.email, testAccount.password);

        assertTrue("Could not log in as employee", loggedIn);

        //mDatabaseRef.child("users").child(testAccount.uid).setValue(testAccount);
        employeeID = testAccount.uid;

        logOut(testAccount);

        assertTrue("Error deleting employee account.", deleteAccount(employeeID));

        /**
         * TODO
         *
         * Test employee functionality once it is implemented.
         */
    }

    /**
     * Tests admin functionality.
     *
     * Step 1: Log in as admin.
     *
     * TODO
     *
     * Step 2: Get list of services from DB.
     *
     * Step 3: Create some new services.
     *
     * Step 4: Edit the new services
     *
     * Step 5: Delete the new services
     *
     * Step 6: Delete the newly created employee and patient accounts via their UIDs.
     */
    @Test
    public void test05Admin()
    {   /*
        TestAccount adminTest = new TestAccount("admin", passwordHash("5T5ptq"), "admin@admin.com", "admin", "0");

        assertFalse("Can login as admin with bad credentials.", loginAccount(adminTest.email, adminTest.password));
        assertFalse("Can create an account with a duplicate email address", createAccount(adminTest));
        */
        if(notWorking)
            return;

        TestAccount adminAccount = new TestAccount("admin", "5T5ptQ", "admin@admin.com", "admin",RandomStringUtils.random(28, true, true));

        boolean adminValidate = validateAccount(adminAccount);

        assertTrue("Admin account could not be validated.", adminValidate);

        if(!adminValidate)
            return;

        else
            adminAccount.password = passwordHash(adminAccount.password);

        boolean accountCreated = createAccount(adminAccount);

        assertTrue("Error during admin account creation.", accountCreated);

        if(!accountCreated)
            return;

        assertTrue("Could not log in as admin", loginAccount(adminAccount.email, adminAccount.password));

        assertTrue("Error logging out admin", logOut(adminAccount));

        assertTrue("Error deleting admin account", deleteAccount(adminAccount.uid));

        /**
         * TODO: add/delete/edit services
         */
    }
}

/*
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.walkin_clinic_ui", appContext.getPackageName());
    }
}
*/