package com.example.walkin_clinic_ui;

public class PatientAccount extends Account{

    public PatientAccount(){}

    public PatientAccount(String username, String email, String type, String id) {
        super(username, email, "patient", id);
    }

    @Override
    public String getType() { return type; }

    public String getUsername(){
        return username;
    }
}
