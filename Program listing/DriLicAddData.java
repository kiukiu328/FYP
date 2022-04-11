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
    ImageView imageDriSample;
    AlertDialog.Builder confirm;
    private static final int REQUEST_CAMERA_CODE = 100;
    Intent intent;
    DatabaseReference reference;
    SQLiteDatabase db= null;
    String sql;
    long result = 0;

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
        setContentView(R.layout.activity_dri_lic_add_data);

        textDriSample2 = findViewById(R.id.textDriSample2);
        imageDriSample = findViewById(R.id.imageDriSample);
        button_back = findViewById(R.id.button_back);
        button_capture = findViewById(R.id.button_capture);
        button_saveDriLicData = findViewById(R.id.button_saveDriLicData);
        textview_data = findViewById(R.id.text_data);
        confirm = new AlertDialog.Builder(this);

        final MediaPlayer correct_sound = MediaPlayer.create(this,R.raw.ding_sound);
        final MediaPlayer click_sound = MediaPlayer.create(this,R.raw.click_sound);

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
                try {
                    confirm.setTitle("Confirm")
                            .setMessage("Make sure the Driving Licence Number is Correct!"+"\n")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // save the data to firebase \\
                                    DriLicData driLicData = new DriLicData(driLicNo, driName, driLicDate);
                                    reference.child(driLicNo).setValue(driLicData);
                                    //e,end\\

                                    // save the data to db \\
                                    db= SQLiteDatabase.openDatabase("/data/data/com.example.v12_1/LicenceDB", null ,SQLiteDatabase.CREATE_IF_NECESSARY);
                                    sql = "CREATE TABLE IF NOT EXISTS DrivingLicence(driLicDataNo INTEGER PRIMARY KEY AUTOINCREMENT, driLicNo text,driName text, driLicDate date);";
                                    db.execSQL(sql);
                                    String row = "INSERT INTO DrivingLicence(driLicNo,driName,driLicDate) Values ('"+driLicNo+"','"+driName+"','"+driLicDate+"')";
                                    db.execSQL(row);
                                    db.close();
                                    //e,end\\
                                    Toast.makeText(DriLicAddData.this, "Save Data Successfully", Toast.LENGTH_LONG).show();
                                    correct_sound.start();
                                    nextActivity();
                                }
                            }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
                }catch (Exception e){
                    Toast.makeText(DriLicAddData.this,"Please Retake again!!\nThe data incorrect!!",Toast.LENGTH_SHORT).show();
                }
            }
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
                    if(i>=3 && i<=7){
                        if(textBlock.getValue().replace("Valid to","")
                        .replaceAll("[A-Z]","").replace(" ","").trim().length() == 10){
                            driLicDate = textBlock.getValue();
                            stringBuilder.append(textBlock.getValue()+"\n");
                        }
                    }
                }
                setData(driLicNo,driName,driLicDate);
                textview_data.setText(stringBuilder.toString());
                button_capture.setText("Retake");
                textDriSample2.setText("If the data is incorrect, you can be provided later or retake the information.");
                imageDriSample.setVisibility(View.GONE);
                button_back.setVisibility(View.GONE);
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