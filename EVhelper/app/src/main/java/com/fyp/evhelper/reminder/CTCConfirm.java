package com.fyp.evhelper.reminder;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fyp.evhelper.MainActivity;
import com.fyp.evhelper.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CTCConfirm extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Button mainPageBtn,btn_back,btn_googleCalender,btn_modifyData,btn_createCTC;
    TextView txt_centreName,txt_centreTel,txt_ctcLocation,txt_workingHours;
    EditText edit_ctcDate,edit_ctcTime;
    LinearLayout ctcShow,ctcShow2;
    Spinner spr_centreArea,spr_centreDistrict,spr_centreName;
    String name,location,tel,workingHours,date;
    SQLiteDatabase db= null;
    String sql;
    Cursor cursor = null;

    //search->
    //get current date      --> a,a
    //read data from db     --> b,b
    //save the record       --> c,c
    //top bar               --> d,d
    //delete record         --> e,e
    //call function google search, phone call , google map      --> f,f
    //change the date for Google Calender       --> g,g
    //google Calender event                     --> h,h
    //select spinner item will show another spinner data base on front spinner item         --> i,i
    //get data from json file                  --> j,j

    //The system show the detail of record if user save the data.
    //User select the area, then select the district, last select the centre
    //The system show the detail of centre
    //User can google search the centre name, phone contact the centre, search the centre location by google map
    //User click save button
    //The system save the data to database
    //User can delete the data or save to google calender

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_ctc_confirm);
        mainPageBtn = findViewById(R.id.mainPageBtn);
        btn_back = findViewById(R.id.btn_back);
        btn_createCTC = findViewById(R.id.btn_createCTC);
        btn_googleCalender = findViewById(R.id.btn_googleCalender);
        btn_modifyData = findViewById(R.id.btn_modifyData);
        txt_centreName = findViewById(R.id.txt_centreName);
        txt_centreTel = findViewById(R.id.txt_centreTel);
        txt_ctcLocation = findViewById(R.id.txt_ctcLocation);
        txt_workingHours = findViewById(R.id.txt_workingHours);
        edit_ctcDate = findViewById(R.id.edit_ctcDate);
        edit_ctcTime = findViewById(R.id.edit_ctcTime);
        ctcShow = findViewById(R.id.ctcShow);
        ctcShow2 = findViewById(R.id.ctcShow2);
        spr_centreArea = findViewById(R.id.spr_centreArea);
        spr_centreDistrict = findViewById(R.id.spr_centreDistrict);
        spr_centreName = findViewById(R.id.spr_centreName);

        final MediaPlayer correct_sound = MediaPlayer.create(this,R.raw.ding_sound);
        final MediaPlayer click_sound = MediaPlayer.create(this,R.raw.click_sound);

        // get current date a,a\\
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month  = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        edit_ctcDate.setText(day+"/"+(month + 1) +"/"+year);
        // a,a end\\

        //read data from db b,b\\
        readData();
        //b,b end\\

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.CentreArea,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spr_centreArea.setAdapter(adapter);
        spr_centreArea.setOnItemSelectedListener(this);

        //save the record c,c\\
        btn_createCTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ctcDate = edit_ctcDate.getText().toString();
                String ctcTime = edit_ctcTime.getText().toString();
                ctcShow2.setVisibility(View.GONE);
                btn_createCTC.setVisibility(View.GONE);
                ctcShow.setVisibility(View.VISIBLE);
                txt_centreName.setText(name);
                txt_centreTel.setText(tel);
                txt_ctcLocation.setText(location);
                txt_workingHours.setText(workingHours);
                btn_googleCalender.setVisibility(View.VISIBLE);
                btn_modifyData.setVisibility(View.VISIBLE);
                edit_ctcDate.setEnabled(false);
                edit_ctcTime.setEnabled(false);
                correct_sound.start();
                try {
                    // save the data to db \\
                    db = SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper/LicenceDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
                    sql = "CREATE TABLE IF NOT EXISTS DCTC(ctcNo INTEGER PRIMARY KEY AUTOINCREMENT, ctcDate date, ctcTime text, ctcName text, ctcLocation text, ctcTel text,ctcWorkingHours text);";
                    db.execSQL(sql);
                    String row = "INSERT INTO DCTC(ctcDate,ctcTime,ctcName,ctcLocation,ctcTel,ctcWorkingHours) Values ('" + ctcDate + "','" + ctcTime + "','" + name + "','" + location + "','" + tel + "','" + workingHours + "')";
                    db.execSQL(row);
                    db.close();
                    //e,end\\
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        //c,c end\\

        // top bar (mainPgnBtn->go to home page),(btn_back -> go to reminder main page) d,d\\
//        mainPageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                click_sound.start();
//                finish();
//                MainActivity.Companion.homePage();
//            }
//        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                finish();
            }
        });
        //d,d end\\

        //delete record e,e\\
        btn_modifyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ctcShow.setVisibility(View.GONE);
                edit_ctcDate.setEnabled(true);
                edit_ctcTime.setEnabled(true);
                btn_googleCalender.setVisibility(View.GONE);
                btn_modifyData.setVisibility(View.GONE);
                btn_createCTC.setVisibility(View.VISIBLE);
                ctcShow2.setVisibility(View.VISIBLE);
                db= SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper/LicenceDB", null ,SQLiteDatabase.CREATE_IF_NECESSARY);
                sql = "DROP TABLE IF EXISTS DCTC;";
                db.execSQL(sql);
                click_sound.start();
            }
        });
        //e,e end\\

        //call function google search, phone call , google map f,f\\
        txt_centreName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeGoogleSearch();
            }
        });
        txt_centreTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
        txt_ctcLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeGoogleMap();
            }
        });
        //f,f end\\

        //change the date for Google Calender g,g\\
        edit_ctcDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CTCConfirm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        edit_ctcDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
        edit_ctcTime.setOnClickListener(new View.OnClickListener() {
            int hourOfDay = 9;
            int minute =00;
            boolean is24HourView = true;

            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CTCConfirm.this,android.R.style.Theme_Holo_Light_DarkActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int intHourOfDay, int intMinute) {
                        DecimalFormat df = new DecimalFormat("00");
                        String stringHourOfDay = df.format(intHourOfDay);
                        String stringMinute = df.format(intMinute);
                        edit_ctcTime.setText(stringHourOfDay + ":" + stringMinute);
                    }
                },hourOfDay,minute,is24HourView);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });
        //g,g end\\

        //Google Calender event h,h\\
        btn_googleCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                String[] result = edit_ctcTime.getText().toString().split(":");
                int hour = Integer.parseInt(result[0]);
                int mins = Integer.parseInt(result[1]);
                long startMillis = 0;
                long endMillis = 0;
                String centreName = txt_centreName.getText().toString();
                String centreTel = txt_centreTel.getText().toString();
                String centreLocation = txt_ctcLocation.getText().toString();
                String centreWorkingHours = txt_workingHours.getText().toString();

                Calendar beginTime = Calendar.getInstance();
                beginTime.set(remindDateYear2 (year,month+1), remindDateMonth2 (month+1), day, (hour-2), mins);
                startMillis = beginTime.getTimeInMillis();
                Calendar endTime = Calendar.getInstance();
                endTime.set(remindDateYear2 (year,month+1), remindDateMonth2 (month+1), day, hour, mins);
                endMillis = endTime.getTimeInMillis() ;

                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE,"Car Testing Remind");
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION,centreLocation);
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            startMillis);
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            endMillis);
                intent.putExtra(CalendarContract.Events.ALL_DAY,1);

                intent.putExtra(CalendarContract.Events.DESCRIPTION,"Car Testing Remind\n\nCentre Name: "+centreName+"\n\nCentre Tel: "+centreTel+"\n\nWorkingHours: \n"+centreWorkingHours);

                if (intent.resolveActivity(getPackageManager())!=null){
                   startActivity(intent);
                }else
                   Toast.makeText(CTCConfirm.this,"There is no app that can support this action",Toast.LENGTH_SHORT).show();
                }
        });
        //h,h end\\
    }
    //call function google search, phone call , google map f,f\\
    private void makePhoneCall(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(CTCConfirm.this,"Please Grant Permission",Toast.LENGTH_SHORT).show();
        }else{
            Intent CentreTel = new Intent(Intent.ACTION_CALL);
            String getCentreTel = txt_centreTel.getText().toString();
            CentreTel.setData(Uri.parse("tel:"+getCentreTel));
            startActivity(CentreTel);
        }
    }
    private void makeGoogleMap(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(CTCConfirm.this,"Please Grant Permission",Toast.LENGTH_SHORT).show();
        }else{
            String getCentreLocation = txt_ctcLocation.getText().toString();
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+getCentreLocation);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }
    private void makeGoogleSearch(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(CTCConfirm.this,"Please Grant Permission",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            String getCentreName = txt_centreName.getText().toString();
            intent.putExtra(SearchManager.QUERY, getCentreName);
            startActivity(intent);
        }
    }
    //f,f end\\

    //base on google calender will remind after the month\\
    public int remindDateMonth2 (int mth){
        if(mth ==1){
            return 12;
        }else return mth-1;
    }
    public int remindDateYear2 (int yr,int mth){
        if(mth ==1){
            return yr-1;
        }else
            return yr;
    }
    //end\\

    // select spinner item will show another spinner data base on front spinner item. i,i\\
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String area =parent.getItemAtPosition(position).toString();
        ArrayAdapter<CharSequence> adapter1;
        String centreDistrict,centreArea;
        int centreName;
        ArrayAdapter<String> adapter2;
        if(parent.getId() == R.id.spr_centreArea)
        {
            if (area.equals("HongKongIsland")){
                adapter1 = ArrayAdapter.createFromResource(this, R.array.HongKongIslandDistrict,
                        android.R.layout.simple_spinner_item);

            }else if(area.equals("Kowloon")){
                adapter1 = ArrayAdapter.createFromResource(this, R.array.KowloonDistrict,
                        android.R.layout.simple_spinner_item);

            }
            else{
                adapter1 = ArrayAdapter.createFromResource(this, R.array.NewTerritoriesDistrict,
                        android.R.layout.simple_spinner_item);

            }
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spr_centreDistrict.setAdapter(adapter1);
            spr_centreDistrict.setOnItemSelectedListener(this);

        }
        if(parent.getId() == R.id.spr_centreDistrict){
            centreArea = spr_centreArea.getSelectedItem().toString();
            centreDistrict = spr_centreDistrict.getSelectedItem().toString();
            ArrayList<String> items = getArea("DCTC.json", centreArea ,centreDistrict);
            adapter2 = new ArrayAdapter<>(this,R.layout.spinner_ctc,R.id.spr_ctcLocation,items);
            spr_centreName.setAdapter(adapter2);
            spr_centreName.setOnItemSelectedListener(this);
        }
        if(parent.getId() == R.id.spr_centreName){
            centreArea = spr_centreArea.getSelectedItem().toString();
            centreDistrict = spr_centreDistrict.getSelectedItem().toString();
            centreName = spr_centreName.getSelectedItemPosition();
            ArrayList<String> items = getArea("DCTC.json", centreArea ,centreDistrict,centreName);
        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    //i,i end\\

    // get data from json file j,j\\
    public ArrayList<String> getArea(String fileName, String centreArea, String centreDistrict){
        ArrayList<String> cList=new ArrayList<String>();
        try{
            InputStream is=getResources().getAssets().open(fileName);
            int size =is.available();
            byte[] data=new byte[size];
            is.read(data);
            is.close();
            String json=new String(data,"UTF-8");
            JSONObject jObject = new JSONObject(json);
            JSONObject area = jObject.getJSONObject(centreArea);
            JSONObject district = area.getJSONObject("District");
            JSONArray areaLocation = district.getJSONArray(centreDistrict);
            for(int i=0;i<areaLocation.length();i++){
                JSONObject object = areaLocation.getJSONObject(i);
                String name = object.getString("name");
                cList.add(name);
            }
        }catch (Exception e){e.printStackTrace();}
        return cList;
    }
    public ArrayList<String> getArea(String fileName, String centreArea, String centreDistrict,int centreName){
        ArrayList<String> cList=new ArrayList<String>();
        try{
            InputStream is=getResources().getAssets().open(fileName);
            int size =is.available();
            byte[] data=new byte[size];
            is.read(data);
            is.close();
            String json=new String(data,"UTF-8");
            JSONObject jObject = new JSONObject(json);
            JSONObject area = jObject.getJSONObject(centreArea);
            JSONObject district = area.getJSONObject("District");
            JSONArray areaLocation = district.getJSONArray(centreDistrict);
                JSONObject object = areaLocation.getJSONObject(centreName);
                String name = object.getString("name");
                String location = object.getString("location");
                String tel = object.getString("tel");
                String workingHours = object.getString("workingHours");
                cList.add("Centre Location: \n"+location+"\n\nCentre Tel: "+tel+"\n\nWorking Hours: \n"+workingHours);
                this.name = name;
                this.location = location;
                this.tel =tel;
                this.workingHours = workingHours;
                txt_centreName.setText(name);
                txt_ctcLocation.setText(location);
                txt_centreTel.setText(tel);
                txt_workingHours.setText(workingHours);
        }catch (Exception e){e.printStackTrace();}
        return cList;
    }
    //j,j end\\
    //read data from db b,b\\
    @SuppressLint("Range")
    public void readData(){
        try {
            db= SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper/LicenceDB", null ,SQLiteDatabase.OPEN_READWRITE);
            cursor= db.rawQuery("SELECT * FROM DCTC WHERE ctcNo = 1" , null);
            edit_ctcDate.setEnabled(false);
            edit_ctcTime.setEnabled(false);
            ctcShow2.setVisibility(View.GONE);
            btn_createCTC.setVisibility(View.GONE);
            ctcShow.setVisibility(View.VISIBLE);
            btn_googleCalender.setVisibility(View.VISIBLE);
            btn_modifyData.setVisibility(View.VISIBLE);
            while (cursor.moveToNext()) {

            txt_centreName.setText(cursor.getString(cursor.getColumnIndex("ctcName")));
            txt_centreTel.setText(cursor.getString(cursor.getColumnIndex("ctcTel")));
            txt_ctcLocation.setText(cursor.getString(cursor.getColumnIndex("ctcLocation")));
            txt_workingHours.setText(cursor.getString(cursor.getColumnIndex("ctcWorkingHours")));
            edit_ctcDate.setText(cursor.getString(cursor.getColumnIndex("ctcDate")));
            edit_ctcTime.setText(cursor.getString(cursor.getColumnIndex("ctcTime")));
            }
            db.close();
        }
        catch (Exception e){
        }
    }
    //b,b end\\
}
