package com.fyp.evhelper.book;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.evhelper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class activity_book_timetable extends AppCompatActivity {

    //Create variables
    RadioGroup rg1, rg2, rg3;
    RadioButton rb;
    Button nextbtn, backbtn;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_timetable);

        //initializing variables
        rg1= (RadioGroup) findViewById(R.id.rg1);
        rg2= (RadioGroup) findViewById(R.id.rg2);
        rg3= (RadioGroup) findViewById(R.id.rg3);
        nextbtn = findViewById(R.id.btnNext);
        backbtn = findViewById(R.id.btnBack);
        textView2 = findViewById(R.id.textView2);

        //set on checked listener for radio group
        rg1.setOnCheckedChangeListener(listener1);
        rg2.setOnCheckedChangeListener(listener2);
        rg3.setOnCheckedChangeListener(listener3);

        //get date from previous activity
        //and show selected date to user
        Intent intent = getIntent();
        String date = intent.getStringExtra("message_key");
        String month = intent.getStringExtra("message_keyA");
        String day = intent.getStringExtra("message_keyB");
        textView2.setText(date);

        //submit the selected time
        //and start the next activity
        nextbtn.setOnClickListener(view -> {
            String time = getTime();

            Intent intent2 = new Intent(getApplicationContext(), Duration.class);

            intent2.putExtra("message_key2", time);
            intent2.putExtra("message_key3", date);
            intent2.putExtra("message_keyC", month);
            intent2.putExtra("message_keyD", day);

            startActivity(intent2);

        });

        //go back to previous activity
        backbtn.setOnClickListener(view -> {
            finish();
        });


        //connect to Firebase and get the data form it
        DatabaseReference hourRef = FirebaseDatabase.getInstance().getReference().child("CarPark01").child(month).child(day);

        hourRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //used to loop each half hour
                double count = 1;
                for (int r = 1; r <= 48; r++){

                    //format double to time format
                    //e.g. from "1.5" to "01.30" to "0130"
                     DecimalFormat df = new DecimalFormat("00.00");
                     String s = df.format(count);
                     String hr = s;

                     hr = hr.replace(".5", "3");
                     hr = hr.replace(".0", "0");

                     //get the vacancy value from Firebase
                     String vacancy = (String) snapshot.child(hr).child("vacancy").getValue();

                     Log.e("vacancy", String.valueOf(vacancy));

                     //get the full-booking hour
                     //if the vacancy number is 0
                     if (vacancy.equals("0")){
                         String hour = snapshot.child(hr).getKey();

                         //call method to disable the radio buttons
                         //which are is full-booking
                         //from different radio groups
                         checkVacancy1(hour);
                         checkVacancy2(hour);
                         checkVacancy3(hour);
                     }
                    Log.e("r", String.valueOf(count));
                    count += 0.5;

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }

    //clear the checked radiobutton
    //when other radiobutton is checked on other radio group
    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                rg2.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                rg3.setOnCheckedChangeListener(null);
                rg2.clearCheck(); // clear the second RadioGroup!
                rg3.clearCheck();
                rg2.setOnCheckedChangeListener(listener2); //reset the listener
                rg3.setOnCheckedChangeListener(listener3);
                Log.e("XXX2", "do the work");
            }
        }
    };

    //clear the checked radiobutton
    //when other radiobutton is checked on other radio group
    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                rg1.setOnCheckedChangeListener(null);
                rg3.setOnCheckedChangeListener(null);
                rg1.clearCheck();
                rg3.clearCheck();
                rg1.setOnCheckedChangeListener(listener1);
                rg3.setOnCheckedChangeListener(listener1);
                Log.e("XXX2", "do the work");
            }
        }
    };

    //clear the checked radiobutton
    //when other radiobutton is checked on other radio group
    private RadioGroup.OnCheckedChangeListener listener3 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                rg1.setOnCheckedChangeListener(null);
                rg2.setOnCheckedChangeListener(null);
                rg1.clearCheck();
                rg2.clearCheck();
                rg1.setOnCheckedChangeListener(listener1);
                rg2.setOnCheckedChangeListener(listener1);
                Log.e("XXX2", "do the work");
            }
        }
    };

    //get the selected time from radio button
    private String getTime(){
        int chkId1 = rg1.getCheckedRadioButtonId();
        int chkId2 = rg2.getCheckedRadioButtonId();
        int chkId3 = rg3.getCheckedRadioButtonId();
        int realCheck = 0;

        //check location of the checked radiobutton
        //from 3 radio group
        if (chkId1 != -1){
            realCheck = chkId1;
        } else if (chkId2 != -1) {
            realCheck = chkId2;
        } else {
            realCheck = chkId3;
        }


        //get the text from checked radiobutton
        rb = findViewById(realCheck);
        String txtTime = (String) rb.getText();

        Log.e("XXX2", txtTime);

        return txtTime;
    }

    //find the time which are full-booking
    //and disable that time for selection
    private void checkVacancy1(String hour){
        int count = rg1.getChildCount();

        //loop the number of radio button in radio group
        for (int i=0;i<count;i++) {
            View o = rg1.getChildAt(i);
            if (o instanceof RadioButton) {
                RadioButton availTime =(RadioButton)o;

                String time = String.valueOf(availTime.getText());

                time = time.replace(" ", "");
                time = time.replace(":","");

                Log.e("check", time);

                //check if the text from radio button
                //equal to the input variable(time that is full-booking
                if(time.equals(hour)) {
                    availTime.setEnabled(false);
                    availTime.setBackgroundResource(R.drawable.radio_disabled);
                }
            }
        }
    }

    //find the time which are full-booking
    //and disable that time for selection
    private void checkVacancy2(String hour){
        int count = rg2.getChildCount();

        //loop the number of radio button in radio group
        for (int i=0;i<count;i++) {
            View o = rg2.getChildAt(i);
            if (o instanceof RadioButton) {
                RadioButton availTime =(RadioButton)o;

                String time = String.valueOf(availTime.getText());

                time = time.replace(" ", "");
                time = time.replace(":","");

                Log.e("check", time);

                //check if the text from radio button
                //equal to the input variable(time that is full-booking
                if(time.equals(hour)) {
                    availTime.setEnabled(false);
                    availTime.setBackgroundResource(R.drawable.radio_disabled);
                }
            }
        }
    }

    //find the time which are full-booking
    //and disable that time for selection
    private void checkVacancy3(String hour){
        int count = rg3.getChildCount();

        //loop the number of radio button in radio group
        for (int i=0;i<count;i++) {
            View o = rg3.getChildAt(i);
            if (o instanceof RadioButton) {
                RadioButton availTime =(RadioButton)o;

                String time = String.valueOf(availTime.getText());

                time = time.replace(" ", "");
                time = time.replace(":","");

                Log.e("check", time);

                //check if the text from radio button
                //equal to the input variable(time that is full-booking
                if(time.equals(hour)) {
                    availTime.setEnabled(false);
                    availTime.setBackgroundResource(R.drawable.radio_disabled);
                }
            }
        }
    }
}