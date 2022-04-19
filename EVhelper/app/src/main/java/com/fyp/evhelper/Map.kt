package com.fyp.evhelper

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.*
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.lang.Exception
import java.net.URL
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.view.isVisible
import com.fyp.evhelper.book.Book
import com.google.android.gms.location.*
import org.json.JSONObject
import java.io.BufferedReader
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URLEncoder


class Map : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var circle: Circle
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var zoom = false

    private var locationManager: LocationManager? = null
    private var selectedMarker: Marker? = null
    private var chargeMarkers: ArrayList<MutableMap<String, Any>> = ArrayList()
    private var parkMarkers: ArrayList<MutableMap<String, Any>> = ArrayList()
    private var markerInfo = mutableMapOf<String, Any>()
    private lateinit var sortedChargeMarkers: ArrayList<MutableMap<String, Any>>
// for the save name to a readable name
    private val socketList = mapOf(
        "GBQSocket" to "Quick - GB/T 20234.2",
        "IECComSocket" to "Quick - CCS Combo 2",
        "IECQSocket" to "Quick - IEC 62196 Type 2",
        "CdMSocket" to "Quick - CHAdeMO",
        "GBSocket" to "Medium - GB/T  20234.2",
        "IECSocket" to "Medium - IEC 62196 Type 2",
        "BSSocket" to "Standard - BS 1363",
        "SAESocket" to "Medium - SAE J1772",
        "TeslaSuperSocket" to "Tesla Supercharger",
        "TeslaRoadsterSocket" to "Tesla Roadster Charger"
    )


    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    private lateinit var infoCard: CardView
    private lateinit var infoCardText: TextView
    private lateinit var booking: Button
    private lateinit var navigation: Button
// when the location change send the location to server
    var float: FloatArray = floatArrayOf(0f)
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("onLocationChanged", "" + location.longitude + ":" + location.latitude)
//move the camera
            lastLocation = location
            try {
                val currentLatLong = LatLng(lastLocation.latitude, lastLocation.longitude)
                if (zoom) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 1f))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 15f))
                    zoom = false
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLong))
                    circle.center = currentLatLong
                    if (circle.isVisible) {
                        for (i in chargeMarkers)
                            (i["marker"] as Marker).isVisible =
                                (i["distance"] as Float) < circle.radius
                        for (i in parkMarkers)
                            (i["marker"] as Marker).isVisible =
                                (i["distance"] as Float) < circle.radius
                    }
                    setTV()
                }
//                send location to server
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        try {

                            sendPostRequest(
                                MainActivity.androidID,
                                location.latitude.toString(),
                                location.longitude.toString()
                            )
                        } catch (e: Exception) {
                            print(e)
                        }
                    }
                }

            } catch (e: Exception) {
                Log.d(null, e.toString())
            }
        }
// implement interface method
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.fragment_map, container, false)

        // Inflate the layout for this fragment
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

// set the map
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager?
//        ask for permission
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return v
        }
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L,
            100f,
            locationListener
        )


//        Set Filter
        val chargeBtn: ToggleButton = v.findViewById(R.id.chargeBtn)
        val parkBtn: ToggleButton = v.findViewById(R.id.parkBtn)
        val scopeBtn: ToggleButton = v.findViewById(R.id.scopeBtn)

        val chargeFilter: CardView = v.findViewById(R.id.chargeFilter)
        val chargeFilterAll: CheckBox = v.findViewById(R.id.chargeFilterAll)
        val chargeFilterConfirm: Button = v.findViewById(R.id.chargeFilterConfirm)
        val parkFilter: CardView = v.findViewById(R.id.parkFilter)
        val parkFilterShowEmpty: CheckBox = v.findViewById(R.id.parkFilterShowEmpty)
        val parkFilterConfirm: Button = v.findViewById(R.id.parkFilterConfirm)


        infoCard = v.findViewById(R.id.infoCard)
        infoCardText = v.findViewById(R.id.infoCardText)
        booking = v.findViewById(R.id.booking)
        navigation = v.findViewById(R.id.navigation)

        val ll = v.findViewById<LinearLayout>(R.id.chargeFilterLinearLayout)
        val checkBoxs: HashMap<String, CheckBox> = HashMap()

        for ((key, value) in socketList) {
            val cb = CheckBox(context)
            cb.text = value
            cb.setTextColor(Color.BLACK)
            cb.buttonTintList = ColorStateList.valueOf(Color.BLACK)
            cb.isChecked = true
            cb.setOnClickListener {
                var allChecked = true
                for ((_, cb) in checkBoxs)
                    if (!cb.isChecked)
                        allChecked = false
                chargeFilterAll.isChecked = allChecked


            }
            checkBoxs[key] = cb
            ll.addView(cb)
        }
