package com.fyp.evhelper.stream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.fyp.evhelper.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class Stream : Fragment() {
    private lateinit var viewPager2: ViewPager2
    private var tabLayout: TabLayout? = null
    private val tab_label = arrayOf("Live", "Records")
    private val drables = intArrayOf(R.drawable.live, R.drawable.record)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_stream, container, false)
        viewPager2 = v.findViewById(R.id.viewPager2)
        tabLayout = v.findViewById(R.id.tabLayout)
        val adapter = ViewPagerAdapter(activity)
        viewPager2.adapter = adapter
        TabLayoutMediator(
            tabLayout!!, viewPager2,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                val show_view: View = makeTabView(position)
                tab.customView = show_view
            }).attach()

        return v
    }

    fun makeTabView(position: Int): View {
        val tabView:View = LayoutInflater.from(context).inflate(R.layout.tab_list, null)
        val textView = tabView.findViewById<TextView>(R.id.textview)
        val imageView = tabView.findViewById<ImageView>(R.id.imageview)
        textView.text = tab_label[position]
        imageView.setImageResource(drables[position])
        return tabView
    }
}