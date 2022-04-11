package com.fyp.evhelper.book;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.evhelper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Duration extends AppCompatActivity {

    //Create variables
    TextView tvE, tvL;
    NumberPicker numPicker;
    Button btnConfirm, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duration);

        //initializing variables
        tvE = findViewById(R.id.tvETime);
        tvL = findViewById(R.id.tvLTime);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack2);

        //get date from previous activity
        Intent intent = getIntent();
        String eTime = intent.getStringExtra("message_key2");
        eTime = eTime.replace(" ", "");
        String bookDate = intent.getStringExtra("message_key3");
        String month = intent.getStringExtra("message_keyC");
        String day = intent.getStringExtra("message_keyD");

        Log.e("XXX2", bookDate);

        //initializing the number picker
        initiateNumPicker(eTime);

        //show the selected time which is from previous activity
        tvE.setText(eTime);

        //call the method to initiate the text of exit time
        initiateLeaveTime();

        //check whether next selection of time is full or empty
        checkNextHourVacancy(eTime, month, day);

        //connect to Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        //save the booking details to database
        //after clicking the "confirm" button
        btnConfirm.setOnClickListener(view -> {
            String entranceTime = String.valueOf(tvE.getText());
            String leaveTime = String.valueOf(tvL.getText());
            myRef.child("Booking").child("Client01").child("02").child("car_plate").setValue("SR7122");
            myRef.child("Booking").child("Client01").child("02").child("car_park").setValue("Maritime Square");
            myRef.child("Booking").child("Client01").child("02").child("book_date").setValue(bookDate);
            myRef.child("Booking").child("Client01").child("02").child("entrance_time").setValue(entranceTime);
            myRef.child("Booking").child("Client01").child("02").child("exit_time").setValue(leaveTime);

            //Create a dialog to show booking successful message
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    Duration.this);
            // set title
            alertDialogBuilder.setTitle("Message");
            alertDialogBuilder.setCancelable(true);
            // set dialog message
            alertDialogBuilder
                    .setMessage("Booking Success")
                    .setCancelable(true)
                    .setPositiveButton( "OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            try {
                                Intent intent = new Intent(getApplicationContext(), BookHistory.class);
                                startActivity(intent);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            reduceVacancy(entranceTime, leaveTime, month, day);

        });

        //go back previous activity
        btnBack.setOnClickListener(view -> {

            finish();

        });



    }

    //initializing the number picker
    private void initiateNumPicker(String eTime){
        //create array to store the duration of booking hours
        String nums[] = {"0.5", "1","1.5","2","2.5","3","3.5","4","4.5","5","5.5","6"};
        numPicker = findViewById(R.id.numPicker);

        //set the max value of number picker
        if (eTime.equals("18:30"))
            numPicker.setMaxValue(nums.length-2);
        else if (eTime.equals("19:00"))
            numPicker.setMaxValue(nums.length-3);
        else if (eTime.equals("19:30"))
            numPicker.setMaxValue(nums.length-4);
        else if (eTime.equals("20:00"))
            numPicker.setMaxValue(nums.length-5);
        else if (eTime.equals("20:30"))
            numPicker.setMaxValue(nums.length-6);
        else if (eTime.equals("21:00"))
            numPicker.setMaxValue(nums.length-7);
        else if (eTime.equals("21:30"))
            numPicker.setMaxValue(nums.length-8);
        else if (eTime.equals("22:00"))
            numPicker.setMaxValue(nums.length-9);
        else if (eTime.equals("22:30"))
            numPicker.setMaxValue(nums.length-10);
        else if (eTime.equals("23:00"))
            numPicker.setMaxValue(nums.length-11);
        else if (eTime.equals("23:30"))
            numPicker.setMaxValue(nums.length-12);
        else
            numPicker.setMaxValue(nums.length-1);


        numPicker.setMinValue(0);        //set the min value of number picker
        numPicker.setWrapSelectorWheel(false);
        numPicker.setDisplayedValues(nums);
        numPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //when the number picker is changing
        //the text of exit time will be change as well
        numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String txtTime = (String) tvE.getText();
                Double enTime = ConvertToDouble(txtTime);

                Double selectedHour = Double.parseDouble(nums[newVal]);
                Double exTime = enTime + selectedHour;

                String outputTime = ConvertToString(exTime).replaceAll("..(?!$)", "$0:");

                tvL.setText(outputTime);
            }
        });

    }

    //initiating the text of exit time
    private void initiateLeaveTime(){

        String txtTime = (String) tvE.getText();
        Double enTime = ConvertToDouble(txtTime);

        Double selectedHour = 0.5;
        Double exTime = enTime + selectedHour;

        String outputTime = ConvertToString(exTime).replaceAll("..(?!$)", "$0:");

        tvL.setText(outputTime);
    }

    //check whether next selection of time is full or empty
    private void checkNextHourVacancy(String eTime, String month, String day){
        double convertedTime = ConvertToDouble(eTime);      //convert the time to double for calculation
        double nextHour = convertedTime + 0.5;
        final String[] newHour = {ConvertToString(nextHour)};

        //connect to Firebase
        DatabaseReference hourRef = FirebaseDatabase.getInstance().getReference().child("CarPark01").child(month).child(day);

        hourRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //get the vacancy number of selection time
                String newHourVacancy = (String) snapshot.child(newHour[0]).child("vacancy").getValue();

                //check if vacancy number is equal 0
                //if true, disable the number picker
                if(newHourVacancy.equals("0")){
                    numPicker.setEnabled(false);
                    newHour[0] = newHour[0].replaceAll("..(?!$)", "$0:");

                    tvL.setText(newHour[0]);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    //convert the time from string to double for calculation
    //e.g. from "01:30" to "01.30" to 1.5
    private double ConvertToDouble(String hour){
        hour = hour.replace(":", ".");

        hour = hour.replace(".3", ".5");

        double convertedTime = Double.parseDouble(hour);
        Log.e("convertedTime", String.valueOf(convertedTime));
        return convertedTime;
    }

    //convert the time from double to String
    //e.g. from 1.5 to "0130"
    private String ConvertToString(double hour){
        DecimalFormat df = new DecimalFormat("00.00");
        String s = df.format(hour);

        s = s.replace(".5", "3");
        s = s.replace(".0", "0");

        Log.e("nextHour", String.valueOf(s));
        return s;
    }

    //reduce the vacancy number from the database after the booking is confirm
    private void reduceVacancy(String entranceTime, String leaveTime, String month, String day){
        double eTime = ConvertToDouble(entranceTime);
        double lTime = ConvertToDouble(leaveTime);

        //Create an array list to shore the time which are included in the selected duration
        ArrayList<Double> listTime = new ArrayList<Double>();

        //add the time to array list
        for (double i = eTime; i<lTime; i = i + 0.5){
            listTime.add(i);
        }

        //connect to Firebase
        DatabaseReference hourRef = FirebaseDatabase.getInstance().getReference().child("CarPark01").child(month).child(day);

        hourRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //get the current vacancy number from the database
                for (int r = 0; r<listTime.size(); r++){

                    String time = ConvertToString(listTime.get(r));

                    String vacancy = (String) snapshot.child(time).child("vacancy").getValue();

                    int newVacancy = Integer.parseInt(vacancy) - 1;

                    //call method to reduce vacancy number from the database
                    updateDB(newVacancy , time, month, day);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //Log.e("listTime", String.valueOf(listTime));
    }

    //reduce vacancy number from the database
    private void updateDB(int vacancy, String hr, String month, String day){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("CarPark01").child(month).child(day).child(hr).child("vacancy").setValue(String.valueOf(vacancy));
    }

}