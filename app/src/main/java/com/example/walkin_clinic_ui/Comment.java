package com.example.walkin_clinic_ui;

public class Comment {
    private String username;
    private String rating;
    private String body;
    private String timeStamp;

    public Comment(String _username, String _rating, String _body, String _timeStamp)
    {
        this.timeStamp = _timeStamp;
        this.username = _username;
        this.rating = _rating;
        this.body = _body;
    }

    public Comment()
    {
        this.username = "ClinicBot";
        this.timeStamp = "January 1 2000 00:00:00";
        this.rating = "No rating";
        this.body = "Open the door, get on the floor, everyone walk the dinosaur";
    }

    public String getUsername(){return username;}
    public String getRating(){return rating;}
    public String getTimeStamp(){return timeStamp;}
    public String getBody(){return body;}

    public String getHeader()
    {
        return this.username + "   " + this.rating + "   " + this.timeStamp;
    }
}
