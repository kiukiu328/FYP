package com.fyp.evhelper.stream;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.evhelper.R;

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
    ImageButton leave_btn;
    LinearLayout test_layout;
    EditText detectTime,previewValue;
    TextView detectTimeTV,PreviewPictureTV,control_tv;
    boolean show = true;

    String detectTimeValue="10";
    String previewTimeValue="5";
    public void showDetectTimePanel(){
        test_layout.setVisibility(View.VISIBLE);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        test_layout.startAnimation(aniFade);
        control_tv.setText("Waiting for input");
        show=false;
    }
    public void hideDetectTimePanel(){

        show=true;
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        control_tv.setText("Data Setting");
        test_layout.setVisibility(View.GONE);
        detectTime.setText("");
        previewValue.setText("");
        setStateText();
    }

    public void setStateText(){
        detectTimeTV.setText(detectTimeValue+" second");
        PreviewPictureTV.setText(previewTimeValue+" second");
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
        detectTimeTV=findViewById(R.id.detectTimeTV);
        PreviewPictureTV=findViewById(R.id.PreviewPictureTV);
        control_tv=findViewById(R.id.control_tv);
        leave_btn=findViewById(R.id.leave_btn);



        leave_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btn_show_close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(show == true){
                    showDetectTimePanel();
                }else{
                    hideDetectTimePanel();
                }
            }
        });


        changeTimeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                     String edit1 = detectTime.getText().toString();
                    String edit2 = previewValue.getText().toString();
                    //Toast.makeText(getApplicationContext(),value_time, Toast.LENGTH_SHORT).show();

                    if(edit1.equals("")){
                        edit1=detectTimeValue;
                    }else{
                        detectTimeValue = edit1;
                    }
                    if(edit2.equals("")){
                        edit2=previewTimeValue;
                    }else{
                        previewTimeValue = edit2;
                    }

                    int val = Integer.parseInt(edit1);
                    int val2 = Integer.parseInt(edit2);

                    if((val<10 || val > 40) || (val2<0 || val2>20)){
                        throw new Exception("Due to the security problem,please set a value between 10 and 40");
                    }


                    changeDetectTime(detectTimeValue,previewTimeValue);

                    hideDetectTimePanel();

                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        setStateText();
        new RetrieveParameters().execute();


    }

    public void changeDetectTime(String val1,String val2){
        new Thread(new Runnable(){
            @Override
            public void run() {
                Log.w("running","enter the thread");
                try {
                    String path = "http://192.168.0.184:8080/setParameters?detectTime=" + val1 +"&previewPictureTime="+val2;
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK){
                        InputStream inputStream = connection.getInputStream();
                        String result = is2String(inputStream);
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public String is2String(InputStream is){
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
        }catch (Exception e){
            return "";
        }

    }

    class RetrieveParameters extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... strings) {
            Log.w("running","enter the async");
            try {
                String path = "http://192.168.0.184:8080/getParameters";
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = connection.getInputStream();
                    String result = is2String(inputStream);
                    return result;
                }
            }catch(Exception e){
                e.printStackTrace();
            }


            return "";
        }

        protected void onPostExecute(String s) {
            Log.w("running","onPostExecute");
            Log.w("running",s);
            if(!s.equals("")) {
                try {
                    JSONObject obj = new JSONObject(s);
                    detectTimeValue = obj.getString("DetectTime");
                    previewTimeValue = obj.getString("PreviewPictureTime");
                    setStateText();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "JSON parse fail", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


}