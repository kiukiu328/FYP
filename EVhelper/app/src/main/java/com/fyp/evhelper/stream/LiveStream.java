package com.fyp.evhelper.stream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.evhelper.R;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;

import javax.net.SocketFactory;

public class LiveStream extends AppCompatActivity {

    ImageButton btn_exit, stop_play_btn;
    GetFrame getFrame = null;
    ImageView img_container;
    Intent FirstPage;

    String ip_address = "";
    final int PORT = 9990;
    Handler displayFrame;
    boolean play_pause = true; //true is playing false is pausing

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_stream_page);
        if (ip_address == "") {
            ip_address = getIntent().getExtras().getString("ip_address");
        }
        Log.w("ip address", ip_address);
        init();
    }

    public void controlVideo() {
        Bitmap start = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.play_stream);
        Bitmap pause = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.pause_stream);
        if (play_pause) {
            getFrame.setShow_frame(false);
            play_pause = false;
            stop_play_btn.setImageBitmap(start);
        } else {
            getFrame.setShow_frame(true);
            play_pause = true;
            stop_play_btn.setImageBitmap(pause);
        }
    }

    private void init() {
        btn_exit = findViewById(R.id.btn_exit);
        img_container = findViewById(R.id.stream_content);
        stop_play_btn = findViewById(R.id.stop_play_btn);
        //开始直播
        getFrame = new GetFrame();
        getFrame.start();
//        Toast.makeText(getApplicationContext(),"show live",Toast.LENGTH_SHORT).show();

        FirstPage = new Intent(this, Stream.class);

        stop_play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlVideo();
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "exit button", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        displayFrame = new Handler() {
            public void handleMessage(Message msg) {
                img_container.setImageBitmap((Bitmap) msg.obj);
            }
        };

    }

    protected void onRestart() {
        super.onRestart();
//        Toast.makeText(getApplicationContext(),"Activity restart",Toast.LENGTH_SHORT).show();
        if (getFrame != null) {
            getFrame = new GetFrame();
            getFrame.start();
        }
    }

    protected void onStop() {
        super.onStop();
//        Toast.makeText(getApplicationContext(),"Activity Stop",Toast.LENGTH_SHORT).show();
        if (getFrame != null) {
            getFrame.setStopFlag(true);
        }
    }

    class GetFrame extends Thread {
        boolean show_frame = true;
        private volatile boolean stopFlag = false;
//        ImageView img_container;
//        public GetFrame(ImageView img_container){
//            this.img_container=img_container;
//
//        }

        public void setShow_frame(boolean show_frame) {
            this.show_frame = show_frame;
        }


        public void run() {
            while (!stopFlag) {
                if (ip_address != "") {
                    if (show_frame == true) {
                        try {
                            Socket socket = SocketFactory.getDefault().createSocket(ip_address, PORT);
                            Log.d(null,"LiveStream Socket");
                            Log.d("ip", ip_address);
                            Log.d("PORT", "" + PORT);
                            InputStream in = socket.getInputStream();
                            OutputStream out = socket.getOutputStream();

                            String frame = "";
                            out.write("stream".getBytes());

                            DataInputStream buffer = new DataInputStream(new BufferedInputStream(in));
                            frame = buffer.readLine();
                            String[] data = frame.split(",");
                            if (Integer.valueOf(data[0]) == data[1].length()) {
                                Log.d("stream_byte_length", "True");
                                byte[] decodedBytes = Base64.getDecoder().decode(data[1]);
                                show_img(decodedBytes);
                            } else {
                                Log.d("stream_byte_length", "False");
                            }
                            socket.close();
                            in.close();
                            out.close();
                            buffer.close();
//                    Log.d("byte_length", Integer.toString(decodedBytes.length));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        public void setStopFlag(boolean stopFlag) {
            this.stopFlag = stopFlag;
        }

        public void show_img(byte[] bytesFrame) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytesFrame, 0, bytesFrame.length);
//                this.img_container.setImageBitmap(bitmap);
                Message message = new Message();
                message.obj = bitmap;
                displayFrame.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
