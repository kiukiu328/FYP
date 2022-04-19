package com.fyp.evhelper

import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class PushNotificationService : FirebaseMessagingService() {
    companion object {
        const val TAG = "PUSH_Android"
    }


    override fun onNewToken(p0: String) {
        Log.d(TAG, "Refreshed token: $p0")
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                sendToken(MainActivity.androidID, p0)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            Toast.makeText(applicationContext,"Detects uniformed personnel", Toast.LENGTH_LONG)
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
            } else {
                // Handle message within 10 seconds
//                handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    fun sendToken(id: String, token: String) {

        var reqParam = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
        reqParam += "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(
            token,
            "UTF-8"
        )

        val mURL = URL("http://${MainActivity.SERVER_PATH}:5000/location")
        val connection = mURL.openConnection()
        connection.doOutput = true
        with(connection as HttpURLConnection) {

            // optional default is GET
            requestMethod = "POST"

            val wr = OutputStreamWriter(outputStream);
            wr.write(reqParam)
            wr.flush()


            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                println("Response : $response")
            }
        }
    }

}