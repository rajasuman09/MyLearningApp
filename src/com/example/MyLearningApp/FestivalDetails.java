package com.example.MyLearningApp;

/**
 * Created by Raja.Chirala on 28/02/2016.
 */
public class FestivalDetails {
    private String serial_num;
    private String event_date = "";
    private String event_name = "";

    public void setSerialNum(String serial_num) {
        this.serial_num = serial_num;
    }

    public String getSerialNum() {
        return serial_num;
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