//        set scope filter
        scopeBtn.setOnCheckedChangeListener { _, isChecked ->
            circle.isVisible = isChecked
            if (!isChecked) {

                for (i in chargeMarkers)
                    (i["marker"] as Marker).isVisible = true
                for (i in parkMarkers)
                    (i["marker"] as Marker).isVisible = true
            } else {
                for (i in chargeMarkers)
                    (i["marker"] as Marker).isVisible = (i["distance"] as Float) < circle.radius
                for (i in parkMarkers)
                    (i["marker"] as Marker).isVisible = (i["distance"] as Float) < circle.radius
            }
        }
        scopeBtn.setOnLongClickListener {
            val dialogView: View = inflater.inflate(R.layout.circle_dialog, container, false)
            val sbNum: SeekBar = dialogView.findViewById(R.id.sbNum)
            val etNum: EditText = dialogView.findViewById(R.id.etNum)
            etNum.setText(circle.radius.toString())
            sbNum.progress = circle.radius.toInt()
            sbNum.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    etNum.setText(sbNum.progress.toString())
                    Log.d("onProgressChanged", etNum.text.toString())
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}

            })
// set distance
            etNum.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    sbNum.progress = Integer.parseInt(etNum.text.toString())

                    Log.d("onTextChanged", sbNum.progress.toString())
                }

                override fun afterTextChanged(p0: Editable?) {}

            })

            val builder = AlertDialog.Builder(v.context)
            builder.setMessage("Select distance (m)")
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    circle.radius = etNum.text.toString().toDouble()
                }
                .setView(dialogView)
            val alert = builder.create()
            alert.show()
            true
        }
//        set charge filter
        chargeBtn.setOnCheckedChangeListener { _, isChecked ->

            var haveSocket: Boolean
            if (!isChecked)
                for (i in chargeMarkers) {
                    (i["marker"] as Marker).isVisible = false
                }
            else
                if (chargeFilterAll.isChecked)
                    for (i in chargeMarkers) {
                        (i["marker"] as Marker).isVisible = true
                    }
                else
                    for (i in chargeMarkers) {
                        haveSocket = false
                        for ((k, v) in checkBoxs) {
                            if ((i["sockets"] as? JSONObject) != null && (i["sockets"] as? JSONObject)!!.has(
                                    k
                                ) && v.isChecked
                            )
                                haveSocket = true
                        }
                        (i["marker"] as Marker).isVisible = haveSocket
                    }
        }
//      charge filter
        chargeBtn.setOnLongClickListener {
            infoCard.isVisible = false
            chargeFilter.isVisible = true
            parkFilter.isVisible = false
            true
        }
        parkBtn.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked)
                for (i in parkMarkers) {
                    (i["marker"] as Marker).isVisible = false
                }
            else
                if (parkFilterShowEmpty.isChecked)
                    for (i in parkMarkers) {
                        (i["marker"] as Marker).isVisible = true
                    } else
                    for (i in parkMarkers) {
                        (i["marker"] as Marker).isVisible = (i["vacancy"] as Int) > 0
                    }
        }
        parkBtn.setOnLongClickListener {
            infoCard.isVisible = false
            parkFilter.isVisible = true
            chargeFilter.isVisible = false
            true
        }
        chargeFilterAll.setOnClickListener {
            for ((_, cb) in checkBoxs)
                cb.isChecked = chargeFilterAll.isChecked
        }
        chargeFilterConfirm.setOnClickListener {
            chargeBtn.isChecked = false
            chargeBtn.isChecked = true
            chargeFilter.isVisible = false
        }
        parkFilterConfirm.setOnClickListener {
            parkBtn.isChecked = false
            parkBtn.isChecked = true
            parkFilter.isVisible = false
        }
//        end of Set Filter
        booking.setOnClickListener {
            val intent = Intent(context, Book::class.java)
            intent.putExtra("title", selectedMarker?.title)
            infoCard.isVisible = false
            startActivity(intent)
        }
        navigation.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=${selectedMarker?.position?.latitude},${selectedMarker?.position?.longitude}&mode=l")
            )
            intent.setPackage("com.google.android.apps.maps")
            infoCard.isVisible = false
            startActivity(intent)

        }
