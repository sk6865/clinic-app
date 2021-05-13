package com.example.walkin_clinic_ui;

public class Service {
    private String name;
    private String role;
    private String id;


    public Service(String id, String name, String role) {
        this.name = name;
        this.role = role;
        this.id = id;
    }

    public Service(){}


    public void setName(String name) { this.name = name; }
    public String getName() {
        return this.name;
    }
    public String getId(){ return this.id;}
    public void setRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return this.role;
    }
}
