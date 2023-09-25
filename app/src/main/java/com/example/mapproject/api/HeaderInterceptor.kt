package com.example.mapproject.api

import android.content.SharedPreferences
import android.os.Build
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


class HeaderInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences
): Interceptor {

     private lateinit var userAgent: String
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val versionAndroid = Build.VERSION.RELEASE
        userAgent = "$manufacturer / $model / Android Version: $versionAndroid"
        val requestWithUserAgent: Request = originalRequest.newBuilder()
           // .header("User-Agent","Koja/Android")
            .header("User-Agent",userAgent)
            .build()
        return chain.proceed(requestWithUserAgent)
    }

}