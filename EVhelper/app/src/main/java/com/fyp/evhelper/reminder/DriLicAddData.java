package com.fyp.evhelper.reminder;


import android.Manifest;
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
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.IOException;


public class DriLicAddData extends AppCompatActivity {
    Button button_capture, button_saveDriLicData, button_back;
    TextView textview_data, textDriSample2;
    Bitmap bitmap;
    String driLicNo, driName, driLicDate, dataNo;
    ImageView imageDriSample,imageCapture;
    AlertDialog.Builder confirm;
    private static final int REQUEST_CAMERA_CODE = 100;
    Intent intent;
    DatabaseReference reference;
    SQLiteDatabase db= null;
    String sql;
    long result = 0;

    //19-4-2022
    LinearLayout dri_showDate;
    EditText edit_driName,edit_driLicNo,edit_driLicDate;
    //19-4-2022

    //search->
    //crop image                        --> a,a
    //save the data of driving licence include SQL and fire base     --> b,b
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
        setContentView(R.layout.activity_dri_lic_add_data);

        textDriSample2 = findViewById(R.id.textDriSample2);
        imageDriSample = findViewById(R.id.imageDriSample);
        imageCapture =findViewById(R.id.imageCapture);
        button_back = findViewById(R.id.button_back);
        button_capture = findViewById(R.id.button_capture);
        button_saveDriLicData = findViewById(R.id.button_saveDriLicData);
        textview_data = findViewById(R.id.text_data);
        confirm = new AlertDialog.Builder(this);

        final MediaPlayer correct_sound = MediaPlayer.create(this,R.raw.ding_sound);
        final MediaPlayer click_sound = MediaPlayer.create(this,R.raw.click_sound);

