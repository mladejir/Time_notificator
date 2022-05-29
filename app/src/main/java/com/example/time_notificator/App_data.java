package com.example.time_notificator;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

/**
 * @author Jiri Mladek
 * Class which holds app data
 */
public class App_data {

    private static Integer interval;
    private static String unit;

    /**
     * Method to set interval number
     * @param interval
     */
    public static void setInterval(Integer interval) {
        App_data.interval = interval;
    }

    /**
     * Method to get interval number
     * @return interval number
     */
    public static Integer getInterval() {
        return interval;
    }

    /**
     * Method to set interval unit (seconds/minutes/hours)
     * @param unit
     */
    public static void setUnit(String unit) {
        App_data.unit = unit;
    }

    /**
     * Method to get interval unit (seconds/minutes/hours)
     * @return interval unit
     */
    public static String getUnit() {
        return unit;
    }

    /************* Other functions *************/
    /*******************************************/

    /**
     * Method which gets interval in milli seconds
     * @return
     */
    public static Integer getIntervalMilisec(){
        int metric = 1;
        if(App_data.getUnit().equals("minutes")){
            metric = 60;
        }
        if(App_data.getUnit().equals("hours")){
            metric = 3600;
        }
        return App_data.getInterval() * 1000 * metric;
    }

    /**
     * Method which gets current date and time and formats the string
     * @return current date and time
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    /**
     * Method which gets the date and time of next notification
     * @return date and time of next notification
     */
    public static String getNextTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.MILLISECOND, App_data.getIntervalMilisec());
        return formatter.format(calendar.getTime());
    }
}
