package com.fyp.evhelper

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL


lateinit var top10List: Array<TableRow>

//lateinit var top10List: Array<TextView>
lateinit var markers: ArrayList<MutableMap<String, Any>>
lateinit var v: View
lateinit var top10Layout: TableLayout

//lateinit var top10Layout: LinearLayout
//lateinit var name: TextView
var onCreate = false
var viewDestroy = false
var isExtend = false

class Home : Fragment() {
    fun getEmojiByUnicode(unicode: Int): String? {
        return String(Character.toChars(unicode))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false)
        Log.d(null, "onCreateView")
        onCreate = true
        viewDestroy = false
        val tvExtendTable: TextView = v.findViewById(R.id.tvExtendTable)
        val svTable: ScrollView = v.findViewById(R.id.svTable)
        val lvMaintenance:ListView = v.findViewById(R.id.lvMaintenance)

        lvMaintenance.adapter = ArrayAdapter<String>(v.context,android.R.layout.simple_list_item_1, arrayOf("Air Filter"))
        Log.d(null, "tvExtendTable: $tvExtendTable")
        tvExtendTable.setOnClickListener {
            Log.d(null, "getPixelsFromDp(75)${getPixelsFromDp(75)}")
            Log.d(null,"isExtend = $isExtend")
            if (isExtend) {
                svTable.layoutParams.height = getPixelsFromDp(75)
                svTable.requestLayout()
                tvExtendTable.text = "See more..."
                isExtend = false
            } else {
                svTable.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                svTable.requestLayout()
                tvExtendTable.text = "See less..."
                isExtend = true
            }
        }
        if (::markers.isInitialized)
            setTop10(markers)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val tvCarData: TextView = v.findViewById(R.id.tvCarData)
                var apiResponse: String
                var jObj: JSONObject
//                when the view is running load the data
                while (!viewDestroy) {

                    try {
                        apiResponse = URL("http://192.168.1.107/").readText()
                        jObj = JSONObject(apiResponse)
//                        println("${apiResponse}")
                        (context as Activity).runOnUiThread {
                            tvCarData.text =
                                "${getEmojiByUnicode(0x1F321)}  Temperature:  ${jObj.getString("temperature")} \n" +
                                        "${getEmojiByUnicode(0x1F4A8)}  Pressure : %.2f \n".format(
                                            jObj.getDouble("pressure")
                                        ) +
                                        "${getEmojiByUnicode(0x1F4A6)}   Humidity :  %.2f\n".format(
                                            jObj.getDouble("humidity")
                                        ) +
                                        "${getEmojiByUnicode(0x26A1)}   Voltage :  ${
                                            jObj.getString(
                                                "voltage"
                                            )
                                        }  \n"
                        }
                    } catch (e: Exception) {

                        print(e)
                    }
                    delay(1000)
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

    companion object {
        //        set the nearest charge station list
//        fun setTop10(mk: ArrayList<MutableMap<String, Any>>) {
//            markers = mk
//            if (markers.size == 0)
//                return
//            if (onCreate) {
//                top10List = Array(10) { TextView(v.context) }
//                top10Layout = v.findViewById(R.id.top10List)
//                name = TextView(v.context)
//                name.text = "     Name [Distance(m)]"
//                name.textSize = 20f
//                val title: TextView = v.findViewById(R.id.title)
//                title.text = "Nearest charge station"
//                title.textSize = 30f
//                onCreate = false
//            }
//            top10Layout.removeAllViews()
//            top10Layout.addView(name)
//            for (tv in top10List) {
//                tv.setOnClickListener {
//                    val marker: Marker = markers[top10List.indexOf(tv)]["marker"] as Marker
//                    val builder = AlertDialog.Builder(v.context)
//                    builder.setTitle(marker.title).setMessage("Do you want to show navigation?")
//                        .setCancelable(true)
//                        .setPositiveButton("Yes") { _, _ ->
//                            val intent = Intent(
//                                Intent.ACTION_VIEW,
//                                Uri.parse("google.navigation:q=${marker.position.latitude},${marker.position.longitude}&mode=l")
//                            )
//                            intent.setPackage("com.google.android.apps.maps")
//                            v.context.startActivity(intent)
//                        }
//                        .setNeutralButton("Cancel") { dialog, _ ->
//                            dialog.dismiss()
//                        }
//                    val alert = builder.create()
//                    alert.show()
//                }
//                top10Layout.addView(tv)
//            }
//            for (i in 0..9) {
//                top10List[i].textSize = 20f
//                top10List[i].text =
//                    "${(i + 1)}: ${(markers[i]["marker"] as Marker).title}  [${(markers[i]["distance"] as Float).toInt()}]"
//                top10List[i].setPadding(10, 10, 10, 10)
//            }
//        }
        fun setTop10(mk: ArrayList<MutableMap<String, Any>>) {
            markers = mk
            if (markers.size == 0)
                return
            v.findViewById<TextView>(R.id.tvExtendTable).visibility = ViewGroup.VISIBLE
            if (onCreate) {
                top10List = Array(11) { TableRow(v.context) }
                top10Layout = v.findViewById(R.id.top10List)
//                name = TextView(v.context)
//                name.text = "     Name [Distance(m)]"
//                name.textSize = 20f
                val title: TextView = v.findViewById(R.id.title)
                title.text = "Nearest charging station"
                title.textSize = 20f
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

//                top10List[i].textSize = 20f
//                top10List[i].text =
//                    "${(i + 1)}: ${(markers[i]["marker"] as Marker).title}  [${(markers[i]["distance"] as Float).toInt()}]"
//                top10List[i].setPadding(10, 10, 10, 10)
            }
//            val btn = Button(v.context)
//            top10Layout.addView(btn)
        }


    }

    private fun getPixelsFromDp(size: Int): Int {
        val metrics = DisplayMetrics()
        activity?.getWindowManager()?.getDefaultDisplay()?.getMetrics(metrics)
        return size * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
    }
}