        //19-04-2022
        dri_showDate = (LinearLayout) findViewById(R.id.dri_showDate);
        edit_driLicNo = findViewById(R.id.edit_driLicNo);
        edit_driName = findViewById(R.id.edit_driName);
        edit_driLicDate = findViewById(R.id.edit_driLicDate);;
        edit_driLicDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] result = edit_driLicDate.getText().toString().split("/");
                final int year = Integer.parseInt(result[2]);
                final int month = Integer.parseInt(result[1]);
                final int day = Integer.parseInt(result[0]);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DriLicAddData.this, new DatePickerDialog.OnDateSetListener() {
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
        //19-04-2022

        // camera permission \\
        if(ContextCompat.checkSelfPermission(DriLicAddData.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriLicAddData.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }
        //end\\

        // firebase connect\\
        reference = FirebaseDatabase.getInstance().getReference("Data").child("DriLicData");
        //end\\

        //crop image a,a\\
        button_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(DriLicAddData.this);
            }
        });
        //a,a end\\
        //back to menu\\
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                finish();
            }
        });
        //end\\
        //save the data of driving licence include SQL and fire base b,b\\
        button_saveDriLicData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound.start();
                //try {
                //confirm.setTitle("Confirm")
                //.setMessage("Make sure the Driving Licence Number is Correct!"+"\n")
                //.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                //@Override
                //public void onClick(DialogInterface dialog, int which) {
                // save the data to firebase \\
                if(edit_driLicDate.getText().toString().length() !=10){
                    Toast.makeText(DriLicAddData.this, "The data is incorrect. Please correct the data or provide the vehicle license again", Toast.LENGTH_LONG).show();
                }else{
                    DriLicData driLicData = new DriLicData(edit_driLicNo.getText().toString(),edit_driName.getText().toString(),edit_driLicDate.getText().toString());
                    reference.child(driLicNo).setValue(driLicData);
                    //e,end\\

                    // save the data to db \\
                    db= SQLiteDatabase.openDatabase("/data/data/com.fyp.evhelper.reminder/LicenceDB", null ,SQLiteDatabase.CREATE_IF_NECESSARY);
                    sql = "CREATE TABLE IF NOT EXISTS DrivingLicence(driLicDataNo INTEGER PRIMARY KEY AUTOINCREMENT, driLicNo text,driName text, driLicDate date);";
                    db.execSQL(sql);
                    String row = "INSERT INTO DrivingLicence(driLicNo,driName,driLicDate) Values ('"+edit_driLicNo.getText().toString()+"','"+edit_driName.getText().toString()+"','"+edit_driLicDate.getText().toString()+"')";
                    db.execSQL(row);
                    db.close();
                    //e,end\\
                    Toast.makeText(DriLicAddData.this, "Save Data Successfully", Toast.LENGTH_LONG).show();
                    correct_sound.start();
                    nextActivity();
                }
            }
            //).setNegativeButton("Back", new DialogInterface.OnClickListener() {
            //@Override
            //public void onClick(DialogInterface dialog, int which) {
            //dialog.cancel();
            //}
            //}).show();
            //}catch (Exception e){
            //Toast.makeText(DriLicAddData.this,"Please Retake again!!\nThe data incorrect!!",Toast.LENGTH_SHORT).show();
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
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);
                    imageCapture.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 600, 400, false));
                    Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 600, 400, false);
                    getTextFromImage(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //c,c end\\
    //OCR d,d\\
    private void getTextFromImage(Bitmap bitmap){
        driLicDate="";
        String validDate;
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()){
            Toast.makeText(DriLicAddData.this,"Error Occurred!!!",Toast.LENGTH_SHORT).show();
        }
        else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            try {
                for(int i=0; i<textBlockSparseArray.size();i++){
                    TextBlock textBlock = textBlockSparseArray.valueAt(i);

                    // check the Driving Licence number, driver name on which line and print it\\
                    // base on the the orc is stabilize \\

                    if(i==1){
                        if (textBlock.getValue().equals("DRIVING LICENCE") || textBlock.getValue().length()>=15) {
                            textBlock = textBlockSparseArray.valueAt(i+1);
                            stringBuilder.append("Driving Licence No : \n"+ textBlock.getValue().replace(" ", "").trim() + "\n");
                            driLicNo = textBlock.getValue().replace(" ", "").trim();
                            textBlock = textBlockSparseArray.valueAt(i+2);
                        }else{

                            stringBuilder.append("Driving Licence No : \n"+ textBlock.getValue().replace(" ", "").trim() + "\n");
                            driLicNo = textBlock.getValue().replace(" ", "").trim();
                            textBlock = textBlockSparseArray.valueAt(i+1);
                            if (textBlock.getValue().equals("DRIVING LICENCE") || textBlock.getValue().length() <=5) {
                                textBlock = textBlockSparseArray.valueAt(i + 2);
                            }
                            driName = textBlock.getValue().replace(",", "").trim();
                        }
                        stringBuilder.append("\nDriver Name : \n" + textBlock.getValue().replace(",", "").trim() + "\n");
                        stringBuilder.append("\nValid to    : \n");
                    }
                    // check the valid date and print it\\
                    if(i>=3){
                        validDate = textBlock.getValue().replaceAll("[a-z]","").replaceAll("[A-Z]","").replace("Valid to","").replace(" ","").trim();
                        if( validDate.length() == 10){
                            driLicDate = validDate;
                            //stringBuilder.append(textBlock.getValue()+"\n");
                            //stringBuilder.append(validDate+"\n");
                        }
                    }
                }
                setData(driLicNo,driName,driLicDate);
                //19-4-2022
                edit_driLicNo.setText(driLicNo);
                edit_driName.setText(driName);
                edit_driLicDate.setText(driLicDate);
                dri_showDate.setVisibility(View.VISIBLE);
                //imageCapture.requestLayout();
                imageCapture.setVisibility(View.VISIBLE);
                //19-4-2022

                //textview_data.setText(stringBuilder.toString());
                button_capture.setText("Retake");
                textDriSample2.setVisibility(View.GONE);
                //textDriSample2.setText("If the data is incorrect, you can be provided later or retake the information.");
                imageDriSample.setVisibility(View.GONE);
                //button_back.setVisibility(View.GONE);
                button_saveDriLicData.setVisibility(View.VISIBLE);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //d,d end\\

    //set data\\
    public void setData(String driLicNo, String driName, String driLicDate){
        this.driLicNo = driLicNo;
        this.driName = driName;
        this.driLicDate = driLicDate;
    }
    //end\\

    //next page\\
    public void nextActivity(){
        intent = new Intent(DriLicAddData.this, DriLicConfirm.class);
        startActivity(intent);
    }
    //next page\\
}
