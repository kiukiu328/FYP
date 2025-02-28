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
    ImageView imageVehSample;
    AlertDialog.Builder confirm;
    private static final int REQUEST_CAMERA_CODE = 100;
    Intent intent;
    DatabaseReference reference;
    SQLiteDatabase db= null;
    String sql;

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
        setContentView(R.layout.activity_veh_lic_add_data);
        //String dataNo = getIntent().getStringExtra("dataNo");
        textVehSample2 = findViewById(R.id.textVehSample2);
        //textVehSample1 = findViewById(R.id.textVehSample1);
        imageVehSample = findViewById(R.id.imageVehSample);
        button_back = findViewById(R.id.button_back);
        button_capture = findViewById(R.id.button_capture);
        button_saveVehLicData = findViewById(R.id.button_saveVehLicData);
        textview_data = findViewById(R.id.text_data);
        confirm = new AlertDialog.Builder(this);
        //textVehSample1.setText(dataNo);

        final MediaPlayer correct_sound = MediaPlayer.create(this,R.raw.ding_sound);
        final MediaPlayer click_sound = MediaPlayer.create(this,R.raw.click_sound);

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
                try {
                    confirm.setTitle("Confirm")
                            .setMessage("Make sure the Vehicle Licence Number is Correct!"+"\n")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // save the data to firebase \\
                                    VehLicData vehLicData = new VehLicData(vehLicNo  , vehLicDate ,vehClass);

                                    reference.child(vehLicNo).setValue(vehLicData);
                                    //e,end\\
                                    // save the data to db \\
                                    db= SQLiteDatabase.openDatabase("/data/data/com.example.v12_1/LicenceDB", null ,SQLiteDatabase.CREATE_IF_NECESSARY);
                                    sql = "CREATE TABLE IF NOT EXISTS VehicleLicence(vehLicDataNo INTEGER PRIMARY KEY AUTOINCREMENT, vehLicNo text,vehClass text, vehLicDate date);";
                                    db.execSQL(sql);
                                    String row = "INSERT INTO VehicleLicence(vehLicNo,vehClass,vehLicDate) Values ('"+vehLicNo+"','"+vehClass+"','"+vehLicDate+"')";
                                    db.execSQL(row);
                                    db.close();
                                    //e,end\\
                                    Toast.makeText(VehLicAddData.this, "Save Data Successfully", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(VehLicAddData.this,"Please Retake again!!\nThe data incorrect!!",Toast.LENGTH_SHORT).show();
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
                    //base base on the the orc is stabilize
                    //processing the picture height Brightness and contrast
                    //remove the background(watermark) of licence
                    Filter filter = SampleFilters.getBlueMessFilter();
                    filter.addSubFilter(new BrightnessSubFilter(100));
                    filter.addSubFilter(new ContrastSubFilter(1.2f));
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);
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
                textview_data.setText(stringBuilder.toString());
                button_capture.setText("Retake");
                textVehSample2.setText("If the data is incorrect, you can be provided later or retake the information.");
                imageVehSample.setVisibility(View.GONE);
                button_back.setVisibility(View.GONE);
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