package com.example.mapproject.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityAddValidityBinding
import com.example.mapproject.databinding.ActivityChangePasswordBinding
import com.example.mapproject.databinding.ActivityEditProfileBinding

class AddValidityActivity : AppCompatActivity() {


    private var _binding: ActivityAddValidityBinding? = null
    private val viewBinding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddValidityBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        viewBinding.ivBackPressInAddValidityActivity.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}