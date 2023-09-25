package com.example.mapproject.ui.changeEmail

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityEmailBinding
import com.example.mapproject.model.changeEmail.EmailRequest
import com.example.mapproject.model.changeEmail.VerifyCodeEmailRequest
import com.example.mapproject.ui.editProfile.EditProfileActivity
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmailActivity : AppCompatActivity() {

    private var _binding: ActivityEmailBinding? = null
    private val viewBinding get() = _binding!!

    private val changeEmailViewModel: ChangeEmailViewModel by viewModels()


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEmailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        userID = sharedPreferences.getString(Constants.KEY_USER_ID,"").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN,"").toString()
        viewBinding.ivBackPressInChangeEmailActivity.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        viewBinding.etFirstConfirmInEmailActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etSecondConfirmInEmailActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etFirstConfirmInEmailActivity.requestFocus()
            }
        }
        viewBinding.etSecondConfirmInEmailActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etThirdConfirmInEmailActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etFirstConfirmInEmailActivity.requestFocus()
            }
        }
        viewBinding.etThirdConfirmInEmailActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etFourthConfirmInEmailActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etSecondConfirmInEmailActivity.requestFocus()
            }
        }
        viewBinding.etFourthConfirmInEmailActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etFifthConfirmInEmailActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etThirdConfirmInEmailActivity.requestFocus()
            }
        }
        viewBinding.etFifthConfirmInEmailActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etSixthConfirmInEmailActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etFourthConfirmInEmailActivity.requestFocus()
            }
        }
        viewBinding.etSixthConfirmInEmailActivity.addTextChangedListener { editable ->
            if (editable.toString() == ""){
                viewBinding.etFifthConfirmInEmailActivity.requestFocus()
            }
        }
        resultEmail()
        resultConfirmEmail()
    }


    private fun resultConfirmEmail(){
        viewBinding.btnConfirmVerifyCodeInEmailActivity.setOnClickListener {
            if (viewBinding.etFirstConfirmInEmailActivity.text.toString().isNotEmpty() &&
                viewBinding.etSecondConfirmInEmailActivity.text.toString().isNotEmpty() &&
                viewBinding.etThirdConfirmInEmailActivity.text.toString().isNotEmpty() &&
                viewBinding.etFourthConfirmInEmailActivity.text.toString().isNotEmpty() &&
                viewBinding.etFifthConfirmInEmailActivity.text.toString().isNotEmpty() &&
                viewBinding.etSixthConfirmInEmailActivity.text.toString().isNotEmpty()){
                val verifyCode = viewBinding.etFirstConfirmInEmailActivity.text.toString() + viewBinding.etSecondConfirmInEmailActivity.text.toString() +
                        viewBinding.etThirdConfirmInEmailActivity.text.toString() + viewBinding.etFourthConfirmInEmailActivity.text.toString() +
                        viewBinding.etFifthConfirmInEmailActivity.text.toString() + viewBinding.etSixthConfirmInEmailActivity.text.toString()
                val verifyCodeEmailRequest = VerifyCodeEmailRequest(
                    verifyCode,
                    email
                )
                changeEmailViewModel.getResultVerifyEmail("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter",verifyCodeEmailRequest)
                showProgress()
                changeEmailViewModel.getResultVerifyEmail.observe(this, Observer { response ->
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
                toast(this, this.getString(R.string.toast_write_your_verify_code))
            }
        }
    }

    private fun resultEmail(){
        viewBinding.btnConfirmPassInEmailActivity.setOnClickListener {
            if (viewBinding.etEmailInActivity.text.toString().isNotEmpty()){
                email = viewBinding.etEmailInActivity.text.toString()
                val emailRequest = EmailRequest(
                    email
                )
                changeEmailViewModel.getResultChangeEmail("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter",emailRequest)
                showProgress()
                changeEmailViewModel.getResultChangeEmail.observe(this, Observer { response ->
                    when(response){
                        is Resource.Success -> {
                            hideProgress()
                            response.data.let {
                                toast(this, it.message)
                                viewBinding.etEmailInActivity.visibility = View.GONE
                                viewBinding.txtEnterNumberInEmailActivity.visibility = View.GONE
                                viewBinding.txtEnterVerifyInEmailActivity.visibility = View.VISIBLE
                                viewBinding.lnVerifyCodeInEmailActivity.visibility = View.VISIBLE
                                viewBinding.btnConfirmPassInEmailActivity.visibility = View.GONE
                                viewBinding.btnConfirmVerifyCodeInEmailActivity.visibility = View.VISIBLE
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
                toast(this, this.getString(R.string.toast_write_your_email))
            }
        }
    }

    private fun showProgress() {
        viewBinding.progressInEmailActivity.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressInEmailActivity.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}