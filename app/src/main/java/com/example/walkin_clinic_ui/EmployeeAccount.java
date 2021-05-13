package com.example.walkin_clinic_ui;

public class EmployeeAccount extends Account {

    //public String username;
    //public String email;
    //public String type;
    //public String id;
    private String clinicID;

    public EmployeeAccount(){}

    public EmployeeAccount(String username, String email, String type, String id) {
        super(username, email, "employee", id);


    }

    public void addClinic(String clinicID){
        this.clinicID = clinicID;
    }

    public String getClinicID() { return this.clinicID; }

    public void setClinicID(String id) { this.clinicID = id; }
    //public String getType() {return type; }
}