//        end of onCreateView

        return v

    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(null, "onMapReady")
        zoom = true
        mMap = googleMap
        mMap.setInfoWindowAdapter(context?.let { CustomInfoWindowAdapter(it) })
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener {
            infoCard.isVisible = false
        }


        //ask permission if don't have
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        //focus to current location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                circle = mMap.addCircle(
                    CircleOptions().center(currentLatLong).radius(1000.0)
                        .strokeColor(Color.argb(100, 255, 255, 153))
                        .fillColor(Color.argb(100, 255, 255, 153))
                )
                circle.isVisible = false
            }

        }

        //start to add the makers with threading
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                addMarkers()
            }
        }


    }

    private suspend fun addMarkers() {
        withContext(Dispatchers.IO) {
            // get json object save to json array
            chargeMarkers = ArrayList()
            parkMarkers = ArrayList()
            var jObjChargeStation = JSONArray()
            var jObjVacancy = JSONArray()
//            load data from server
            try {
                (URL("https://${MainActivity.SERVER_PATH}/python/main.py?type=echarge_en").openConnection() as HttpsURLConnection).apply {
                    sslSocketFactory = createSocketFactory(listOf("TLSv1.2"))
                    hostnameVerifier = HostnameVerifier { _, _ -> true }
                    readTimeout = 50_000
                }.inputStream.use {
                    jObjChargeStation = JSONArray(it.bufferedReader().use(BufferedReader::readText))
                    (URL("https://${MainActivity.SERVER_PATH}/python/main.py?type=vacancy").openConnection() as HttpsURLConnection).apply {
                        sslSocketFactory = createSocketFactory(listOf("TLSv1.2"))
                        hostnameVerifier = HostnameVerifier { _, _ -> true }
                        readTimeout = 50_000
                    }.inputStream.use {
                        jObjVacancy = JSONArray(it.bufferedReader().use(BufferedReader::readText))
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                var name: String
                var latitude: Double
                var longitude: Double
                var snippet = "<p>Sockets: "
                var vacancy: Int
                var tag: Int
                var marker: Marker
//                Add Charge Station marker
                for (i in 0 until jObjChargeStation.length()) {
                    name = jObjChargeStation.getJSONObject(i).getString("carPark")
                    latitude = jObjChargeStation.getJSONObject(i).getDouble("latitude")
                    longitude = jObjChargeStation.getJSONObject(i).getDouble("longitude")
//set the information box display data
                    snippet += "<p>Addresss: ${
                        jObjChargeStation.getJSONObject(i).get("address")
                    }</p>"

                    snippet += "<p>Sockets: "
                    if (jObjChargeStation.getJSONObject(i).has("sockets"))
                        for (item in jObjChargeStation.getJSONObject(i).keys()) {
                            markerInfo[item as String] =
                                jObjChargeStation.getJSONObject(i).get(item)
                            if (item == "sockets")
                                for (sockets in jObjChargeStation.getJSONObject(i)
                                    .getJSONObject(item).keys())
                                    if (sockets in socketList.keys) {
                                        snippet += "${socketList[sockets]} x ${
                                            jObjChargeStation.getJSONObject(
                                                i
                                            ).getJSONObject("sockets").get(sockets)
                                        }<br>"
                                    }

                        }
                    else
                        snippet += "N/A"
                    snippet += "</p>"
                    snippet += "<p>Charger Cost: ${
                        jObjChargeStation.getJSONObject(i).get("chargerCost")
                    }</p>"
                    marker = mMap.addMarker(
                        MarkerOptions().position(LatLng(latitude, longitude)).title(name)
                            .snippet(snippet)
                            .icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    bitmap(
                                        jObjChargeStation.getJSONObject(
                                            i
                                        ).getInt("vacancyEV"), R.drawable.tag_charge
                                    )
                                )
                            )
                    )!!
                    markerInfo["marker"] = marker

                    Location.distanceBetween(
                        lastLocation.latitude,
                        lastLocation.longitude,
                        marker.position.latitude,
                        marker.position.longitude,
                        float
                    )
                    markerInfo["distance"] = float[0]

                    chargeMarkers.add(markerInfo)
                    markerInfo = mutableMapOf()
                    snippet = ""
                }

