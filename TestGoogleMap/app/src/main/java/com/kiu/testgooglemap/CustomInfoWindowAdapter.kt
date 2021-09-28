package com.kiu.testgooglemap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(context:Context):GoogleMap.InfoWindowAdapter {
    val mWindow:View = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null)

    private fun rendowWindowText(marker: Marker?, view: View){
        view.findViewById<TextView>(R.id.title).text = marker?.title ?: ""
        view.findViewById<TextView>(R.id.snippet).text = marker?.snippet ?: ""


    }

    override fun getInfoWindow(p0: Marker?): View {
        rendowWindowText(p0,mWindow)
        return mWindow
    }

    override fun getInfoContents(p0: Marker?): View {
        rendowWindowText(p0,mWindow)
        return mWindow
    }
}