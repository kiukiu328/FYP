package com.fyp.evhelper.reminder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.evhelper.MainActivity;
import com.fyp.evhelper.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class DriLicConfirm extends AppCompatActivity {
    EditText edit_driName,edit_driLicNo,edit_driLicDate;
    Button btn_googleCalender,btn_modifyData,btn_back,mainPageBtn;
    String driLicNo, driName, driLicDate;
    DatePickerDialog.OnDateSetListener setListener;

    DatabaseReference reference;
    SQLiteDatabase db;
    String sql;
    Cursor cursor = null;

    //search->
    //read data from db     --> a,a
    //date Picker Dialog    --> b,b
    //top bar               --> c,c
    //Google Calender event --> d,d
    //Modify data           --> e,e
    //update data from db   --> f,f

    //The system show the detail of record if user save the data.
    //User can modify the data or save to google calender

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dri_lic_confirm);
        edit_driName = findViewById(R.id.edit_driName);
        edit_driLicNo = findViewById(R.id.edit_driLicNo);
        edit_driLicDate = findViewById(R.id.edit_driLicDate);
        btn_googleCalender = findViewById(R.id.btn_googleCalender);
        btn_modifyData = findViewById(R.id.btn_modifyData);
        btn_back = findViewById(R.id.btn_back);
        mainPageBtn = findViewById(R.id.mainPageBtn);

        final MediaPlayer correct_sound = MediaPlayer.create(this,R.raw.ding_sound);
        final MediaPlayer click_sound = MediaPlayer.create(this,R.raw.click_sound);

        //read data from db a,a\\
        readData();
        //a,a end\\

        String[] result = this.driLicDate.split("/");
        final int year = Integer.parseInt(result[2]);
        final int month = Integer.parseInt(result[1]);
        final int day = Integer.parseInt(result[0]);

        //date Picker Dialog b,b\\
        edit_driLicDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(DriLicConfirm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        edit_driLicDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
        //b,b end\\

        //firebase connect\\
        reference = FirebaseDatabase.getInstance().getReference("Data").child("DriLicData");
        //end\\

        //top bar c,c\\
        mainPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                MainActivity.Companion.homePage();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                Intent intent = new Intent(DriLicConfirm.this, ReminderMainPage.class);
                startActivity(intent);
            }
        });
        //c,c end\\

        //Google Calender event d,d\\
        btn_googleCalender.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                click_sound.start();
                if (!edit_driName.getText().toString().isEmpty() && !edit_driLicDate.getText().toString().isEmpty()) {
                    long startMillis = 0;
                    long endMillis = 0;

                    Calendar beginTime = Calendar.getInstance();
                    beginTime.set(remindDateYear (year,month), remindDateMonth (month), 1, 12, 01);
                    startMillis = beginTime.getTimeInMillis();
                    Calendar endTime = Calendar.getInstance();
                    endTime.set(remindDateYear2 (year,month), remindDateMonth2 (month), day, 23, 59);
                    endMillis = endTime.getTimeInMillis() ;

                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    intent.putExtra(CalendarContract.Events.TITLE,"Driving Licence Remind");

                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            startMillis);
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            endMillis);
                    intent.putExtra(CalendarContract.Events.ALL_DAY,1);

                    intent.putExtra(CalendarContract.Events.DESCRIPTION,"Driving Licence Valid Date");
                    if (intent.resolveActivity(getPackageManager())!=null){
                        startActivity(intent);
                    }else
                        Toast.makeText(DriLicConfirm.this,"There is no app that can support this action",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DriLicConfirm.this,"Data can not empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //d,d end\\

        //Modify data e,e\\
        btn_modifyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correct_sound.start();
                if (!edit_driName.getText().toString().isEmpty() && !edit_driLicDate.getText().toString().isEmpty()){
                    try {
                        upData(edit_driName,edit_driLicNo,edit_driLicDate);
                        reference.child(edit_driLicNo.getText().toString()).child("driName").setValue(edit_driName.getText().toString());
                        reference.child(edit_driLicNo.getText().toString()).child("driLicDate").setValue(edit_driLicDate.getText().toString());
                        Toast.makeText(DriLicConfirm.this,"Modify Successful ",Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(DriLicConfirm.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(DriLicConfirm.this,"Data can not empty",Toast.LENGTH_SHORT).show();
                }

            }
        });
        //e,e end\\
    }
    //read data from db a,a\\
    @SuppressLint("Range")
    public void readData(){
        try {
            db= SQLiteDatabase.openDatabase("/data/data/com.example.v12_1/LicenceDB", null ,SQLiteDatabase.OPEN_READWRITE);
            cursor= db.rawQuery("SELECT * FROM DrivingLicence WHERE driLicDataNo = 1" , null);
            while (cursor.moveToNext()) {
                this.driLicNo = cursor.getString(cursor.getColumnIndex("driLicNo"));
                this.driName = cursor.getString(cursor.getColumnIndex("driName"));
                this.driLicDate = cursor.getString(cursor.getColumnIndex("driLicDate"));
            }
                edit_driName.setText(this.driName);
                edit_driLicNo.setText(this.driLicNo);
                edit_driLicDate.setText(this.driLicDate);
            db.close();
        }catch (Exception e){
            Toast.makeText(DriLicConfirm.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
    //a,a end\\
    //update data from db f,f\\
    @SuppressLint("Range")
    public void upData(EditText edit_driName, EditText edit_driLicNo, EditText edit_driLicDate){
        try {
            db= SQLiteDatabase.openDatabase("/data/data/com.example.v12_1/LicenceDB", null ,SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("driName", edit_driName.getText().toString());
            //contentValues.put("driLicNo", String.valueOf(edit_driLicNo));
            contentValues.put("driLicDate", edit_driLicDate.getText().toString());
            String args[] = {edit_driLicNo.getText().toString()};
            db.update("DrivingLicence",contentValues,"driLicNo=?",args);
            db.close();
        }catch (Exception e){
            Toast.makeText(DriLicConfirm.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    //f,f end\\

    //base on google calender will remind after the month.\\
    public int remindDateMonth (int mth){
        if(mth ==1){
            return 11;
        }else if(mth==2){
            return 12;
        }else return mth-2;
    }
    public int remindDateMonth2 (int mth){
        if(mth ==1){
            return 12;
        }else return mth-1;
    }
    public int remindDateYear (int yr,int mth){
        if(mth ==1 || mth==2){
            return yr-1;
        }else
            return yr;
    }
    public int remindDateYear2 (int yr,int mth){
        if(mth ==1){
            return yr-1;
        }else
            return yr;
    }
    //end\\
}