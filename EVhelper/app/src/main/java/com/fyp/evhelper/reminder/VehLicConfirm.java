package com.fyp.evhelper.reminder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.evhelper.MainActivity;
import com.fyp.evhelper.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class VehLicConfirm extends AppCompatActivity {
    EditText edit_vehClass,edit_vehLicNo,edit_vehLicDate;
    Button btn_googleCalender,btn_modifyData,btn_back,mainPageBtn;
    String vehClass,vehLicNo,vehLicDate;
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
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_veh_lic_confirm);
        edit_vehClass = findViewById(R.id.edit_vehClass);
        edit_vehLicNo = findViewById(R.id.edit_vehLicNo);
        edit_vehLicDate = findViewById(R.id.edit_vehLicDate);

        btn_googleCalender = findViewById(R.id.btn_googleCalender);
        btn_modifyData = findViewById(R.id.btn_modifyData);
        btn_back = findViewById(R.id.btn_back);
        mainPageBtn = findViewById(R.id.mainPageBtn);

        //read data from db a,a\\
        readData();
        //a,a end\\

        final MediaPlayer correct_sound = MediaPlayer.create(this,R.raw.ding_sound);
        final MediaPlayer click_sound = MediaPlayer.create(this,R.raw.click_sound);

        String[] result = this.vehLicDate.split("/");
        final int year = Integer.parseInt(result[2]);
        final int month = Integer.parseInt(result[1]);
        final int day = Integer.parseInt(result[0]);

        //date Picker Dialog b,b\\
        edit_vehLicDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(VehLicConfirm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        edit_vehLicDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
        //b,b end\\
        // firebase connect\\
        reference = FirebaseDatabase.getInstance().getReference("Data").child("VehLicData");
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
                Intent intent = new Intent(VehLicConfirm.this, ReminderMainPage.class);
                startActivity(intent);
            }
        });
        //c,c end\\

        //Google Calender event d,d\\
        btn_googleCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                if (!edit_vehClass.getText().toString().isEmpty() && !edit_vehLicDate.getText().toString().isEmpty()) {
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
                    intent.putExtra(CalendarContract.Events.TITLE,"Vehicle Licence Remind");

                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            startMillis);
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            endMillis);
                    intent.putExtra(CalendarContract.Events.ALL_DAY,1);

                    intent.putExtra(CalendarContract.Events.DESCRIPTION,"Vehicle Licence Valid Date");

                    if (intent.resolveActivity(getPackageManager())!=null){
                        startActivity(intent);
                    }else
                        Toast.makeText(VehLicConfirm.this,"There is no app that can support this action",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(VehLicConfirm.this,"Data can not empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //d,d end\\
        //Modify data e,e\\
        btn_modifyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correct_sound.start();
                if (!edit_vehClass.getText().toString().isEmpty() && !edit_vehLicDate.getText().toString().isEmpty()){
                    try {
                        upData(edit_vehLicNo,edit_vehLicDate,edit_vehClass);
                        reference.child(edit_vehLicNo.getText().toString()).child("vehClass").setValue(edit_vehClass.getText().toString());
                        reference.child(edit_vehLicNo.getText().toString()).child("vehLicDate").setValue(edit_vehLicDate.getText().toString());
                        Toast.makeText(VehLicConfirm.this,"Modify Successful ",Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(VehLicConfirm.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(VehLicConfirm.this,"Data can not empty",Toast.LENGTH_SHORT).show();
                }

            }
        });
        //e,end\\
    }
    //read data from db\\
    //read data from db a,a\\
    @SuppressLint("Range")
    public void readData(){
        try {
            db= SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper.reminder/LicenceDB", null ,SQLiteDatabase.OPEN_READWRITE);
            cursor= db.rawQuery("SELECT * FROM VehicleLicence WHERE vehLicDataNo = 1" , null);
            while (cursor.moveToNext()) {
                this.vehLicNo = cursor.getString(cursor.getColumnIndex("vehLicNo"));
                this.vehLicDate = cursor.getString(cursor.getColumnIndex("vehLicDate"));
                this.vehClass = cursor.getString(cursor.getColumnIndex("vehClass"));

            }
            edit_vehClass.setText(this.vehClass);
            edit_vehLicNo.setText(this.vehLicNo);
            edit_vehLicDate.setText(this.vehLicDate);

            db.close();
        }catch (Exception e){
            Toast.makeText(VehLicConfirm.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    //a,a end\\
    //update data from db f,f\\
    @SuppressLint("Range")
    public void upData(EditText edit_vehLicNo,EditText edit_vehLicDate,EditText edit_vehClass){
        try {
            db= SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper.reminder/LicenceDB", null ,SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            contentValues.put("vehLicDate", edit_vehLicDate.getText().toString());
            contentValues.put("vehClass", edit_vehClass.getText().toString());
            String args[] = {edit_vehLicNo.getText().toString()};
            db.update("VehicleLicence",contentValues,"vehLicNo=?",args);

            db.close();
        }catch (Exception e){
            Toast.makeText(VehLicConfirm.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
    //e,end\\
}
