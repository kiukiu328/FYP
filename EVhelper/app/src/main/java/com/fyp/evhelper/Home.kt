package com.fyp.evhelper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.fyp.evhelper.reminder.evmList
import com.fyp.evhelper.reminder.evmListAdapter
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.InputStream
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


lateinit var top10List: Array<TableRow>

lateinit var markers: ArrayList<MutableMap<String, Any>>
lateinit var v: View
lateinit var top10Layout: TableLayout

var onCreate = false
var viewDestroy = false
var isExtend = false
var liveCaptureOn = true

class Home : Fragment() {

    fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false)
        onCreate = true
        viewDestroy = false
        val tvExtendTable: TextView = v.findViewById(R.id.tvExtendTable)
        val svTable: NestedScrollView = v.findViewById(R.id.svTable)
        val cvTable: CardView = v.findViewById(R.id.cvTable)
        val lvMaintenance: ListView = v.findViewById(R.id.lvMaintenance)
        val navigationBar: BottomNavigationView = v.findViewById(R.id.navigationBar)
        val imgLiveStream: ImageView = v.findViewById(R.id.imgLiveStream)
        val btnExpandLiveCapture: Button = v.findViewById(R.id.btnExpandLiveCapture)
        val liveStreamContainer: RelativeLayout = v.findViewById(R.id.LiveStreamContainer)
        val voltValue: TextView = v.findViewById(R.id.voltValue)
        val tempValue: TextView = v.findViewById(R.id.tempValue)
        val humidityValue: TextView = v.findViewById(R.id.humidityValue)
        val pressureValue: TextView = v.findViewById(R.id.pressureValue)
        val humidityBackground: CardView = v.findViewById(R.id.humidityBackground)
        val pressureBackground: CardView = v.findViewById(R.id.pressureBackground)
        val tempBackground: CardView = v.findViewById(R.id.tempBackground)
        val voltBackground: CardView = v.findViewById(R.id.voltBackground)
        var apiResponse: String
        var jObj: JSONObject
        var imageStream: InputStream
        var image: Bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        var booking: HashMap<String, Objects>
        var data: HashMap<String, Objects>

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                booking =
                    snapshot.child("Booking").child("Client01").value as HashMap<String, Objects>
                data = snapshot.child("Data").value as HashMap<String, Objects>
                setBookingHistory(v, booking)
//                setMaintenance(v, data)
//                Log.d(null, "gotDate")
            }

            override fun onCancelled(error: DatabaseError) {
                println("The read failed: " + error.code)
            }
        })
        imgLiveStream.scaleType = ImageView.ScaleType.FIT_XY


        navigationBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nvMain -> {
                    liveStreamContainer.visibility = View.VISIBLE
                    v.findViewById<CardView>(R.id.cvHardware).visibility = View.VISIBLE
                    cvTable.visibility = View.VISIBLE
                    v.findViewById<CardView>(R.id.cvEVM).visibility = View.GONE
                    v.findViewById<CardView>(R.id.cvBooking).visibility = View.GONE
                }
                R.id.nvSub -> {
                    liveStreamContainer.visibility = View.GONE
                    v.findViewById<CardView>(R.id.cvHardware).visibility = View.GONE
                    cvTable.visibility = View.GONE
                    v.findViewById<CardView>(R.id.cvEVM).visibility = View.VISIBLE
                    v.findViewById<CardView>(R.id.cvBooking).visibility = View.VISIBLE
                }
            }
            true
        }


        btnExpandLiveCapture.setOnClickListener {
            Log.d(null, "liveCaptureOn: ${liveCaptureOn}")
            if (liveCaptureOn) {
                imgLiveStream.layoutParams.height = dpToPx(50)
                imgLiveStream.setImageResource(R.color.cardview_shadow_start_color)
                btnExpandLiveCapture.setBackgroundResource(R.drawable.ic_baseline_expand_more_24)
                liveCaptureOn = false
            } else {
                imgLiveStream.layoutParams.height = dpToPx(200)
                imgLiveStream.setImageBitmap(image)
                btnExpandLiveCapture.setBackgroundResource(R.drawable.ic_baseline_expand_less_24)
                liveCaptureOn = true
            }

            imgLiveStream.requestLayout()
            btnExpandLiveCapture.requestLayout()
            liveStreamContainer.requestLayout()
        }

//        set Maintenance LV
        setMaintenance(v, lvMaintenance)


        tvExtendTable.setOnClickListener {
            if (isExtend) {
                svTable.layoutParams.height = dpToPx(75)
                tvExtendTable.text = getString(R.string.seeMore)
                isExtend = false
            } else {
                svTable.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                tvExtendTable.text = getString(R.string.seeLess)
                isExtend = true
            }
            svTable.requestLayout()
            cvTable.requestLayout()
        }
        if (::markers.isInitialized)
            setTop10(markers)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val textSize = 20f
