package model;

import java.sql.Date;

public class Event {
    private int eventId;
    private String title;
    private Date date;
    private String time;
    private String location;
    private String category;
    private int reminderDays;

    public Event(int eventId, String title, Date date, String time, String location, String category, int reminderDays) {
        this.eventId = eventId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.category = category;
        this.reminderDays = reminderDays;
    }

    public int getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public int getReminderDays() {
        return reminderDays;
    }
}