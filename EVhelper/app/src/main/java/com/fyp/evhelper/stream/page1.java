package com.fyp.evhelper.stream;

import android.content.Intent;
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
    GetPicture getPicture=null;
    Handler handler;
    Intent page_configuration;
    Intent SecondPage;
    String ip_address ="";
    final int PORT = 9990;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        init_server_address();
//        init();
//        Toast.makeText(getContext(),"page1 create",Toast.LENGTH_SHORT).show();
    }

    public void init_server_address(){
        if(ip_address.equals("")) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference ref = database.getReference().child("ip_address");
            ref.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ip_address = dataSnapshot.getValue().toString();
                    System.out.println("set ip_address:"+ip_address);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

    }

    public void onStart(){
        super.onStart();
//        Toast.makeText(getContext(),"page1 restart",Toast.LENGTH_SHORT).show();
        if(getPicture!=null) {
            getPicture= new GetPicture();
            getPicture.start();
        }
    }

    public void onStop(){
        super.onStop();
//        Toast.makeText(getContext(),"page1 Activity Stop",Toast.LENGTH_SHORT).show();
        if(getPicture!=null) {
            getPicture.setStopFlag(true);
        }
    }

    public void onPause(){
        super.onPause();
        if(getPicture!=null) {
            getPicture.setStopFlag(true);
        }
//        Toast.makeText(getContext(),"page1 Activity pause",Toast.LENGTH_SHORT).show();
    }

    public void init(){

        getPicture= new GetPicture();
        getPicture.start();

        SecondPage = new Intent(getActivity(), LiveStream.class);
        btn_live.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"show live click",Toast.LENGTH_SHORT).show();
                SecondPage.putExtra("ip_address",ip_address);
                startActivity(SecondPage);
            }
        });

        handler = new Handler(){
            public void handleMessage(Message msg){
                timing_picture.setImageBitmap((Bitmap) msg.obj);
            }
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View page1 = inflater.inflate(R.layout.fragment_page1, container, false);

        timing_picture=page1.findViewById(R.id.timing_picture);
        btn_live=page1.findViewById(R.id.btn_live);
        jump_btn = page1.findViewById(R.id.open_another);
        page_configuration = new Intent(getActivity(), SettingsActivity.class);
        jump_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(page_configuration);
            }
        });



        init_server_address();
        init();

        Log.w("view","page1 view create");
        return page1;
    }




    class GetPicture extends Thread {
//        ImageView img_container;
        Socket socket;
        InputStream in;
        OutputStream out;
        DataInputStream buffer;
        private volatile boolean stopFlag = false;
//        public GetPicture(ImageView img_container){
//            this.img_container=img_container;
//        }

        public void run() {
            while (!stopFlag) {
                if (ip_address != "") {
                    try {
                        ip_address = "192.168.137.1";
                        socket = SocketFactory.getDefault().createSocket(ip_address, PORT);


                        in = socket.getInputStream();
                        out = socket.getOutputStream();

                        String frame = "";
                        out.write("picture".getBytes());

                        buffer = new DataInputStream(new BufferedInputStream(in));
                        frame = buffer.readLine();
                        if (frame == null)
                            continue;
                        String[] data = frame.split(",");
                        if (Integer.valueOf(data[0]) == data[1].length()) {
//                        Log.d("picture_byte_length", "True");
                            byte[] decodedBytes = Base64.getDecoder().decode(data[1]);
                            show_img(decodedBytes);
                            Thread.sleep(10 * 1000);
                        } else {
//                        Log.d("picture_byte_length", "False");
                        }
                        socket.close();
                        in.close();
                        out.close();
                        buffer.close();

                    } catch (Exception e) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}