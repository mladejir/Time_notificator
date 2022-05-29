package com.example.time_notificator;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * @author Jiri Mladek
 * Class representing background service of notification
 */
public class NotifierBackground extends Service {

    private NotificationManagerCompat notifManagerCompat;
    private Notification notification;
    private static boolean exit;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        exit = false;

        //For Android 8.0+
        createNotificationChannel();

        //Create notification and set attributes
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Timer notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notifManagerCompat = NotificationManagerCompat.from(this);

        //Creating new thread for running app in the background
        Thread t1 = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (!exit){
                            try {
                                builder.setContentText("Current date " + App_data.getCurrentTime() + ". Next notification in " + App_data.getInterval() + " " + App_data.getUnit() + ".");
                                builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Current date " + App_data.getCurrentTime() + ". Next notification in " + App_data.getInterval() + " " + App_data.getUnit() + "."));
                                notification = builder.build();
                                notifManagerCompat.notify(125, notification);

                                new Handler(Looper.getMainLooper()).post(new Runnable () { //needs to be run in main thread
                                    @Override
                                    public void run () {
                                        MainActivity.showNextTime();
                                    }
                                });
                                Thread.sleep(App_data.getIntervalMilisec()); //delay specified by user
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        t1.setName("T1");
        t1.start();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Method which creates notificationChannel for notification(API 26+, Android 8.0+)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel = new NotificationChannel("1", "Channel_1", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notifManager = getSystemService(NotificationManager.class);
            notifManager.createNotificationChannel(notifChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Method which sets exit boolean value (true indicates that notification thread should stop)
     * @param exit
     */
    public static void setExit(boolean exit) {
        NotifierBackground.exit = exit;
    }


}
