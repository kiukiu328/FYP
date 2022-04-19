package com.fyp.evhelper.stream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fyp.evhelper.R;

public class page2 extends Fragment {

    ListView listView;
    List<AlertRecord> setData = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    LinearLayout dataReminder,loading;
    View context;
    ImageView loading_icon;
    RotateAnimation rotate;
    String ip_address="";

    public void playVideo(String path){
        Intent intent = new Intent(getActivity(), AlertRecordVideo.class);
        // 開始跳頁
        intent.putExtra("videoPatn",path);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(getContext(),"page2 create",Toast.LENGTH_SHORT).show();
    }

    public void init_address(){
        SharedPreferences preferences=getActivity().getSharedPreferences("parameter", Context.MODE_PRIVATE);
        ip_address=preferences.getString("ip_address","");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init_address();
        context = inflater.inflate(R.layout.fragment_page2, container, false);

        listView = context.findViewById(R.id.record_list_view);
        refreshLayout = context.findViewById(R.id.refresh_page);
        dataReminder = context.findViewById(R.id.dataReminder);
        loading= context.findViewById(R.id.loading);
        loading_icon=context.findViewById(R.id.loading_icon);


        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);

//        loading_icon.startAnimation(rotate);

        loading.getBackground().setAlpha(200);

//        loading= context.findViewById(R.id.loading);
        getAlertReocrd();
        AlertRecordAdapter alertAdapter = new AlertRecordAdapter(getActivity(), R.layout.list_item, setData);
        listView.setAdapter(alertAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(setData.get(position).getVideoState()==true) {
                    playVideo(setData.get(position).getVideoPath());
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                               @Override
                                               public void onRefresh() {
                                                   loading.setVisibility(View.VISIBLE);
                                                   loading_icon.startAnimation(rotate);
                                                   new RetrieveAlertRecord().execute("http://"+ip_address+":8080/AlertRecordJSON");
//                   Toast.makeText(getContext(),"refresh",Toast.LENGTH_SHORT).show();
                                                   refreshLayout.setRefreshing(false);
                                               }
                                           }

        );

        Log.w("view","page2 view create");
        // Inflate the layout for this fragment
        return context;
    }

    public void getAlertReocrd(){
        loading.setVisibility(View.VISIBLE);
        loading_icon.startAnimation(rotate);
        new RetrieveAlertRecord().execute("http://"+ip_address+":8080/AlertRecordJSON");
    }

    public void displayItem(){
        AlertRecordAdapter alertAdapter = new AlertRecordAdapter(getActivity(), R.layout.list_item, setData);
        listView.setAdapter(alertAdapter);
        Log.w("setData size",String.valueOf(setData.size()));
        Toast.makeText(getContext(), "Load complete", Toast.LENGTH_SHORT).show();
        alertAdapter.notifyDataSetChanged();
    }

    class RetrieveAlertRecord extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line="";
                while((line=reader.readLine())!=null){
                    buffer.append(line+"\n");
                }
                Log.w("JSON-STR",buffer.toString());
                return buffer.toString();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


//            Toast.makeText(getContext(), "Load complete doIN background", Toast.LENGTH_SHORT).show();
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            loading.setVisibility(View.GONE);

            try {
                if(s.length()!=0){
                    JSONObject obj = JSONObject.parseObject(s);
                    JSONArray record_index = obj.getJSONArray("record_index");
                    JSONArray record_date = obj.getJSONArray("record_date");
                    JSONArray video_path = obj.getJSONArray("video_path");
                    JSONArray icon_path = obj.getJSONArray("icon_path");
                    JSONArray video_state = obj.getJSONArray("video_state");
                    setData = new ArrayList<>();
                    for (int i = 0; i < record_index.size(); i++) {
                        String imagePath = "http://"+ip_address+":8080/image/"+icon_path.get(i);
                        String videoPath = "http://"+ip_address+":8080/video/"+icon_path.get(i);
                        setData.add(new AlertRecord(record_date.get(i).toString(), imagePath,videoPath,(boolean)video_state.get(i)));
                    }
                    if(setData.size()==0){
                        dataReminder.setVisibility(View.VISIBLE);
                    }else{
                        dataReminder.setVisibility(View.GONE);
                    }

                }else{
                    dataReminder.setVisibility(View.VISIBLE);
                }

                displayItem();

            }catch(Exception e){
                Toast.makeText(getContext(), "LoadFail", Toast.LENGTH_SHORT).show();

            }
        }

    }


    public void onStart(){
        super.onStart();
//        Toast.makeText(getContext(),"page2 restart",Toast.LENGTH_SHORT).show();
    }

    public void onStop(){
        super.onStop();
//        Toast.makeText(getContext(),"page2 Activity Stop",Toast.LENGTH_SHORT).show();
    }

    public void onPause(){
        super.onPause();
//        Toast.makeText(getContext(),"page2 Activity pause",Toast.LENGTH_SHORT).show();
    }

}