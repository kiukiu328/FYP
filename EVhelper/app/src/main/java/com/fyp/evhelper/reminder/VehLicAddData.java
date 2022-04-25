package com.fyp.evhelper.reminder;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fyp.evhelper.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;

import java.io.IOException;

public class VehLicAddData extends AppCompatActivity {
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    Button button_capture, button_saveVehLicData, button_back;
    TextView textview_data, textVehSample2;
    //TextView textVehSample1;
    Bitmap bitmap;
    String vehLicNo, vehLicDate, vehClass;
    ImageView imageVehSample,imageCapture;
    AlertDialog.Builder confirm;
    private static final int REQUEST_CAMERA_CODE = 100;
    Intent intent;
    DatabaseReference reference;
    SQLiteDatabase db= null;
    String sql;

    //19-4-2022
    LinearLayout veh_showDate;
    EditText edit_vehLicNo, edit_vehClass, edit_vehLicDate;
    //19-4-2022


    //search->
    //crop image                        --> a,a
    //save the data of vehicle licence include SQL and fire base     --> b,b
    //request code for permission       --> c,c
    //OCR                               --> d,d

    //The page is the user provide the picture (can use CAMERA, from image gallery)
    //The system use OCR the detect the key word
    //base on orc is stabilize, the system request user conform the data.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_veh_lic_add_data);
        //String dataNo = getIntent().getStringExtra("dataNo");
        textVehSample2 = findViewById(R.id.textVehSample2);
        //textVehSample1 = findViewById(R.id.textVehSample1);
        imageVehSample = findViewById(R.id.imageVehSample);
        imageCapture =findViewById(R.id.imageCapture);
        button_back = findViewById(R.id.button_back);
        button_capture = findViewById(R.id.button_capture);
        button_saveVehLicData = findViewById(R.id.button_saveVehLicData);

        textview_data = findViewById(R.id.text_data);
        confirm = new AlertDialog.Builder(this);
        //textVehSample1.setText(dataNo);

        final MediaPlayer correct_sound = MediaPlayer.create(this,R.raw.ding_sound);
        final MediaPlayer click_sound = MediaPlayer.create(this,R.raw.click_sound);

