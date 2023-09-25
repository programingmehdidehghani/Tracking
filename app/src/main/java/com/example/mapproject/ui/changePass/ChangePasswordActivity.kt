package com.example.mapproject.ui.changePass

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityChangePasswordBinding
import com.example.mapproject.databinding.ActivityLoginBinding
import com.example.mapproject.model.changePassWord.ChangePasswordRequest
import com.example.mapproject.ui.activities.MainActivity
import com.example.mapproject.ui.editProfile.EditProfileActivity
import com.example.mapproject.ui.login.LoginActivity
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {


    private val changePassViewModel: ChangePassViewModel by viewModels()

    @Inject
    lateinit var sharedPref : SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String

    private var _binding: ActivityChangePasswordBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.teal_700)

        viewBinding.btnSavePassInChangePassActivity.setOnClickListener {
            if (viewBinding.etCurrentInChangePassActivity.text.toString().isNotEmpty()
                && viewBinding.etNewPassInChangePassActivity.text.toString().isNotEmpty() &&
                viewBinding.etConfirmPassInChangePassActivity.text.toString().isNotEmpty()){
                userID = sharedPref.getString(Constants.KEY_USER_ID,"").toString()
                token = sharedPref.getString(Constants.KEY_TOKEN,"").toString()
                val changePasswordRequest = ChangePasswordRequest(
                    viewBinding.etCurrentInChangePassActivity.text.toString(),
                    viewBinding.etNewPassInChangePassActivity.text.toString(),
                    viewBinding.etConfirmPassInChangePassActivity.text.toString()
                )
                changePassViewModel.getResultChange("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter",changePasswordRequest)
                showProgress()
                changePassViewModel.getResultChangePass.observe(this, Observer { response ->
                    when(response){
                        is Resource.Success -> {
                            hideProgress()
                            response.data.let {
                                toast(this, it.message)
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                this.finish()
                            }
                        }
                        is Resource.Error -> {
                            hideProgress()
                        }
                        is Resource.Loading -> {
                            showProgress()
                        }
                    }

                })
            }  else {
                toast(this, "بر کردن همه موارد اجباریست")
            }
        }
        viewBinding.ivBackPressInChangePassActivity.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun showProgress() {
        viewBinding.progressInChangePassActivity.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressInChangePassActivity.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}