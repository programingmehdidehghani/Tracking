package com.example.mapproject.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityAddValidityBinding
import com.example.mapproject.databinding.ActivityChangePasswordBinding
import com.example.mapproject.databinding.ActivityContactUsBinding
import com.example.mapproject.databinding.ActivityEditProfileBinding

class ContactUsActivity : AppCompatActivity() {


    private var _binding: ActivityContactUsBinding? = null
    private val viewBinding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityContactUsBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)
        viewBinding.ivBackPressInContactUsActivity.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}