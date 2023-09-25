package com.example.mapproject.ui.phoneNumber

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityPhoneNumberBinding
import com.example.mapproject.model.changePhoneMobile.ConfirmPhoneMobileRequest
import com.example.mapproject.model.changePhoneMobile.PhoneMobileRequest
import com.example.mapproject.ui.editProfile.EditProfileActivity
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhoneNumberActivity : AppCompatActivity() {

    private var _binding: ActivityPhoneNumberBinding? = null
    private val viewBinding get() = _binding!!

    private val changeMobileViewModel: PhoneMobileViewModel by viewModels()


    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var phoneNumber: String


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPhoneNumberBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        userID = sharedPreferences.getString(Constants.KEY_USER_ID,"").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN,"").toString()
        viewBinding.ivBackPressInPhoneNumberActivity.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        viewBinding.etFirstConfirmInPhoneNumberActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etSecondConfirmPhoneNumberActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etFirstConfirmInPhoneNumberActivity.requestFocus()
            }
        }
        viewBinding.etSecondConfirmPhoneNumberActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etThirdConfirmPhoneNumberActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etFirstConfirmInPhoneNumberActivity.requestFocus()
            }
        }
        viewBinding.etThirdConfirmPhoneNumberActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etFourthConfirmPhoneNumberActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etSecondConfirmPhoneNumberActivity.requestFocus()
            }
        }
        viewBinding.etFourthConfirmPhoneNumberActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etFifthConfirmPhoneNumberActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etThirdConfirmPhoneNumberActivity.requestFocus()
            }
        }
        viewBinding.etFifthConfirmPhoneNumberActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etSixthConfirmPhoneNumberActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etFourthConfirmPhoneNumberActivity.requestFocus()
            }
        }
        viewBinding.etSixthConfirmPhoneNumberActivity.addTextChangedListener { editable ->
            if (editable.toString() == ""){
                viewBinding.etFifthConfirmPhoneNumberActivity.requestFocus()
            }
        }
        resultPhoneMobile()
        resultConfirmPhoneMobile()
    }

    private fun resultConfirmPhoneMobile(){
        viewBinding.btnConfirmVerifyCodePhoneNumberActivity.setOnClickListener {
            if (viewBinding.etFirstConfirmInPhoneNumberActivity.text.toString().isNotEmpty() &&
                viewBinding.etSecondConfirmPhoneNumberActivity.text.toString().isNotEmpty() &&
                viewBinding.etThirdConfirmPhoneNumberActivity.text.toString().isNotEmpty() &&
                viewBinding.etFourthConfirmPhoneNumberActivity.text.toString().isNotEmpty() &&
                viewBinding.etFifthConfirmPhoneNumberActivity.text.toString().isNotEmpty() &&
                viewBinding.etSixthConfirmPhoneNumberActivity.text.toString().isNotEmpty()){
                val verifyCode = viewBinding.etFirstConfirmInPhoneNumberActivity.text.toString() + viewBinding.etSecondConfirmPhoneNumberActivity.text.toString() +
                        viewBinding.etThirdConfirmPhoneNumberActivity.text.toString() + viewBinding.etFourthConfirmPhoneNumberActivity.text.toString() +
                        viewBinding.etFifthConfirmPhoneNumberActivity.text.toString() + viewBinding.etSixthConfirmPhoneNumberActivity.text.toString()
                val confirmPhoneMobileRequest = ConfirmPhoneMobileRequest(
                    verifyCode,
                    phoneNumber
                )
                changeMobileViewModel.getResultConfirmPhoneMobile("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter",confirmPhoneMobileRequest)
                showProgress()
                changeMobileViewModel.getResultConfirmPhoneNumber.observe(this, Observer { response ->
                    when(response){
                        is Resource.Success -> {
                            hideProgress()
                            response.data.let {
                                toast(this, it.message)
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

            } else {
                toast(this, this.getString(R.string.toast_write_your_verify_code_phone_number))
            }
        }
    }

    private fun resultPhoneMobile(){
        viewBinding.btnConfirmPassPhoneNumberActivity.setOnClickListener {
            if (viewBinding.etPhoneMobileInPhoneNumberActivity.text.toString().isNotEmpty()){

                phoneNumber = viewBinding.etPhoneMobileInPhoneNumberActivity.text.toString()
                val phoneMobileRequest = PhoneMobileRequest(
                    phoneNumber
                )
                changeMobileViewModel.getResultPhoneMobile("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter",phoneMobileRequest)
                showProgress()
                changeMobileViewModel.getResultPhoneMobile.observe(this, Observer { response ->
                    when(response){
                        is Resource.Success -> {
                            hideProgress()
                            response.data.let {
                                toast(this, it.message)
                                viewBinding.etPhoneMobileInPhoneNumberActivity.visibility = View.GONE
                                viewBinding.txtEnterNumberInPhoneNumberActivity.visibility = View.GONE
                                viewBinding.lnVerifyCodeInPhoneNumberActivity.visibility = View.VISIBLE
                                viewBinding.txtEnterVerifyInPhoneNumberActivity.visibility = View.VISIBLE
                                viewBinding.btnConfirmPassPhoneNumberActivity.visibility = View.GONE
                                viewBinding.btnConfirmVerifyCodePhoneNumberActivity.visibility = View.VISIBLE
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
                toast(this, this.getString(R.string.toast_write_your_email_phone_number))
            }
        }
    }



    private fun showProgress() {
        viewBinding.progressPhoneNumberActivity.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressPhoneNumberActivity.visibility = View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}