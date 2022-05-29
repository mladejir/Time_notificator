package com.example.time_notificator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

/**
 * @author Jiri Mladek
 * Class representing main activity of application
 */
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

    /**
     * Method, which is called when creating main activity
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //creating graphical components
        createGuiComponents();
    }

    /**
     * Method, which is called when canceling application
     */
    @Override
    public void onDestroy() {
        NotifierBackground.setExit(true);
        super.onDestroy();
    }

    /**
     * Method, which creates graphical components or gets reference to them
     */
    public void createGuiComponents() {
        intervalBox = (EditText)findViewById(R.id.inputNumber);
        nextNotification = (TextView) findViewById(R.id.nextNotification);
        pushButton = (Button) findViewById(R.id.pushButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        //Create a spinner for time unit
        spinner = findViewById(R.id.time_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
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

        //if input number is valid
        if(!withError){
            if(firstPushed){ //pushing for the first time
                startService(serviceIntent);
                firstPushed = false;
            }
            else{
                if(!cancelPushed){
                    NotifierBackground.setExit(true);
                    //interrupting the notification thread and stopping service
                    Thread toInterrupt = getThreadByName("T1");
                    toInterrupt.interrupt();
                    stopService(serviceIntent);
                }
                else{
                    cancelPushed = false;
                }
                //starting new service
                startService(serviceIntent);
            }
            //show Next notification in x seconds
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
        //interrupting the notification thread and stopping service
        Thread toInterrupt = getThreadByName("T1");
        toInterrupt.interrupt();
        stopService(serviceIntent);

        Snackbar cancel = Snackbar.make(findViewById(R.id.mainLayout), "Interval canceled", BaseTransientBottomBar.LENGTH_LONG);
        cancel.show();
    }

    /**
     * Method which sets interval of next notification, checks validity of input number
     */
    public void setInterval(){
        String intervalStr = (intervalBox.getText().toString());
        Integer inputNumber = 0;
        //try to parse input number
        try {
            inputNumber = Integer.parseInt(intervalStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        //if empty field or number out of range <1;10000>, then error occurs
        if(intervalStr.equals("") || inputNumber < 1 ||  inputNumber > 10000){
            Snackbar error = Snackbar.make(findViewById(R.id.mainLayout), "Input number can not be empty and has to be in range from 1 to 10000!", 5000);
            View errorView = error.getView();
            errorView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_red));
            error.show();
            withError = true;
        }
        else{
            App_data.setInterval(Integer.parseInt(intervalStr));
            Snackbar ok = Snackbar.make(findViewById(R.id.mainLayout), "Interval of notification set successfully.", BaseTransientBottomBar.LENGTH_LONG);
            View okView = ok.getView();
            okView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
            ok.show();
            withError = false;
        }
    }

    /**
     * Method which sets the time unit (seconds/minutes/hours)
     */
    public void setUnit(){
        App_data.setUnit(timeUnit);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        timeUnit = parent.getItemAtPosition(position).toString(); //get item at corresponding position
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Method which shows time of next notification
     */
    public static void showNextTime(){
        MainActivity.nextNotification.setVisibility(View.VISIBLE);
        MainActivity.nextNotification.setText("Next notification at: " + App_data.getNextTime());
    }

    /**
     * Method which gets the Thread by according name
     * @param name
     * @return thread, null if thread not found
     */
    public Thread getThreadByName(String name) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getName().equals(name)){
                return thread;
            }
        }
        return null;
    }
}