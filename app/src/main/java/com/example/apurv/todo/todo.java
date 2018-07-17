package com.example.apurv.todo;

import android.util.EventLogTags;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Apurv on 7/5/2018.
 */

public class todo {

    long timedate;
    String date;
    String time;
    String notification_set;
    String repeat_alarm;

    public void setRepeat_alarm(String repeat_alarm) {
        this.repeat_alarm = repeat_alarm;
    }

    public String getNotification_set() {
        return notification_set;
    }

    public void setNotification_set(String notification_set) {
        this.notification_set = notification_set;
    }

    public long getTimedate() {
        return timedate;
    }

    public String getTime() {
        return time;
    }

    public void setTimedate(long timedate) {
        this.timedate = timedate;

        Calendar calendar=Calendar.getInstance();
    calendar.setTimeInMillis(timedate);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);

        date=day+"/"+month+"/"+year;

        int hr=calendar.get(Calendar.HOUR_OF_DAY);
        int min=calendar.get(Calendar.MINUTE);
        time=hr+":"+min;
    }

    String description;
    String name;
        long id;
    public todo(String name, long timedate, String description,long id) {
        this.timedate = timedate;
        this.description = description;
        this.name = name;
        this.id=id;
        repeat_alarm="false";
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getRepeat_alarm() {
        return repeat_alarm;
    }

    public void setId(long id) {
        this.id = id;
    }


}
