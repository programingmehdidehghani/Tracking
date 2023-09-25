package com.example.mapproject.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityMain2Binding
import com.example.mapproject.databinding.ActivityMainBinding
import com.example.mapproject.databinding.ActivityMonitorDetailBinding

class MonitorDetailActivity : AppCompatActivity() {


    private var _binding: ActivityMonitorDetailBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMonitorDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}