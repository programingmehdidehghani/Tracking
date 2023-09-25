package com.example.mapproject.ui.spalsh

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityLoginBinding
import com.example.mapproject.databinding.ActivitySplashBinding
import com.example.mapproject.ui.login.LoginActivity
import com.example.mapproject.ui.login.LoginViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import java.util.*
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    private var _binding: ActivitySplashBinding? = null
    private val viewBinding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        splashViewModel.constant("fa", "asia_tehran", "celsius", "km", "liter")
        splashViewModel.getConstant.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress()
                    response.data.let {
                        val jsonArrayMaps = JSONArray(it.result.maps)
                        sharedPref.edit()
                            .putString(Constants.MAP_LIST, jsonArrayMaps.toString())
                            .apply()
                        val jsonArrayTimezones = JSONArray(it.result.timezones)
                        sharedPref.edit()
                            .putString(Constants.TIMEZONES_LIST, jsonArrayTimezones.toString())
                            .apply()
                        val jsonArrayDistance = JSONArray(it.result.units.distance)
                        sharedPref.edit()
                            .putString(Constants.DISTANCE_LIST, jsonArrayDistance.toString())
                            .apply()
                        val jsonArrayCapacity = JSONArray(it.result.units.capacity)
                        sharedPref.edit()
                            .putString(Constants.CAPACITY_LIST, jsonArrayCapacity.toString())
                            .apply()
                        val jsonArrayTemperature = JSONArray(it.result.units.temperature)
                        sharedPref.edit()
                            .putString(Constants.TEMPERATURE_LIST, jsonArrayTemperature.toString())
                            .apply()
                        Timer().schedule(object : TimerTask() { override fun run() {
                            startActivity(Intent(applicationContext, LoginActivity::class.java))
                        } }, 1000)
                    }
                }
                is Resource.Error -> {
                    hideProgress()
                    response.errorMessage.let {
                    }
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        })


    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private fun showProgress() {
        viewBinding.progressInSplashActivity.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressInSplashActivity.visibility = View.GONE
    }
}