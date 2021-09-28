package com.kiu.testgooglemap

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_maps)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        //ask permission if don't have
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        //focus to current location
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 15f))
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

            var jObjChargeStation = ""
            val urlPath = "https://10.0.2.2:8080/"
            try {
                (URL(urlPath).openConnection() as HttpsURLConnection).apply {
                    sslSocketFactory = createSocketFactory(listOf("TLSv1.2"))
                    hostnameVerifier = HostnameVerifier { _, _ -> true }
                    readTimeout = 5_000
                }.inputStream.use {
                    jObjChargeStation = it.bufferedReader().use(BufferedReader::readText)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // get json object save to json array
            var chargeStationJsonArray: JSONArray =
                JSONObject(jObjChargeStation).getJSONObject("ChargingStationData")
                    .getJSONObject("stationList").getJSONArray("station")
            val jObjInfo =
                JSONObject(URL("https://resource.data.one.gov.hk/td/carpark/basic_info_all.json").readText())
            var infoJsonArray: JSONArray = jObjInfo.getJSONArray("car_park")
            val jObjVacancy =
                JSONObject(URL("https://resource.data.one.gov.hk/td/carpark/vacancy_all.json").readText())
            var vacancyJsonArray: JSONArray = jObjVacancy.getJSONArray("car_park")


            var name = ""
            var latitude = 0.0
            var longitude = 0.0
            var snippet = ""
            var parkID = ""
            var vacancy = 0
            var vacancyMap = mapOf<String, JSONArray>()
            withContext(Dispatchers.Main) {
                for (i in 0 until vacancyJsonArray.length()) {
                    vacancyMap += mapOf(
                        vacancyJsonArray.getJSONObject(i)
                            .getString("park_id") to vacancyJsonArray.getJSONObject(i)
                            .getJSONArray("vehicle_type")
                    )
                }

                for (i in 0 until infoJsonArray.length()) {
                    parkID = infoJsonArray.getJSONObject(i).getString("park_id")
                    name = infoJsonArray.getJSONObject(i).getString("name_tc")
                    latitude = infoJsonArray.getJSONObject(i).getDouble("latitude")
                    longitude = infoJsonArray.getJSONObject(i).getDouble("longitude")
                    snippet = infoJsonArray.getJSONObject(i).getString("remark_tc")
                    try {
                        vacancy = vacancyMap[parkID]?.getJSONObject(0)
                            ?.getJSONArray("service_category")
                            ?.getJSONObject(0)
                            ?.getInt("vacancy") ?: 0
                    } catch (e: Exception) {
                        vacancy = -1
                        Log.d("Exception", e.toString())
                    }

//            snippet = if (height == 0.0) "沒有" else height.toString()"高度限制:"
                    snippet += "\nvacancy:${if (vacancy != -1) vacancy else "no information"}"


                    mMap.addMarker(
                        MarkerOptions().position(LatLng(latitude, longitude)).title(name)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.tag))
                    )
                }
                for (i in 0 until chargeStationJsonArray.length()) {
                    name = chargeStationJsonArray.getJSONObject(i).getString("location")
                    latitude = chargeStationJsonArray.getJSONObject(i).getString("lat").toDouble()
                    longitude = chargeStationJsonArray.getJSONObject(i).getString("lng").toDouble()

                    mMap.addMarker(
                        MarkerOptions().position(LatLng(latitude, longitude)).title(name)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.tag_charge))
                    )

                }
            }
        }
    }

    override fun onMarkerClick(p0: Marker?) = false

    // link Https URL Connection without ssl
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

}

