package com.example.walkin_clinic_ui;

/**
 * Needed to run test class. Stores password and UID (normally insecure)
 */
public class TestAccount extends Account {

    public String uid;
    public String password;

    public TestAccount()
    {
        super();
    }

    public TestAccount(String username, String password, String email,String type, String uid) {
        super(username, email, type, uid);
        this.password = password;
        this.uid = uid;
    }

    public boolean equals(Object object)
    {
        if(this.username == username & this.password == password & this.email == email & this.type == type)
            return true;

        else
            return false;
    }
}
