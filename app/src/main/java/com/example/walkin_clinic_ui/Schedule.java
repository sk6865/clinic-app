package com.example.walkin_clinic_ui;

public class Schedule {

    public ScheduleDay Monday;
    public ScheduleDay Tuesday;
    public ScheduleDay Wednesday;
    public ScheduleDay Thursday;
    public ScheduleDay Friday;
    public ScheduleDay Saturday;
    public ScheduleDay Sunday;


    /**
     * Default constructor generates a new schedule object with default values.
     *
     * Default day names
     *
     * Default opening times: 12:00 AM - 12:00 PM (range for each day is 12:00 AM - 11:59 PM)
     *
     * Days are all open by default.
     */
    public Schedule()
    {
        this.Monday = new ScheduleDay("Monday");
        this.Tuesday = new ScheduleDay("Tuesday");
        this.Wednesday = new ScheduleDay("Wednesday");
        this.Thursday = new ScheduleDay("Thursday");
        this.Friday = new ScheduleDay("Friday");
        this.Saturday = new ScheduleDay("Saturday");
        this.Sunday = new ScheduleDay("Sunday");
    }

    /**
     * Updates a day of the schedule with a provided day object.
     *
     * @param day The ScheduleDay to be changed.
     */
    public void updateDay(ScheduleDay day)
    {
        switch(day.name)
        {
            case("Monday"):
                this.Monday = day;
                break;

            case("Tuesday"):
                this.Tuesday = day;
                break;

            case("Wednesday"):
                this.Wednesday = day;
                break;

            case("Thursday"):
                this.Thursday = day;
                break;

            case("Friday"):
                this.Friday = day;
                break;

            case("Saturday"):
                this.Saturday = day;
                break;

            case("Sunday"):
                this.Sunday = day;
                break;

            default:
                break;
        }
    }

    /**
     * Retrieves a given day object based on string input.
     *
     * @param dayName The name of the day we want.
     *
     * @return The corresponding day object.
     */
    public ScheduleDay retrieveDay(String dayName)
    {
        switch(dayName)
        {
            case("Monday"):
                return this.Monday;

            case("Tuesday"):
                return this.Tuesday;

            case("Wednesday"):
                return this.Wednesday;

            case("Thursday"):
                return this.Thursday;

            case("Friday"):
                return this.Friday;

            case("Saturday"):
                return this.Saturday;

            case("Sunday"):
                return this.Sunday;

            default:
                return null;
        }
    }

    /**
     * Closes the clinic for a given day.
     *
     * @param dayName The name of the day to be closed.
     */
    public void closeFlip(String dayName)
    {
        switch(dayName)
        {
            case("Monday"):
                this.Monday.closeFlip();
                break;

            case("Tuesday"):
                this.Tuesday.closeFlip();
                break;

            case("Wednesday"):
                this.Wednesday.closeFlip();
                break;

            case("Thursday"):
                this.Thursday.closeFlip();
                break;

            case("Friday"):
                this.Friday.closeFlip();
                break;

            case("Saturday"):
                this.Saturday.closeFlip();
                break;

            case("Sunday"):
                this.Sunday.closeFlip();
                break;

            default:
                break;
        }
    }
}
