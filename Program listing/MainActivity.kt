package com.fyp.evhelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.fyp.evhelper.reminder.ReminderMainPage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import com.fyp.evhelper.stream.*

// for other program to go back homepage
lateinit var animatedBottomBar: AnimatedBottomBar

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    lateinit var fManager: FragmentManager
    lateinit var homeFragment: Fragment
    lateinit var mapFragment: Fragment
    lateinit var streamFragment: Fragment
    lateinit var reminderFragment: Fragment

    //    set the companion object for other program to get the public
    companion object {
        var androidID: String = ""
        val SERVER_PATH = "192.168.1.171"
        fun homePage() {
            animatedBottomBar.selectTabAt(2)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        androidID for identify the device fo notification
        androidID = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        println("androidID:" + androidID)
        var token = ""
//        Firebase for getting messaging
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    PushNotificationService.TAG,
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result
            Log.d("Main token", token)
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    sendToken(androidID, token)
                }
            }
        })

// set all parameter
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        animatedBottomBar = findViewById(R.id.animatedBottomBar)
        animatedBottomBar.selectTabById(R.id.home, true)
        fManager = supportFragmentManager
        homeFragment = Home()
        mapFragment = Map()
        streamFragment = Stream()
        reminderFragment = ReminderMainPage()
        fManager!!.beginTransaction().replace(R.id.fragment_container, homeFragment)
            .commit()
// set the navigation bar
        animatedBottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                var fragment: Fragment? = null
                when (newTab.id) {
                    R.id.home -> fragment = homeFragment
                    R.id.map -> fragment = mapFragment
                    R.id.stream -> fragment = streamFragment
                    R.id.reminder -> fragment = reminderFragment
//                    R.id.setting -> fragment = SettingFragment()

                }
                if (fragment != null) {
                    fManager = supportFragmentManager
                    fManager!!.beginTransaction().replace(R.id.fragment_container, fragment)
                        .commit()
                } else {
                    Log.e(TAG, "Error in creating Fragment")
                }
            }
        })
    }

    // send Token to server for notification
    fun sendToken(id: String, token: String) {
        var reqParam = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
        reqParam += "&" + URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(
            token,
            "UTF-8"
        )

        val mURL = URL("http://$SERVER_PATH:5000/sendToken")
        val connection = mURL.openConnection()
        connection.doOutput = true
        with(connection as HttpURLConnection) {

            // optional default is GET
            requestMethod = "POST"

            val wr = OutputStreamWriter(outputStream);
            wr.write(reqParam)
            wr.flush()

//        read the response
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