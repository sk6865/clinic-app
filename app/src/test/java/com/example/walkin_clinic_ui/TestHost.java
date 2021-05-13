package com.example.walkin_clinic_ui;


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

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestHost {

    /* This is the Unit Testing Class. All Tests Cases requiring DB and other Firebase modules
    are located in the instrumentation test folder. */

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

    /* Here are the testing methods. */


    /**
     * This tests all of the field validation methods.
     *
     * Step 1: Tests our means of randomly generating users for the test class.
     *
     * Step 2: Tests known success and failure cases for current constraints.
     *
     * Step 3: Tests functionality of hashing algorithm.
     *
     * TODO: Test field validation for services.
     *
     */
    @Test
    public void test01FieldValidation()
    {
        /* Begin by testing user generation method. Randomly generates 50 employees and 50 patients. */
        boolean successFlag = true;

        for(int i = 0; i < 50; i++)
        {
            TestAccount acc1 = userGen("employee", true);
            TestAccount acc2 = userGen("patient", true);

            if(acc1 == null || acc2 == null)
                successFlag = false;


        }

        assertTrue("Automated user generation failed field validation.", successFlag);
    }

    @Test
    public void test011FieldValidation()
    {
        String[] usernameSuccessTests = {"mybelle42", "aztecRain" , "wolf_and_moon", "liberty-cabbage" , "FRIEDBANANA", "Krakatoa", "barn"};
        String[] passwordSuccessTests = {"GLORY2wind", "123Fragaboolas", "$Bounded$8", "ir_1337_DOOD", "5T5ptQ", "Chumble@3#Spuzz", "ch33se-is-AWESOME"};
        String[] emailSuccessTests = {"admin@admin.com", "pierre.trudeau@justintrudeau.org", "dolphins@whales.cx", "5guys@fiveguys.net",
                "fried-balogna@sandwich.ca", "1234@1234.za", "GORGONZOLA@ipswitch.uk"};

        String[] serviceSuccessTests = {"X-Ray", "Blood Tests", "Checkup", "Ear Wax Removal", "immunization", "a", "da da da"};

        for(int j = 0; j < 7; j++) {
            assertTrue("Username failed to validate", fieldValidate.usernameValidate(usernameSuccessTests[j]));
            assertTrue("Password failed to validate", fieldValidate.passwordValidate(passwordSuccessTests[j]));
            assertTrue("Email address failed to validate", fieldValidate.emailValidate(emailSuccessTests[j]));
            assertTrue("Service failed to validate", fieldValidate.serviceValidate(serviceSuccessTests[j]));
        }
    }

    @Test
    public void test012FieldValidation()
    {
        String[] usernameFailTests = {null, "", " ", ".65", "%", "Drink Coke", "&Barbie"};
        String[] passwordFailTests = {null, "", " ", "666666", "artichoke", "Jimothy", "&^$#@^&^"};
        String[] emailFailTests = {null, "", " ", "rocks", "a@a", "$$$@hi.com", "ninja@screaming.com%%%"};
        String[] serviceFailTests = {null, "", " ", " stew", "$5 amputations", "/bar/", "free_drugs"};

        for(int j = 0; j < 7; j++) {
            assertFalse("Bad username is validated.", fieldValidate.usernameValidate(usernameFailTests[j]));
            assertFalse("Bad password is validated.", fieldValidate.passwordValidate(passwordFailTests[j]));
            assertFalse("Bad email address is validated.", fieldValidate.emailValidate(emailFailTests[j]));
            assertFalse("Bad service is validated.", fieldValidate.serviceValidate(serviceFailTests[j]));
        }
    }

    @Test
    public void test013FieldValidation()
    {
        String[] accountTypeSuccessTests = {"employee", "patient", "admin"};
        String[] accountTypeFailTests = {null, "", " ", "cannibal", "34567", "$", "employe"};

        for(int j = 0; j < 7; j++) {

            if (j < 3)
                assertTrue("Account type failed to validate", fieldValidate.accountTypeValidate(accountTypeSuccessTests[j]));

            assertFalse("Bad account type is validated.", fieldValidate.accountTypeValidate(accountTypeFailTests[j]));
        }
    }

    @Test
    public void test014FieldValidation()
    {
        /* We now test the hashing function. */
        String password = "123ThisWorks";
        String password1 = "123ThisWork";
        String passwordHashed = passwordHash(password);
        String password1Hashed = passwordHash(password1);

        assertNotEquals("Password is the same as hashed password." ,password, passwordHashed);
        assertNotEquals("Hash collision on generated passwords.", passwordHashed, password1Hashed);
    }

    @Test
    public void test015FieldValidation()
    {
        String[] serviceTypeSuccessTests = {"Staff", "Nurse", "Doctor"};
        String[] serviceTypeFailTests = {null, "", " ", "staf", "84756", "$", "tester"};

        for(int j = 0; j < 7; j++) {

            if (j < 3)
                assertTrue("Service role failed to validate", fieldValidate.roleValidate(serviceTypeSuccessTests[j]));

            assertFalse("Bad Service role is validated.", fieldValidate.roleValidate(serviceTypeFailTests[j]));
        }
    }

    @Test
    public void test016FieldValidation()
    {
        String[] phoneSuccessTests = {"123-123-1231", "1231231231"};
        String[] phoneFailTests = {null, "", "123", "wrong", "$"};

        for(int j = 0; j < 5; j++) {

            if (j < 2)
                assertTrue("Phone number failed to validate", fieldValidate.phoneValidate(phoneSuccessTests[j]));

            assertFalse("Bad phone number is validated.", fieldValidate.phoneValidate(phoneFailTests[j]));
        }
    }
}