package com.jimmy.ebac_estimate.function;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarStringConvert {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String Time;
    Calendar calendar;

    public String CalendarToString(Calendar c){
        Time = (formatter.format(c.getTime()));
        return Time;
    }

    public Calendar StringToCalendar(String Time){
        calendar = Calendar.getInstance();
        try{
            calendar.setTime(formatter.parse(Time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }
}
