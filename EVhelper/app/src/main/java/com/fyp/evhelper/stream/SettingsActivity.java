package com.fyp.evhelper.stream;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.evhelper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xujiaji.happybubble.BubbleDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsActivity extends AppCompatActivity {
    LinearLayout btn_show_close;
    Button changeTimeBtn;
    ImageButton leave_btn,question_support1,question_support2;
    LinearLayout test_layout;
    NumberPicker detectTime, previewValue;
    TextView detectTimeTV, PreviewPictureTV, control_tv;
    boolean show = true;
    SharedPreferences preferences;
    String detectTimeValue = "10";
    String previewTimeValue = "5";
    String ip_address = "";

    public void init_server_address(){
        SharedPreferences preferences=this.getSharedPreferences("parameter", Context.MODE_PRIVATE);
        ip_address=preferences.getString("ip_address","");

        Log.w("ip_address sssss",ip_address);

        //Make sure the address value is exists
        if(ip_address.equals("")) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference().child("ip_address");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ip_address = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }


    public void showDetectTimePanel() {
        test_layout.setVisibility(View.VISIBLE);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        test_layout.startAnimation(aniFade);
        control_tv.setText("Waiting for input");
        show = false;
    }

    public void hideDetectTimePanel() {

        show = true;
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        control_tv.setText("Data Setting");
        test_layout.setVisibility(View.GONE);

        get_value_number_picker();

        setStateText();
    }

    public void init_number_picker(){
        detectTime.setMaxValue(40);
        detectTime.setMinValue(10);
        previewValue.setMaxValue(20);
        previewValue.setMinValue(5);
    }

    public void get_value_number_picker(){
        detectTime.setValue(Integer.parseInt(detectTimeValue));
        previewValue.setValue(Integer.parseInt(previewTimeValue));
    }

    public void setStateText() {
        preferences=this.getSharedPreferences("parameter", Context.MODE_PRIVATE);
        previewTimeValue = preferences.getString("phone_change_time","5");
        detectTimeValue=preferences.getString("detect_time","10");
        detectTimeTV.setText(detectTimeValue + " seconds");
        PreviewPictureTV.setText(previewTimeValue + " seconds");
    }

    public void saveParameter(){
        SharedPreferences preferences=getSharedPreferences("parameter",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("detect_time", detectTimeValue);
        editor.putString("phone_change_time", previewTimeValue);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        btn_show_close = findViewById(R.id.setDetectTime);
        detectTime = findViewById(R.id.detectTime);
        test_layout = findViewById(R.id.test_layout);
        changeTimeBtn = findViewById(R.id.change_time_btn);
        previewValue = findViewById(R.id.previewVal);
        detectTimeTV = findViewById(R.id.detectTimeTV);
        PreviewPictureTV = findViewById(R.id.PreviewPictureTV);
        control_tv = findViewById(R.id.control_tv);
        leave_btn = findViewById(R.id.leave_btn);
        question_support1 = findViewById(R.id.question_support1);
        question_support2= findViewById(R.id.question_support2);

        init_server_address();
        init_number_picker();

        //show bubble
        question_support1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBubbleDialog("The system will send a notification when the " +
                        "detect time equals "+detectTimeValue,question_support1);
            }
        });
        question_support2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBubbleDialog("The preview picture changes every "+previewTimeValue+" seconds",question_support2);
            }
        });
        //end show bubble


        leave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDetectTimePanel();
                finish();
            }
        });




        btn_show_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show == true) {
                    showDetectTimePanel();
                } else {
                    hideDetectTimePanel();
                }
            }
        });


        changeTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String edit1 = Integer.toString(detectTime.getValue());
                    String edit2 = Integer.toString(previewValue.getValue());
                    //Toast.makeText(getApplicationContext(),value_time, Toast.LENGTH_SHORT).show();

//                    if (edit1.equals("")) {
//                        edit1 = detectTimeValue;
//                    } else {
                    detectTimeValue = edit1;
//                    }
//                    if (edit2.equals("")) {
//                        edit2 = previewTimeValue;
//                    } else {
                    previewTimeValue = edit2;
//                    }

//                    int val = Integer.parseInt(edit1);
//                    int val2 = Integer.parseInt(edit2);
//                    if ((val < 10 || val > 40) || (val2 < 0 || val2 > 20)) {
//                        throw new Exception("Due to the security problem,please set a value between 10 and 40");
//                    }
                    ////
                    saveParameter();
                    ////

                    changeDetectTime(detectTimeValue, previewTimeValue);

                    hideDetectTimePanel();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        setStateText();
        get_value_number_picker();

    }

    public String is2String(InputStream is) {
        try {

            BufferedReader bufferedReader = new BufferedReader(new
                    InputStreamReader(is, "utf-8"));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            String response = "";

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            response = stringBuilder.toString().trim();
            return response;
        } catch (Exception e) {
            return "";
        }

    }

    public void changeDetectTime(String val1, String val2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.w("running", "enter the thread");
                try {
                    String path = "http://"+ip_address+":8080/setParameters?detectTime=" + val1 + "&previewPictureTime=" + val2;
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        String result = is2String(inputStream);
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void showBubbleDialog(String msg,View clickView){
        View content = LayoutInflater.from(this).inflate(R.layout.message_support_setting,null);
        TextView text = content.findViewById(R.id.message_support_text);
        text.setText(msg);

        new BubbleDialog(this)
                .setBubbleContentView(content)
                .setClickedView(clickView)
                .setTransParentBackground()
                .setPosition(BubbleDialog.Position.BOTTOM)
                .show();
    }

}