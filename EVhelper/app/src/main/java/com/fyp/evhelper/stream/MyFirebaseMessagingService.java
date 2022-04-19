package com.fyp.evhelper.stream;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.fyp.evhelper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public void onCreate(){
        super.onCreate();
        Log.w("FirebaseFile", "FirebaseFile initial");

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();

                onNewToken(token);
            }
        });
    }



    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("Token", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }


    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.w("data",""+remoteMessage.getData());
        Log.d("onMessageReceived","execute");
        String title =remoteMessage.getData().get("title");
        String body =remoteMessage.getData().get("body");
        sendNotification(title,body);


//        Intent mDisplayAlert = new Intent(this,DisplayAlert.class);
//        mDisplayAlert.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        mDisplayAlert.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mDisplayAlert.putExtra("body",body);
//        getApplicationContext().startActivity(mDisplayAlert);
    }




    private void sendNotification(String title,String messageBody) {
        Intent intent = new Intent(this, Stream.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


//        Uri soundURL = Uri.parse("android.resource://" + getPackageName() + "/raw/correct.mp3");

        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.safetyalert);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.setting)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setLargeIcon(bitmapDrawable.getBitmap())
                        .setVibrate(new long[]{0,500})
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setOnlyAlertOnce(true)
                        .setAutoCancel(true)
//                        .setSound(soundURL)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }




    private void sendRegistrationToServer(String token) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("AndroidToken");
        ref.setValue(token);
    }

}