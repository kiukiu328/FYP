package com.fyp.evhelper

import android.content.res.Configuration
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import java.util.*


class Setting : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_setting, container, false)

        val configuration = Configuration()
        val rgLang: RadioGroup = v.findViewById(R.id.rgLanguage)
        configuration.locale = Locale("zh-hk")
        (v.context as ContextThemeWrapper).applyOverrideConfiguration(configuration)
//        LocaleHelper.setLocale(v.context, "zh-hk");
//        val context = LocaleHelper.setLocale(MainActivity.this, "en")
//        val resources = context.getResources()
//        messageView.setText(resources.getString(R.string.language));

        return v
    }


}