package com.example.mapproject

import android.app.Application
import android.content.SharedPreferences
import com.example.mapproject.util.Constants
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class MyApp : Application() {

    @Inject
    lateinit var sharedPref: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        sharedPref.edit()
            .putString(Constants.DEFAULT_LANGUAGE, "fa")
            .apply()
    }
}