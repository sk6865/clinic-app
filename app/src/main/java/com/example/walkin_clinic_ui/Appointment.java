package com.example.walkin_clinic_ui;

public class Appointment {
    public String name;
    public String day;
    public String hour;
    public String minute;
    public String am;

    public Appointment(){}

    public Appointment(String name, String day, String hour, String minute, String am){
        this.name=name;
        this.day=day;
        this.hour=hour;
        this.minute=minute;
        this.am=am;


    }

    public void setName(String type) {

        this.name = name;
    }
    public String getName() {

        return this.name;
    }
    public void setDay(String type) {

        this.day = day;
    }
    public String getDay() {

        return this.day;
    }
    public void setHour(String type) {
        this.hour = hour;
    }
    public String getHour() {

        return this.hour;
    }
    public void setMinute(String type) {

        this.minute = minute;
    }
    public String getMinute() {

        return this.minute;
    }
    public void setAm(String type) {

        this.am = am;
    }
    public String getAm() {

        return this.am;
    }
}
