package com.example.walkin_clinic_ui;

public class Account {

    public String username;
    public String email;
    public String type;
    public String id;

    public Account() {
    }

    public Account(String username, String email,String type, String id) {
        this.username = username;
        this.email = email;
        this.type = type;
        this.id=id;
    }

    public void setUsername(String name) { this.username = name; }
    public String getUsername() {
        return this.username;
    }
    public String getEmail(){ return this.email;}
    public String getId(){ return this.id;}
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }


}