        //19-04-2022
        veh_showDate = (LinearLayout) findViewById(R.id.veh_showDate);
        edit_vehLicNo = findViewById(R.id.edit_vehLicNo);
        edit_vehClass = findViewById(R.id.edit_vehClass);
        edit_vehLicDate = findViewById(R.id.edit_vehLicDate);;
        edit_vehLicDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] result = edit_vehLicDate.getText().toString().split("/");
                final int year = Integer.parseInt(result[2]);
                final int month = Integer.parseInt(result[1]);
                final int day = Integer.parseInt(result[0]);
                DatePickerDialog datePickerDialog = new DatePickerDialog(VehLicAddData.this, new DatePickerDialog.OnDateSetListener() {
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
        //19-04-2022


        // camera permission \\
        if(ContextCompat.checkSelfPermission(VehLicAddData.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VehLicAddData.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }
        //end\\
        // firebase connect\\
        reference = FirebaseDatabase.getInstance().getReference("Data").child("VehLicData");
        //end\\

        //back to menu\\
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                finish();
            }
        });
        //end\\
        //crop image a,a\\
        button_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(VehLicAddData.this);
            }
        });
        //a,a end\\
        //save the data of vehicle licence include SQL and fire base b,b\\
        button_saveVehLicData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                //try {
                //confirm.setTitle("Confirm")
                //.setMessage("Make sure the Vehicle Licence Number is Correct!"+"\n")
                //.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                //@Override
                //public void onClick(DialogInterface dialog, int which) {

                // save the data to firebase \\
                if(edit_vehLicDate.getText().toString().length() !=10){
                    Toast.makeText(VehLicAddData.this, "The data is incorrect. Please correct the data or provide the vehicle license again", Toast.LENGTH_LONG).show();
                }else{
                    VehLicData vehLicData = new VehLicData(edit_vehLicNo.getText().toString()  , edit_vehLicDate.getText().toString() ,edit_vehClass.getText().toString());

                    reference.child(vehLicNo).setValue(vehLicData);
                    //e,end\\
                    // save the data to db \\
                    db= SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper/LicenceDB", null ,SQLiteDatabase.CREATE_IF_NECESSARY);
                    sql = "CREATE TABLE IF NOT EXISTS VehicleLicence(vehLicDataNo INTEGER PRIMARY KEY AUTOINCREMENT, vehLicNo text,vehClass text, vehLicDate date);";
                    db.execSQL(sql);
                    String row = "INSERT INTO VehicleLicence(vehLicNo,vehClass,vehLicDate) Values ('"+edit_vehLicNo.getText().toString()+"','"+edit_vehClass.getText().toString()+"','"+edit_vehLicDate.getText().toString()+"')";
                    db.execSQL(row);
                    db.close();
                    //e,end\\
                    //Toast.makeText(VehLicAddData.this, "Save Data Successfully", Toast.LENGTH_LONG).show();
                    correct_sound.start();
                    nextActivity();
                }
            }

            //})
            //.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            //@Override
            //public void onClick(DialogInterface dialog, int which) {
            //dialog.cancel();
            //}
            //}).show();
            //}catch (Exception e){
            //Toast.makeText(VehLicAddData.this,"Please Retake again!!\nThe data incorrect!!",Toast.LENGTH_SHORT).show();
            //}
            //}
        });
        //b,b end\\
    }
    // request code for permission c,c\\
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                try {
                    //base base on the the orc is stabilize
                    //processing the picture height Brightness and contrast
                    //remove the background(watermark) of licence
                    Filter filter = SampleFilters.getBlueMessFilter();
                    filter.addSubFilter(new BrightnessSubFilter(100));
                    filter.addSubFilter(new ContrastSubFilter(1.2f));
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);
                    imageCapture.setImageBitmap(bitmap);
                    Bitmap image = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                    Bitmap outputImage = filter.processFilter(image);
                    getTextFromImage(outputImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //c,c end\\
    //OCR d,d\\
    private void getTextFromImage(Bitmap bitmap){
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()){
            Toast.makeText(VehLicAddData.this,"Error Occurred!!!",Toast.LENGTH_SHORT).show();
        }
        else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            try {
                for(int i=0; i<textBlockSparseArray.size();i++){
                    TextBlock textBlock = textBlockSparseArray.valueAt(i);

                    // check the Vehicle Licence number, user name on which line and print it\\
                    // base on the the orc is stabilize \\

                    if(i==1){

                        vehLicDate = textBlock.getValue();
                        textBlock = textBlockSparseArray.valueAt(i+2);
                        vehClass = textBlock.getValue();

                        textBlock = textBlockSparseArray.valueAt(i+4);
                        vehLicNo = textBlock.getValue().replace(" S5", "").replace(" S", "").trim();

                        stringBuilder.append("Vehicle Licence No : \n"+ vehLicNo + "\n");
                        stringBuilder.append("\nVehicle Class : \n" + vehClass + "\n");
                        stringBuilder.append("\nValid to : \n"+ vehLicDate + "\n");
                    }
                }
                setData(vehLicNo, vehLicDate, vehClass);
                //19-4-2022
                imageCapture.setVisibility(View.VISIBLE);
                edit_vehLicNo.setText(vehLicNo);
                edit_vehClass.setText(vehClass);
                edit_vehLicDate.setText(vehLicDate);
                veh_showDate.setVisibility(View.VISIBLE);
                //19-4-2022

                //textview_data.setText(stringBuilder.toString());
                button_capture.setText("Retake");
                textVehSample2.setVisibility(View.GONE);
                //textVehSample2.setText("Make sure the data is correct");
                imageVehSample.setVisibility(View.GONE);

                button_saveVehLicData.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //d,d end\\
    //set data\\
    public void setData(String vehLicNo, String vehLicDate, String vehClass){
        this.vehLicNo = vehLicNo;
        this.vehLicDate = vehLicDate;
        this.vehClass = vehClass;
    }
    //end\\
    //next page\\
    public void nextActivity(){
        Intent intent = new Intent(this, VehLicConfirm.class);
        //intent.putExtra("dataNo",(dataNo));
        startActivity(intent);
    }
    //end\\
}
