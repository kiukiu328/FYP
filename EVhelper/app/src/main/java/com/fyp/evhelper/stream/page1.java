package com.fyp.evhelper.stream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.fyp.evhelper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;

import javax.net.SocketFactory;


public class page1 extends Fragment {
    Button btn_live;
    ImageView timing_picture;
    LinearLayout jump_btn;
    GetPicture getPicture = null;
    Handler handler, handler2;
    Intent page_configuration;
    Intent SecondPage;
    String ip_address = "";
    final int PORT = 9990;
    String pictureChangeTime = "5";
    String detectTime = "10";
    ProgressBar pb;
    boolean stopTimer = false;
    TextView timer_tv;
    SharedPreferences preferences;

    public void init_server_address() {
        SharedPreferences preferences = getActivity().getSharedPreferences("parameter", Context.MODE_PRIVATE);
        ip_address = preferences.getString("ip_address", "");

        Log.w("ip_address sssss", ip_address);

        //Make sure the address value is exists
        if (ip_address.equals("")) {
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


    public void onStart() {
        super.onStart();
//        Toast.makeText(getContext(),"page1 restart",Toast.LENGTH_SHORT).show();
        if (getPicture == null) {
            getPicture = new GetPicture();
            getPicture.start();
        }
        getParameters();
    }

    public void onResume() {
        super.onResume();
//        Toast.makeText(getContext(),"page1 resume",Toast.LENGTH_SHORT).show();
        if (getPicture == null) {
            getPicture = new GetPicture();
            getPicture.start();
        }
        getParameters();
    }

    public void onStop() {
        super.onStop();
//        Toast.makeText(getContext(),"page1 Activity Stop",Toast.LENGTH_LONG).show();
        if (getPicture != null) {
            getPicture.setStopFlag(true);
            getPicture = null;
        }
        stopTimer = true;
    }

    public void onPause() {
        super.onPause();
        if (getPicture != null) {
            getPicture.setStopFlag(true);
            getPicture = null;
        }
        stopTimer = true;

//        Toast.makeText(getContext(),"page1 Activity pause",Toast.LENGTH_SHORT).show();
    }

    public void init() {

//        getPicture= new GetPicture();
//        getPicture.start();

        SecondPage = new Intent(getActivity(), LiveStream.class);
        btn_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"show live click",Toast.LENGTH_SHORT).show();
                SecondPage.putExtra("ip_address", ip_address);
                startActivity(SecondPage);

            }
        });

        handler = new Handler() {
            public void handleMessage(Message msg) {
                timing_picture.setImageBitmap((Bitmap) msg.obj);
            }
        };

        handler2 = new Handler() {
            public void handleMessage(Message msg) {
                timer_tv.setText(String.valueOf((int) msg.obj));
                pb.setProgress((int) msg.obj);
            }
        };


    }

    public void getParameters() {
        preferences = getActivity().getSharedPreferences("parameter", Context.MODE_PRIVATE);
        pictureChangeTime = preferences.getString("phone_change_time", "5");
        detectTime = preferences.getString("detect_time", "10");

        //set the parameter of progress bar
        pb.setMax(Integer.parseInt(pictureChangeTime));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View page1 = inflater.inflate(R.layout.fragment_page1, container, false);

        timing_picture = page1.findViewById(R.id.timing_picture);
        btn_live = page1.findViewById(R.id.btn_live);
        jump_btn = page1.findViewById(R.id.open_another);
        timer_tv = page1.findViewById(R.id.timer_tv);
        pb = page1.findViewById(R.id.progress_bar);

        page_configuration = new Intent(getActivity(), SettingsActivity.class);
        jump_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(page_configuration, 1);
            }
        });


        init_server_address();

        init();

        Log.w("view", "page1 view create");
        return page1;


    }


    class GetPicture extends Thread {
        //        ImageView img_container;
        Socket socket;
        InputStream in;
        OutputStream out;
        DataInputStream buffer;
        private volatile boolean stopFlag = false;

        public void run() {
            while (!stopFlag) {
                if (ip_address != "") {
                    try {
                        socket = SocketFactory.getDefault().createSocket(ip_address, PORT);


                        in = socket.getInputStream();
                        out = socket.getOutputStream();

                        String frame = "";
                        out.write("picture".getBytes());

                        buffer = new DataInputStream(new BufferedInputStream(in));

                        frame = buffer.readLine();
                        Log.d("frame", frame);

                        if (frame == null) {
                            continue;
                        }

                        String[] data = frame.split(",");
                        if (Integer.valueOf(data[0]) == data[1].length()) {
//                        Log.d("picture_byte_length", "True");
                            byte[] decodedBytes = Base64.getDecoder().decode(data[1]);
                            show_img(decodedBytes);
                            int chengeTime = Integer.parseInt(pictureChangeTime);
                            Log.w("change column", "Photo changed");
                            stopTimer = false;
                            showTime();
                            Thread.sleep(chengeTime * 1000);
                        } else {
//                        Log.d("picture_byte_length", "False");
                        }
                        socket.close();
                        in.close();
                        out.close();
                        buffer.close();

                    } catch (Exception e) {
                        Log.d("page1", "error");
                        e.printStackTrace();
                    }
                }
            }
            Log.w("page 1 message", "page1 threading end");
        }

        public void setStopFlag(boolean stopFlag) {
            this.stopFlag = stopFlag;
        }

        public void show_img(byte[] bytesFrame) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytesFrame, 0, bytesFrame.length);
                Message message = new Message();
                message.obj = bitmap;
                handler.sendMessage(message);
                Log.d("handler", "handler sendMessage");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void showTime() {
        new Thread(new Runnable() {
            Message msg;

            @Override
            public void run() {
                for (int i = 1; i <= Integer.parseInt(pictureChangeTime); i++) {
                    try {
                        if (stopTimer) {
                            break;
                        }
                        msg = new Message();
                        msg.obj = i;
                        handler2.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}