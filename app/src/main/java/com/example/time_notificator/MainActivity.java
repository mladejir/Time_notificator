package com.example.time_notificator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Spinner spinner;
    private String timeUnit;
    private EditText intervalBox;
    private static TextView nextNotification;
    private Button pushButton;
    private Button cancelButton;
    private Intent serviceIntent;
    private boolean withError = false;
    private boolean firstPushed = true;
    private boolean cancelPushed = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating graphical components
        createGuiComponents();
    }

    @Override
    public void onDestroy() {
        NotifierBackground.setExit(true);
        super.onDestroy();
    }

    public void createGuiComponents() {

        intervalBox = (EditText)findViewById(R.id.inputNumber);
        nextNotification = (TextView) findViewById(R.id.nextNotification);
        pushButton = (Button) findViewById(R.id.pushButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        /*Create a spinner for time unit */
        spinner = findViewById(R.id.time_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);




    }

    public void setInterval(){

        String intervalStr = (intervalBox.getText().toString());
        if(intervalStr.equals("") || Integer.parseInt(intervalStr) < 1 ||  Integer.parseInt(intervalStr) > 10000){
            Snackbar error = Snackbar.make(findViewById(R.id.mainLayout), "Input number can not be empty and has to be in range from 1 to 10000!", 5000);
            View errorView = error.getView();
            errorView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_red));
            error.show();
            withError = true;
        }
        else{
            App_data.setInterval(Integer.parseInt(intervalStr));
            Snackbar ok = Snackbar.make(findViewById(R.id.mainLayout), "Interval of notification set successfully.", 5000);
            View okView = ok.getView();
            okView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
            ok.show();
            withError = false;
        }
    }


    public void setUnit(){
        App_data.setUnit(timeUnit);
    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        timeUnit = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }





    /**
     * Method which is called after clicking the Set Interval button
     * @param view
     */
    public void push(View view){
        //get info - interval of next notification
        setInterval();
        setUnit();
        serviceIntent = new Intent(this, NotifierBackground.class);

        if(!withError){
            if(firstPushed){
                startService(serviceIntent);
                firstPushed = false;
            }
            else{
                if(!cancelPushed){
                    NotifierBackground.setExit(true);
                    Thread toInterrupt = getThreadByName("T1");
                    toInterrupt.interrupt();
                    //stopping service
                    stopService(serviceIntent);
                }
                else{
                    cancelPushed = false;
                }
                //starting new service
                startService(serviceIntent);
            }
            //show the timer Next notification in x seconds
            nextNotification.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);

        }

    }

    /**
     * Method which is called after clicking the cancel button
     * @param view
     */
    public void pushCancel(View view){
        cancelPushed = true;
        cancelButton.setVisibility(View.INVISIBLE);
        nextNotification.setVisibility(View.INVISIBLE);

        NotifierBackground.setExit(true);
        Thread toInterrupt = getThreadByName("T1");
        toInterrupt.interrupt();
        //stopping service
        stopService(serviceIntent);
    }


    /**
     * Method which gets the Thread by according name
     * @param name
     * @return
     */
    public Thread getThreadByName(String name) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getName().equals(name)) return thread;
        }
        return null;
    }


    public static void showNextTime(){
        MainActivity.nextNotification.setVisibility(View.VISIBLE);
        MainActivity.nextNotification.setText("Next notification at: " + App_data.getNextTime());
    }

}