package com.example.walkin_clinic_ui;

public class ScheduleDay {

    /* Time is broken into chunks that make it easier and more intuitive to manipulate. */
    public String startHour;
    public String startMinute;
    public String startAMPM;
    public String endHour;
    public String endMinute;
    public String endAMPM;

    public boolean isClosed; // Determines if clinic is closed on this day. True if yes, false if not.

    public String name; // The name of the day, ie: "Monday".

    /**
     * Constructors assign default values for all fields.
     *
     * Schedule class instantiates day objects with proper day names, so "Jamesday" should never
     * occur unless something goes horribly wrong somewhere.
     */
    public ScheduleDay()
    {
        this.name = "Jamesday";
        this.startHour = "12";
        this.startMinute = "00";
        this.startAMPM = "AM";
        this.endHour = "12";
        this.endMinute = "00";
        this.endAMPM = "PM";
        this.isClosed = false;
    }

    public ScheduleDay(String name)
    {
        this.name = name;
        this.startHour = "12";
        this.startMinute = "00";
        this.startAMPM = "AM";
        this.endHour = "12";
        this.endMinute = "00";
        this.endAMPM = "PM";
        this.isClosed = false;
    }

    /**
     * Switches whether the clinic is open/closed on this day.
     */
    public void closeFlip()
    {
        if(this.isClosed)
            this.isClosed = false;

        else
            this.isClosed = true;
    }

    /**
     * Prints out the status of this day for the confirm schedule screen.
     *
     * @return A string stating the opening hours for this day.
     */
    public String toString()
    {
        String resultant = name + ":  ";

        if(isClosed)
            return resultant + "CLOSED";

        else
            return resultant + startHour + ":" + startMinute + " " + startAMPM + " - " +
                    endHour + ":" + endMinute + " " + endAMPM;
    }

    public boolean getClosed(){
        return isClosed;
    }
}
