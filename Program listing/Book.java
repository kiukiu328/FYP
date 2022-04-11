package com.fyp.evhelper.book;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.fyp.evhelper.MainActivity;
import com.fyp.evhelper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Book extends AppCompatActivity {

    //Create variables
    Button button_book, button_Back;
    CalendarView calendarView;
    TextView title;

    //used to record full booked day
    List<Calendar> calendars = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //initializing variables
        button_book = findViewById(R.id.button_Book);
        button_Back = findViewById(R.id.button_Back);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        Intent myIntent = getIntent();
        title = findViewById(R.id.textView);
        title.setText(myIntent.getStringExtra("title"));
        //go to next activity
        button_book.setOnClickListener(view -> {
            //create the vacancy number of EV charger on selected day
            createCarParkDB();

            //set the the selected day to variable
            String date = getDate();

            //Create new intent for next activity
            Intent intent = new Intent(getApplicationContext(), activity_book_timetable.class);

            //pass the selected date to next activity
            intent.putExtra("message_key", date);
            intent.putExtra("message_keyA", getMonth());
            intent.putExtra("message_keyB", getDay());

            startActivity(intent);

        });

        //return to map page
        //after back button is clicked
        button_Back.setOnClickListener(view -> {
            finish();
        });

        //used to disable the date which is full-booking
        //only for demonstration
        /*
        Calendar firstDisabled = DateUtils.getCalendar();
        firstDisabled.add(Calendar.DAY_OF_MONTH, 2);

        Calendar secondDisabled = DateUtils.getCalendar();
        secondDisabled.add(Calendar.DAY_OF_MONTH, 1);

        Calendar thirdDisabled = DateUtils.getCalendar();
        thirdDisabled.add(Calendar.DAY_OF_MONTH, 18);


        calendars.add(firstDisabled);
        calendars.add(secondDisabled);
        calendars.add(thirdDisabled);
        */

        //Log.e("day of month", String.valueOf(calendars));

        //Log.e("day of month", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));


        //check the vacancy number of EV charger from database
        checkDayVacancy();

        //disable the dates which are full-booking
        //with an array list
        calendarView.setDisabledDays(calendars);

        //disable the previous and current day
        Calendar min = Calendar.getInstance();
        //min.add(Calendar.DATE, -1); //exclude current day
        calendarView.setMinimumDate(min);

    }

    //get the selected date from calendar
    //and format the selected date
    private String getDate(){
        String date = "";
        for(Calendar calendar : calendarView.getSelectedDates()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            date = sdf.format(calendar.getTime());
            //Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
        }
        return date;
    }

    //get the month from selected date
    //return the English of month
    private String getMonth(){
        String date = getDate();

        String dateParts[] = date.split("/");

        String month = dateParts[1];
        String outputMonth = "";

        switch (month){
            case "01":
                outputMonth = "January";
                break;
            case "02":
                outputMonth = "February";
                break;
            case "03":
                outputMonth = "March";
                break;
            case "04":
                outputMonth = "April";
                break;
            case "05":
                outputMonth = "May";
                break;
            case "06":
                outputMonth = "June";
                break;
            case "07":
                outputMonth = "July";
                break;
            case "08":
                outputMonth = "August";
                break;
            case "09":
                outputMonth = "September";
                break;
            case "10":
                outputMonth = "October";
                break;
            case "11":
                outputMonth = "November";
                break;
            case "12":
                outputMonth = "December";
                break;
        }

        return outputMonth;
    }

    //get the day from selected date
    //return the day
    private String getDay(){
        String date = getDate();

        String dateParts[] = date.split("/");

        String day = dateParts[2];
        return day;
    }

    //initiate the database of booking table
    /*
    private void createClientDB(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Booking").child("Client01").child("car_plate").setValue("");
        myRef.child("Booking").child("Client01").child("car_park").setValue("");
        myRef.child("Booking").child("Client01").child("book_date").setValue("");
        myRef.child("Booking").child("Client01").child("entrance_time").setValue("");
        myRef.child("Booking").child("Client01").child("exit_time").setValue("");
    }
     */

    //create the vacancy number of EV charger on selected day
    private void createCarParkDB(){
        //connect to Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();


        //used to loop each half hour
        double y = 1;
        for (int r = 1; r <= 48; r++){

            //format double to time format
            //e.g. from "1.5" to "01.30" to "0130"
            DecimalFormat df = new DecimalFormat("00.00");
            String s = df.format(y);
            String hr = s;

            hr = hr.replace(".5", "3");
            hr = hr.replace(".0", "0");

            //create vacancy number on database
            myRef.child("CarPark01").child(getMonth()).child(getDay()).child(hr).child("vacancy").setValue("4");

            Log.e("s", hr);

            y += 0.5;
        }

        //used to change the vacancy to empty
        //only for demonstration
        myRef.child("CarPark01").child(getMonth()).child(getDay()).child("0200").child("vacancy").setValue("0");

        myRef.child("CarPark01").child(getMonth()).child(getDay()).child("0300").child("vacancy").setValue("0");

        myRef.child("CarPark01").child(getMonth()).child(getDay()).child("1000").child("vacancy").setValue("0");

        myRef.child("CarPark01").child(getMonth()).child(getDay()).child("1800").child("vacancy").setValue("0");

    }

    //check the vacancy on each date
    //normally won't be trigger
    private void checkDayVacancy(){
        //connect to Firebase
        DatabaseReference hourRef = FirebaseDatabase.getInstance().getReference().child("CarPark01");
        hourRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String month, day;

                //get the month of date which is full booking
                for (DataSnapshot mSnapshot: snapshot.getChildren()) {
                    month = mSnapshot.getKey();
                    Log.e("month", String.valueOf(month));

                    //get the day of date which is full booking
                    for (DataSnapshot dSnapshot: snapshot.child(month).getChildren()) {
                        boolean fullBooking = true;
                        day = dSnapshot.getKey();
                        Log.e("day", String.valueOf(day));

                        //used to loop each half hour
                        double count = 1;
                        for (int r = 1; r <= 48; r++){

                            DecimalFormat df = new DecimalFormat("00.00");
                            String s = df.format(count);
                            String hr = s;

                            hr = hr.replace(".5", "3");
                            hr = hr.replace(".0", "0");

                            String vacancy = (String) dSnapshot.child(hr).child("vacancy").getValue();

                            //Change the boolean to false
                            //if the vacancy of EV charger of
                            //all time of that day is full booking
                            if (!vacancy.equals("0")){
                                fullBooking = false;
                            }

                            count += 0.5;

                        }
                        //calculate the different of the full-booking date and current date
                        //if the full-booking date is detected
                        if (fullBooking == true){
                            SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");

                            //get the current date
                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
                            String currentDate = df.format(c);


                            //convert the month of full-booking date to digits
                            String monthNum = convertMonth(month);

                            String fullBookedDay = day +" " + monthNum + " 2022";
                            Log.e("c", String.valueOf(c));

                            //calculate the different of two date
                            try {
                                Date date1 = myFormat.parse(currentDate);
                                Date date2 = myFormat.parse(fullBookedDay);
                                long diff = date2.getTime() - date1.getTime();
                                int diffNum = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                                Log.e("diffNum", String.valueOf(diffNum));

                                //call the method to disable full-booking date
                                disabledDay(diffNum);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        Log.e("fullBooking", String.valueOf(fullBooking));
                    }
                }



            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    //disabledDay from calendar
    private void disabledDay(int diffNum){
        Calendar fullday = DateUtils.getCalendar();
        fullday.add(Calendar.DAY_OF_MONTH, diffNum);

        //add the date which is full-booking to an array list
        calendars.add(fullday);

        //calendarView.setDisabledDays(calendars);
    }

    //convert english of month to digits
    private String convertMonth(String monthStr){
        String month = monthStr;
        String outputMonth = "";

        switch (month){
            case "January":
                outputMonth = "01";
                break;
            case "February":
                outputMonth = "02";
                break;
            case "March":
                outputMonth = "03";
                break;
            case "April":
                outputMonth = "04";
                break;
            case "May":
                outputMonth = "05";
                break;
            case "June":
                outputMonth = "06";
                break;
            case "July":
                outputMonth = "07";
                break;
            case "August":
                outputMonth = "08";
                break;
            case "September":
                outputMonth = "09";
                break;
            case "October":
                outputMonth = "10";
                break;
            case "November":
                outputMonth = "11";
                break;
            case "December":
                outputMonth = "12";
                break;
        }

        return outputMonth;
    }

}
