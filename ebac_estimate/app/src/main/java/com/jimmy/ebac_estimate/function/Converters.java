package com.jimmy.ebac_estimate.function;

import androidx.room.TypeConverter;

import java.util.Calendar;

public class Converters {

    @TypeConverter
    public static Calendar fromTimestamp(Long value) {
        if(value == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(value);
        return calendar;
    }

    @TypeConverter
    public static Long toTimestamp(Calendar calendar) {
        if(calendar == null) return null;
        return calendar.getTimeInMillis();
    }
}
