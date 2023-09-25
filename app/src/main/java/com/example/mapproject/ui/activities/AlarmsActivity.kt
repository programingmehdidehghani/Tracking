package com.example.mapproject.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityAlarmsBinding
import com.example.mapproject.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class AlarmsActivity : AppCompatActivity() {

    private var _binding: ActivityAlarmsBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAlarmsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.ivBackPressInEditAlarmsActivity.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}