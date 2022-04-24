package com.fyp.evhelper

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import java.util.*

class ContextWrapper(base: Context?) : android.content.ContextWrapper(base) {
    companion object {
        fun wrap(context: Context, newLocale: Locale?): ContextWrapper {
            var context = context
            val res: Resources = context.resources
            val configuration: Configuration = res.configuration

            configuration.setLocale(newLocale)
            val localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            context = context.createConfigurationContext(configuration)

            return ContextWrapper(context)
        }
    }
}