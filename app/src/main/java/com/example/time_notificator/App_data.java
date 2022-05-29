package com.example.time_notificator;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.Log;

import java.util.Date;

public class App_data {

    private static Integer interval;
    private static String unit;
    //

    /**
     * Method to set
     * @param interval
     */
    public static void setInterval(Integer interval) {
        App_data.interval = interval;
    }


    /**
     * Method to get
     * @return
     */
    public static Integer getInterval() {
        return interval;
    }

    /**
     * Method to get
     * @return
     */
    public static String getUnit() {
        return unit;
    }

    /**
     * Method to set
     * @param unit
     */
    public static void setUnit(String unit) {
        App_data.unit = unit;
    }

    /************* Other functions *************/
    /*******************************************/




    /**
     * Method which gets interval in mili seconds
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


    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    public static String getNextTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.MILLISECOND, App_data.getIntervalMilisec());
        return formatter.format(calendar.getTime());

    }
}
