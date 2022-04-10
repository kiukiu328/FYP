package com.fyp.evhelper.reminder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.evhelper.MainActivity;
import com.fyp.evhelper.R;

import java.util.ArrayList;
import java.util.Calendar;

public class EleVehMai extends AppCompatActivity {
    Spinner spr_evYear,spr_evService;
    Button btn_addEVService,btn_back,mainPageBtn;
    ListView list_servicesList;
    TextView txt_test;



    SQLiteDatabase db= null;
    String sql;
    Cursor cursor = null;
    ImageView swipe_cal, swipe_auto, swipe_mod, swipe_del;

    ArrayList evServiceName =new ArrayList();
    ArrayList evServiceYear =new ArrayList();
    ArrayList listNo =new ArrayList();
    ArrayList<evmList> ev_cycleList = new ArrayList<>();

    //search->
    //read data from db             --> a,a
    //save the record               --> b,b
    //select service list(delete record, save to google calender
    //, renew the remind date , modify the remind date)               --> c,c
    //top bar                       --> d,d
    //chose the reminder electric vehicle maintenance cycle           --> e,e
    //google Calender event         --> f,f
    //auto renew the reminder date  --> g,g

    //The system show the detail of record if user save the data.
    //User select the service, then select the remind cycle and click the save button
    //The system save the data to database and display the record
    //User can delete the data , save to google calender , renew the remind date , modify the remind date
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();

        setContentView(R.layout.activity_ele_veh_mai);



        spr_evYear = findViewById(R.id.spr_evYear);
        spr_evService = findViewById(R.id.spr_evService);
        btn_addEVService = findViewById(R.id.btn_addEVService);
        btn_back = findViewById(R.id.btn_back);
        mainPageBtn = findViewById(R.id.mainPageBtn);
        Dialog dialog = new Dialog(this);

        final MediaPlayer correct_sound = MediaPlayer.create(this,R.raw.ding_sound);
        final MediaPlayer click_sound = MediaPlayer.create(this,R.raw.click_sound);

        list_servicesList = findViewById(R.id.list_servicesList);
        txt_test = findViewById(R.id.txt_test);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ElectricVehicleService,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spr_evService.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.ElectricVehicleYear,
                android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spr_evYear.setAdapter(adapter1);