//                Add Vacancy marker
                for (i in 0 until jObjVacancy.length()) {
                    for (item in jObjChargeStation.getJSONObject(i).keys()) {
                        markerInfo.put(item as String, jObjChargeStation.getJSONObject(i).get(item))

                    }
                    name = jObjVacancy.getJSONObject(i).getString("name")
                    latitude = jObjVacancy.getJSONObject(i).getDouble("latitude")
                    longitude = jObjVacancy.getJSONObject(i).getDouble("longitude")
                    snippet += ""
                    try {
                        vacancy =
                            jObjVacancy.getJSONObject(i).getJSONArray("privateCar").getJSONObject(0)
                                .getInt("vacancy")


                        if (vacancy == 0 || vacancy == -1) {
                            tag = R.drawable.tag_full
                            snippet += "<p>Vacancy: <span style=\"color:red;\">${if (vacancy == 0) "0" else "N/A"}</span></p>"
                        } else if (vacancy < 3) {
                            tag = R.drawable.tag_almost_full
                            snippet += "<p>Vacancy: <span style=\"color:#FF6347;\">${vacancy}</span></p>"
                        } else {
                            tag = R.drawable.tag
                            snippet += "<p>Vacancy: <span style=\"color:green;\">${vacancy}</span></p>"
                        }

                    } catch (e: Exception) {
                        vacancy = -1
                        snippet += "Vacancy: No information"
                        tag = R.drawable.tag_full
                    }
                    markerInfo["vacancy"] = vacancy


                    marker = mMap.addMarker(
                        MarkerOptions().position(LatLng(latitude, longitude)).title(name)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap(vacancy, tag)))
                    )!!
                    markerInfo["marker"] = marker

                    Location.distanceBetween(
                        lastLocation.latitude,
                        lastLocation.longitude,
                        marker.position.latitude,
                        marker.position.longitude,
                        float
                    )
                    markerInfo["distance"] = float[0]

                    parkMarkers.add(markerInfo)
                    markerInfo = mutableMapOf()
                    snippet = ""
                }
                setTV()
            }
        }

    }

    //  Show number on tag
    private fun bitmap(vacancy: Int, tag: Int): Bitmap {
        val conf = Bitmap.Config.ARGB_8888
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
        try {

            val bmp = Bitmap.createBitmap(
                resources.getDrawable(R.drawable.tag).intrinsicWidth,
                resources.getDrawable(R.drawable.tag).intrinsicHeight,
                conf
            )
            val canvas = Canvas(bmp)
            canvas.drawBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    tag
                ), 0f, 0f, paint
            )
            paint.textSize = canvas.width / 3f
            val display: String = if (vacancy == -1) "N/A" else vacancy.toString()
            canvas.drawText(display, canvas.width / 2f, canvas.height / 2.5f, paint)
            return bmp
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Bitmap.createBitmap(10, 10, conf)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
//        set display info and zoom in
        infoCardText.text =
            HtmlCompat.fromHtml("<h1>${p0?.title}</h1>${p0?.snippet}", FROM_HTML_MODE_LEGACY)
        infoCard.isVisible = true
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p0?.position, 15f))

        selectedMarker = p0

        return true
    }
//  link server with ssl
    private fun createSocketFactory(protocols: List<String>) =
        SSLContext.getInstance(protocols[0]).apply {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) =
                    Unit

                override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) =
                    Unit
            })
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory

    private fun setTV() {
// Set the textview
        for (i in chargeMarkers) {

            Location.distanceBetween(
                lastLocation.latitude,
                lastLocation.longitude,
                (i["marker"] as Marker).position.latitude,
                (i["marker"] as Marker).position.longitude,
                float
            )
            i["distance"] = float[0]

        }
        for (i in parkMarkers) {

            Location.distanceBetween(
                lastLocation.latitude,
                lastLocation.longitude,
                (i["marker"] as Marker).position.latitude,
                (i["marker"] as Marker).position.longitude,
                float
            )
            i["distance"] = float[0]

        }
        try {
            sortedChargeMarkers = ArrayList(chargeMarkers.sortedBy { it["distance"] as Float })
            Home.setTop10(sortedChargeMarkers)
        } catch (e: Exception) {
            Log.d(null, e.toString())
        }
    }
// send http post request to server for notification function
    fun sendPostRequest(id: String, latitude: String, longitude: String) {

        var reqParam = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
        reqParam += "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(
            latitude,
            "UTF-8"
        )
        reqParam += "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(
            longitude,
            "UTF-8"
        )
        val mURL = URL("http://${MainActivity.SERVER_PATH}:5000/location")
        val connection = mURL.openConnection()
        connection.doOutput = true
        with(connection as HttpURLConnection) {

            // optional default is GET
            requestMethod = "POST"

            val wr = OutputStreamWriter(outputStream);
            wr.write(reqParam);
            wr.flush();

//            println("URL : $url")
//            println("Response Code : $responseCode")

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






















