package com.example.MyLearningApp;

/**
 * Created by Raja.Chirala on 28/02/2016.
 */
public class FestivalDetails {
    private String file_name;
    private String event_date = "";
    private String event_name = "";

    public void setFileName(String file_name) {
        this.file_name = file_name;
    }

    public String getFileName() {
        return file_name;
    }

    public void setEventDate(String event_date) {
        this.event_date = event_date;
    }

    public String getEventDate() {
        return event_date;
    }

    public void setEventName(String event_name) {
        this.event_name = event_name;
    }

    public String getEventName() {
        return event_name;
    }

}