        //read data from db a,a\\
        readData();
        //a,a end\\
        //save the record b,b\\
        btn_addEVService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean alreadyAdded = false;
                String evCycle = calenderCycle(spr_evYear.getSelectedItem().toString());
                String evService = spr_evService.getSelectedItem().toString();
                for(int i=0;i<evServiceName.size();i++){
                    if(evServiceName.get(i).equals(evService)){
                        alreadyAdded = true;
                    }
                }
                if (alreadyAdded){
                    Toast.makeText(EleVehMai.this,"You have already added ",Toast.LENGTH_SHORT).show();
                }
                else{

                    evServiceName.add(evService);
                    evServiceYear.add(evCycle);
                    ev_cycleList.add(new evmList(evCycle,evService));
                    evmListAdapter evmListAdapter = new evmListAdapter(EleVehMai.this,R.layout.ev_service_list,ev_cycleList);
                    list_servicesList.setAdapter(evmListAdapter);
                    correct_sound.start();
                try {
                    // save the data to db \\
                    db = SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper/LicenceDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
                    sql = "CREATE TABLE IF NOT EXISTS EVM(evmNo INTEGER PRIMARY KEY AUTOINCREMENT, evmName text, evmDate date);";
                    db.execSQL(sql);
                    String row = "INSERT INTO EVM(evmName, evmDate) Values ('" + evService + "','" + evCycle + "')";
                    db.execSQL(row);
                    db.close();
                    //e,end\\
                }catch (Exception e){
                    Toast.makeText(EleVehMai.this,"ERROR",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }}

            }
        });
        //b,b end\\
        //select service list c,c\\
        list_servicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                click_sound.start();
                int indexNo = position;
                String stringAutoRenewal = autoRenewalString(evServiceName.get(indexNo).toString());

                    dialog.setContentView(R.layout.evm_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.getWindow().setWindowAnimations(R.style.AnimationsForDialog);
                    Button evm_dia_cal = dialog.findViewById(R.id.evm_dia_cal);
                    Button evm_dia_auto = dialog.findViewById(R.id.evm_dia_auto);
                    evm_dia_auto.setText(stringAutoRenewal);
                    Button evm_dia_mod = dialog.findViewById(R.id.evm_dia_mod);
                    Button evm_dia_del = dialog.findViewById(R.id.evm_dia_del);
                    dialog.show();
                    evm_dia_cal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            click_sound.start();
                            String printList="";
                            for(int i=0;i<evServiceYear.size();i++){
                                if(evServiceYear.get(i).equals(evServiceYear.get(indexNo).toString())){
                                    listNo.add(i);
                                }
                            }
                            for(int j=0;j<listNo.size();j++){
                                int num = Integer.parseInt(listNo.get(j).toString());
                                printList += evServiceName.get(num).toString()+"\n";
                            }
                            addGoogleCalender(printList,evServiceYear.get(indexNo).toString());
                            listNo.clear();
                            dialog.cancel();
                        }
                    });
                    evm_dia_auto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            correct_sound.start();
                            evServiceYear.set(indexNo,autoRenewal(evServiceName.get(indexNo).toString(),evServiceYear.get(indexNo).toString()));
                            ev_cycleList.set(indexNo,new evmList(evServiceYear.get(indexNo).toString(),evServiceName.get(indexNo).toString()));
                            try {
                                db= SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper/LicenceDB", null ,SQLiteDatabase.OPEN_READWRITE);
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("evmName", evServiceName.get(indexNo).toString());
                                //contentValues.put("driLicNo", String.valueOf(edit_driLicNo));
                                contentValues.put("evmDate", evServiceYear.get(indexNo).toString());
                                String args[] = {evServiceName.get(indexNo).toString()};
                                db.update("EVM",contentValues,"evmName=?",args);
                                db.close();
                            }catch (Exception e){
                                Toast.makeText(EleVehMai.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            evmListAdapter evmListAdapter = new evmListAdapter(EleVehMai.this,R.layout.ev_service_list,ev_cycleList);
                            list_servicesList.setAdapter(evmListAdapter);
                            view.setSelected(true);
                            dialog.cancel();
                        }
                    });
                    evm_dia_mod.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            click_sound.start();
                            String[] result = evServiceYear.get(indexNo).toString().split("/");
                            int day = Integer.parseInt(result[0]);
                            int month = Integer.parseInt(result[1]);
                            int year = Integer.parseInt(result[2]);
                            DatePickerDialog datePickerDialog = new DatePickerDialog(EleVehMai.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int day) {
                                    month = month+1;
                                    String date = day+"/"+month+"/"+year;
                                    evServiceYear.set(indexNo,date);
                                    ev_cycleList.set(indexNo,new evmList(evServiceYear.get(indexNo).toString(),evServiceName.get(indexNo).toString()));
                                    try {
                                        db= SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper/LicenceDB", null ,SQLiteDatabase.OPEN_READWRITE);
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("evmName", evServiceName.get(indexNo).toString());
                                        //contentValues.put("driLicNo", String.valueOf(edit_driLicNo));
                                        contentValues.put("evmDate", evServiceYear.get(indexNo).toString());
                                        String args[] = {evServiceName.get(indexNo).toString()};
                                        db.update("EVM",contentValues,"evmName=?",args);
                                        db.close();
                                    }catch (Exception e){
                                        Toast.makeText(EleVehMai.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                    evmListAdapter evmListAdapter = new evmListAdapter(EleVehMai.this,R.layout.ev_service_list,ev_cycleList);
                                    list_servicesList.setAdapter(evmListAdapter);
                                    view.setSelected(true);

                                }
                            },year,month,day);
                            datePickerDialog.show();
                            dialog.cancel();
                        }
                    });
                    evm_dia_del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            click_sound.start();
                            try {
                                db= SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper/LicenceDB", null ,SQLiteDatabase.OPEN_READWRITE);
                                db.delete("EVM","evmName=?",new String[]{evServiceName.get(indexNo).toString()});
                                db.close();
                            }catch (Exception e){
                                Toast.makeText(EleVehMai.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            try {
                                evServiceName.remove(indexNo);
                                evServiceYear.remove(indexNo);
                                ev_cycleList.remove(indexNo);
                            }catch (Exception e){
                                Toast.makeText(EleVehMai.this,"ERROR",Toast.LENGTH_SHORT).show();
                            }
                            evmListAdapter evmListAdapter = new evmListAdapter(EleVehMai.this,R.layout.ev_service_list,ev_cycleList);
                            list_servicesList.setAdapter(evmListAdapter);
                            view.setSelected(true);
                            dialog.cancel();
                        }
                    });

            }
        });
        //c,c end\\
        //top bar d,d\\
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
    }
    //chose the reminder electric vehicle maintenance cycle e,e\\
    public String calenderCycle(String cycle){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int newYear = year;
        int newMonth = month+1;

        if(cycle.equals("Six months")){
            newMonth +=6;
            if (newMonth >12){
                newYear += 1;
                newMonth -= 12;
            }
        }else if (cycle.equals("One year")){
            newYear += 1;
        }else if (cycle.equals("Two-year")){
            newYear += 2;
        }
        else if (cycle.equals("Four-year")){
            newYear += 4;
        }
        else {
            newMonth = month;
            newYear = year;
        }
        return day+"/"+newMonth+"/"+newYear;
    }
    //e,e end\\
    //base on google calender will remind after the month.\\
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
    //e,end\\
    //google Calender event f,f\\
    public void addGoogleCalender(String serviceName,String serviceDate){
        long startMillis = 0;
        long endMillis = 0;
        String[] result = serviceDate.split("/");
        int year = Integer.parseInt(result[2]);
        int month = Integer.parseInt(result[1]);
        int day = Integer.parseInt(result[0]);

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(remindDateYear2 (year,month), remindDateMonth2 (month-1), day, 12, 01);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(remindDateYear2 (year,month), remindDateMonth2 (month-1), day, 23, 59);
        endMillis = endTime.getTimeInMillis() ;

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE,"Electric Vehicle Maintenance Remind");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                startMillis);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                endMillis);
        intent.putExtra(CalendarContract.Events.ALL_DAY,1);

        intent.putExtra(CalendarContract.Events.DESCRIPTION,"Electric Vehicle Maintenance on that day:\n"+serviceName);

        if (intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }else
            Toast.makeText(EleVehMai.this,"There is no app that can support this action",Toast.LENGTH_SHORT).show();
    }
    //f,f end\\
    //read data from db a,a\\
    @SuppressLint("Range")
    public void readData(){
        try {
            db= SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper/LicenceDB", null ,SQLiteDatabase.OPEN_READWRITE);
            cursor= db.rawQuery("SELECT * FROM EVM " , null);
            while (cursor.moveToNext()) {

                String evCycle = cursor.getString(cursor.getColumnIndex("evmDate"));
                String evService = cursor.getString(cursor.getColumnIndex("evmName"));
                //Toast.makeText(EleVehMai.this,evCycle+evService,Toast.LENGTH_SHORT).show();
                evServiceName.add(evService);
                evServiceYear.add(evCycle);
                ev_cycleList.add(new evmList(evCycle,evService));

            }
            db.close();
            evmListAdapter evmListAdapter = new evmListAdapter(EleVehMai.this,R.layout.ev_service_list,ev_cycleList);
            list_servicesList.setAdapter(evmListAdapter);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    //a,a end\\
    //auto renew the reminder date g,g\\
    public String autoRenewal(String service,String date){
        String[] result = date.split("/");
        int day = Integer.parseInt(result[0]);
        int month = Integer.parseInt(result[1]);
        int year = Integer.parseInt(result[2]);
        if(service.equals("Battery")||service.equals("Coolant")){
            year += 4;
            return newDay(day,month,year);
        }else if (service.equals("Restraint System(airbags)")){
            year += 4;
            return newDay(day,month,year);
        }else if (service.equals("Brake System")){
            year += 2;
            return newDay(day,month,year);
        }else if (service.equals("Others")||service.equals("Car Beauty")){
            month += 6;
            return newDay(day,month,year);
        }else{
            year += 1;
            return newDay(day,month,year);
        }
    }
    //g,g end\\
    //change date (for auto renew the reminder date)\\
    public String newDay(int day, int month, int year){
        int newYear = year;
        int newMonth = month;
        int newDay = day;
        if (newMonth >12){
            newYear += 1;
            newMonth -= 12;
        }
        switch (newMonth){
            case 4: case 6: case 9: case 11:
                if(newDay == 31){
                    newDay = 1;
                    newMonth +=1;
                }
                break;
            case 2:
                if(newDay >=28){
                    newDay = 1;
                    newMonth +=1;
                }
                break;
            default:
                newDay = newDay;
                break;
        }
        return newDay + "/" + newMonth + "/" + newYear;
    }
    //end\\
    //display the service auto renew date\\
    public String autoRenewalString(String service){
        String renew ="";
        if(service.equals("Battery")||service.equals("Coolant")){
            renew =" 4 years ";
        }else if (service.equals("Restraint System(airbags)")){
            renew =" 4 years ";
        }else if (service.equals("Brake System")){
            renew =" 2 years ";
        }else if (service.equals("Others")||service.equals("Car Beauty")){
            renew =" 6 months ";
        }else{
            renew =" 1 year ";
        }
        return "Auto Renewal\n( Renewal Date:"+renew+")";
    }
    //end\\
}