//                when the view is running load the data
                while (!viewDestroy) {
                    try {
                        apiResponse = URL("http://192.168.1.107/").readText()
                        jObj = JSONObject(apiResponse)
                        (context as Activity).runOnUiThread {
                            tempValue.textSize = textSize
                            pressureValue.textSize = textSize
                            humidityValue.textSize = textSize
                            voltValue.textSize = textSize

                            tempValue.text = "${jObj.getString("temperature")}°C"
                            pressureValue.text = "%.0f Pa".format(jObj.getDouble("pressure"))
                            humidityValue.text =
                                "%.0f%%".format(jObj.getDouble("humidity"))
                            voltValue.text = "${jObj.getString("voltage")}v"

                            if (jObj.getDouble("voltage") > 2)
                                voltBackground.setCardBackgroundColor(Color.GREEN)
                            else
                                voltBackground.setCardBackgroundColor(if (jObj.getDouble("voltage") > 1) Color.YELLOW else Color.RED)

                            if (jObj.getDouble("temperature") < 25)
                                tempBackground.setCardBackgroundColor(Color.GREEN)
                            else
                                tempBackground.setCardBackgroundColor(if (jObj.getDouble("temperature") < 30) Color.YELLOW else Color.RED)
                        }

                    } catch (e: Exception) {
                        print(e)
                    }
                    delay(1000)
                }
            }
        }
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                while (!viewDestroy) {
                    if (liveCaptureOn) {
                        try {
                            imageStream = URL("http://192.168.1.171:5000/camera").openStream()
                            image = BitmapFactory.decodeStream(imageStream)

                            (context as Activity).runOnUiThread {
                                imgLiveStream.setImageBitmap(image)
                            }

                        } catch (e: Exception) {
                            print(e)
                        }
                    }
                    delay(60 * 1000)
                }

            }
        }
        return v
    }

    // when fragment not focus in home page stop the functions
    override fun onDestroyView() {
        super.onDestroyView()
        viewDestroy = true
    }

    private fun dpToPx(size: Int): Int {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        return size * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
    }

    private fun setBookingHistory(v: View, booking: HashMap<String, Objects>) {
//        Log.d(null, "setBookingHistory")
        val bookingHistoryList: ListView = v.findViewById(R.id.bookingHistoryList)
        val historyArrayList: ArrayList<HashMap<String, String>> = ArrayList()
        // creating a new array adapter for our list view.
        val adapter = SimpleAdapter(
            v.context,
            historyArrayList,
            R.layout.list_view_booking,
            arrayOf("EntranceTime", "ExitTime", "CarPark", "BookDate"),
            intArrayOf(
                R.id.lv_entrance_time,
                R.id.lv_exit_time,
                R.id.lv_car_park,
                R.id.lv_book_date
            )
        )
        for (i in booking) {
            try {
                i.value as HashMap<*, *>
            } catch (e: java.lang.Exception) {
                continue
            }
            val item: HashMap<String, String> = HashMap()
            item["EntranceTime"] = (i.value as HashMap<*, *>)["entrance_time"].toString()
            item["ExitTime"] = (i.value as HashMap<*, *>)["exit_time"].toString()
            item["CarPark"] = (i.value as HashMap<*, *>)["car_park"].toString()
            item["BookDate"] = (i.value as HashMap<*, *>)["book_date"].toString()

            historyArrayList.add(item)
        }
        historyArrayList.reverse()
        //setting an adapter to our list view.
        bookingHistoryList.adapter = adapter
    }

    private fun setMaintenance(v: View, lv: ListView) {
        val db: SQLiteDatabase
        val cursor: Cursor
        val evCycleList: ArrayList<HashMap<String, String>> = ArrayList()

        try {
            db = SQLiteDatabase.openDatabase(
                "/data/data/com.fyp.evhelper/LicenceDB",
                null,
                SQLiteDatabase.OPEN_READONLY
            )
            cursor = db.rawQuery("SELECT * FROM EVM ", null)
            cursor.count
            while (cursor.moveToNext()) {
                val item: HashMap<String, String> = HashMap()
                item["title"] = cursor.getString(1)
                item["subtitle"] = cursor.getString(2)
                evCycleList.add(item)
            }
            db.close()

            lv.adapter =
                SimpleAdapter(
                    v.context,
                    evCycleList,
                    android.R.layout.simple_list_item_2,
                    arrayOf("title", "subtitle"),
                    intArrayOf(android.R.id.text1, android.R.id.text2)
                )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        fun setTop10(mk: ArrayList<MutableMap<String, Any>>) {
            markers = mk
            if (markers.size == 0)
                return
            v.findViewById<TextView>(R.id.tvExtendTable).visibility = ViewGroup.VISIBLE
            if (onCreate) {
                top10List = Array(11) { TableRow(v.context) }
                top10Layout = v.findViewById(R.id.top10List)
                val tvTableHint: TextView = v.findViewById(R.id.tvTableHint)
                tvTableHint.visibility = View.GONE
                onCreate = false
            }
            top10Layout.removeAllViews()
//            top10Layout.addView(name)
            for (tv in top10List) {
                tv.setOnClickListener {
                    val marker: Marker = markers[top10List.indexOf(tv)]["marker"] as Marker
                    val builder = AlertDialog.Builder(v.context)
                    builder.setTitle(marker.title).setMessage("Do you want to show navigation?")
                        .setCancelable(true)
                        .setPositiveButton("Yes") { _, _ ->
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=${marker.position.latitude},${marker.position.longitude}&mode=l")
                            )
                            intent.setPackage("com.google.android.apps.maps")
                            v.context.startActivity(intent)
                        }
                        .setNeutralButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                }
                top10Layout.addView(tv)
            }
            val num = TextView(v.context)
            num.text = "No. "
            top10List[0].addView(num)
            val marker = TextView(v.context)
            marker.text = "Name"
            top10List[0].addView(marker)
            val distance = TextView(v.context)
            distance.text = "Distance(m)"
            top10List[0].addView(distance)
            for (i in 1..10) {
                val num = TextView(v.context)
                num.text = (i).toString()
                top10List[i].addView(num)
                val marker = TextView(v.context)
                marker.text = (markers[i]["marker"] as Marker).title
                top10List[i].addView(marker)
                val distance = TextView(v.context)
                distance.text = (markers[i]["distance"] as Float).toInt().toString()
                top10List[i].addView(distance)

            }

        }


    }


}