package com.example.mapproject.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityLoginBinding
import com.example.mapproject.model.forgetPass.RequestForgetPass
import com.example.mapproject.model.login.LoginRequest
import com.example.mapproject.model.verifyForgetPass.VerifyForgetPass
import com.example.mapproject.ui.activities.MainActivity
import com.example.mapproject.ui.activities.MainActivity2
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Constants.Companion.KEY_TOKEN
import com.example.mapproject.util.Constants.Companion.KEY_TOKEN_ID
import com.example.mapproject.util.Constants.Companion.KEY_USER_ID
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import com.google.android.gms.tasks.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    private var fcmToken: String = ""

    private var _binding: ActivityLoginBinding? = null
    private val viewBinding get() = _binding!!
    private lateinit var userName: String
    private lateinit var language: String
    private var isChangeLanguage: Boolean = false
    private var isLoadingRequest: Boolean = false
    private var isVerifyCode: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private var isActiveDownTimer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isChangeLanguage = sharedPref.getBoolean(Constants.IS_CHANGE_LANGUAGE, false)
        if (!isChangeLanguage){
            language = sharedPref.getString(Constants.DEFAULT_LANGUAGE, "").toString()
            val localeFirst = Locale(language)
            Locale.setDefault(localeFirst)
            val configFirst = Configuration()
            configFirst.setLocale(localeFirst)
            this.resources.updateConfiguration(
                configFirst,
                this.resources.displayMetrics
            )
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            sharedPref.edit()
                .putBoolean(Constants.IS_CHANGE_LANGUAGE, false)
                .apply()
        }
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.teal_700)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@LoginActivity,
            OnSuccessListener<InstanceIdResult> { instanceIdResult ->
                fcmToken = instanceIdResult.token
                Log.i("newToken", fcmToken)
            })
        viewBinding.txtEnLanguageInLoginActivity.setOnClickListener {
            val localeEn = Locale("en")
            Locale.setDefault(localeEn)
            val configEn = Configuration()
            configEn.setLocale(localeEn)
            this.resources.updateConfiguration(
                configEn,
                this.resources.displayMetrics
            )
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
            sharedPref.edit()
                .putBoolean(Constants.IS_CHANGE_LANGUAGE, true)
                .apply()
            recreate()
        }
        viewBinding.txtPersianInLoginActivity.setOnClickListener {
            val localeFa = Locale("fa")
            Locale.setDefault(localeFa)
            val configFa = Configuration()
            configFa.locale = localeFa
            baseContext.resources.updateConfiguration(
                configFa,
                baseContext.resources.displayMetrics
            )
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
            recreate()
        }
        statusFirstView()
        requestLogin()
        forgetPass()

    }



    private fun statusFirstView() {
        viewBinding.btnVerifyCodePassInLoginActivity.visibility = View.GONE
        viewBinding.lnVerifyCodeInLoginActivity.visibility = View.GONE
        viewBinding.textResendAgainVerifyInLoginActivity.visibility = View.GONE
        viewBinding.viewInLoginActivity.visibility = View.GONE
        viewBinding.textChangeUserNameInLoginActivity.visibility = View.GONE
        /*viewBinding.etNewPassInLoginActivity.visibility = View.GONE
        viewBinding.etConfirmPassInLoginActivity.visibility = View.GONE*/
        viewBinding.textEnterUserVerifyInLoginActivity.visibility = View.GONE
        viewBinding.btnVerifyCodePassInLoginActivity.visibility = View.GONE
        viewBinding.txtTitleForgetPassInLoginActivity.visibility = View.GONE
        viewBinding.txtInputNewPassInLoginActivity.visibility = View.GONE
        viewBinding.txtInputConfirmPassInLoginActivity.visibility = View.GONE

        viewBinding.etFirstConfirmInLoginActivity.setText("")
        viewBinding.etSecondConfirmInLoginActivity.setText("")
        viewBinding.etThirdConfirmInLoginActivity.setText("")
        viewBinding.etFourthConfirmInLoginActivity.setText("")
        viewBinding.etFifthConfirmInLoginActivity.setText("")
        viewBinding.etSixthConfirmInLoginActivity.setText("")
        viewBinding.etNewPassInLoginActivity.setText("")
        viewBinding.etConfirmPassInLoginActivity.setText("")


        viewBinding.txtInputPassInLoginActivity.visibility = View.VISIBLE
        viewBinding.etUserInLoginActivity.visibility = View.VISIBLE
        viewBinding.txtForgetPassInLoginActivity.visibility = View.VISIBLE
        viewBinding.txtNameUserInLoginActivity.visibility = View.VISIBLE
        viewBinding.txtPassInLoginActivity.visibility = View.VISIBLE
        viewBinding.btnEnterInLoginActivity.visibility = View.VISIBLE
        viewBinding.etForgetPassInLoginActivity.setText("")
    }

    private fun handleListenerVerifyForgetPass() {
        viewBinding.etFirstConfirmInLoginActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etSecondConfirmInLoginActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etFirstConfirmInLoginActivity.requestFocus()
            }
        }
        viewBinding.etSecondConfirmInLoginActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etThirdConfirmInLoginActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etFirstConfirmInLoginActivity.requestFocus()
            }
        }
        viewBinding.etThirdConfirmInLoginActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etFourthConfirmInLoginActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etSecondConfirmInLoginActivity.requestFocus()
            }
        }
        viewBinding.etFourthConfirmInLoginActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etFifthConfirmInLoginActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etThirdConfirmInLoginActivity.requestFocus()
            }
        }
        viewBinding.etFifthConfirmInLoginActivity.addTextChangedListener { editable ->
            editable.let {
                viewBinding.etSixthConfirmInLoginActivity.requestFocus()
            }
            if (editable.toString() == ""){
                viewBinding.etFourthConfirmInLoginActivity.requestFocus()
            }
        }
        viewBinding.etSixthConfirmInLoginActivity.addTextChangedListener { editable ->
            if (editable.toString() == ""){
                viewBinding.etFifthConfirmInLoginActivity.requestFocus()
            }
        }
    }

    private fun verifyForgetPass() {
        viewBinding.btnVerifyCodePassInLoginActivity.setOnClickListener {
            if (isLoadingRequest) return@setOnClickListener
            userName = sharedPref.getString(Constants.USER_NAME_FOR_FORGET_PASS, "").toString()
            val verifyCode = viewBinding.etFirstConfirmInLoginActivity.text.toString() + viewBinding.etSecondConfirmInLoginActivity.text.toString() +
                        viewBinding.etThirdConfirmInLoginActivity.text.toString() + viewBinding.etFourthConfirmInLoginActivity.text.toString() +
                        viewBinding.etFifthConfirmInLoginActivity.text.toString() + viewBinding.etSixthConfirmInLoginActivity.text.toString()
                val verifyForgetPass = VerifyForgetPass(
                    verifyCode,
                    viewBinding.etNewPassInLoginActivity.text.toString(),
                    viewBinding.etConfirmPassInLoginActivity.text.toString(),
                    userName
                )
                loginViewModel.verifyForgetPass("fa", "asia_tehran", "celsius", "km", "liter",verifyForgetPass)
                loginViewModel.requestVerifyForgetPass.observe(this, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            hideProgress()
                            isLoadingRequest = true
                            statusFirstView()
                        }
                        is Resource.Error -> {
                            hideProgress()
                            isLoadingRequest = false
                        }
                        is Resource.Loading -> {
                            showProgress()
                            isLoadingRequest = true
                        }
                    }
                })
        }
    }


    private fun stateVerifyCode(){
        viewBinding.txtInputPassInLoginActivity.visibility = View.GONE
        viewBinding.etUserInLoginActivity.visibility = View.GONE
        viewBinding.txtForgetPassInLoginActivity.visibility = View.GONE
        viewBinding.txtNameUserInLoginActivity.visibility = View.GONE
        viewBinding.txtPassInLoginActivity.visibility = View.GONE
        viewBinding.btnEnterInLoginActivity.visibility = View.GONE
        viewBinding.btnForgetPassInLoginActivity.visibility = View.GONE
        viewBinding.txtEnLanguageInLoginActivity.visibility = View.GONE
        viewBinding.txtPersianInLoginActivity.visibility = View.GONE

        viewBinding.txtTitleForgetPassInLoginActivity.visibility = View.VISIBLE
        viewBinding.etForgetPassInLoginActivity.visibility = View.GONE
        viewBinding.txtEnterUserForgetPassInLoginActivity.visibility = View.GONE
        viewBinding.btnVerifyCodePassInLoginActivity.visibility = View.VISIBLE
        viewBinding.lnVerifyCodeInLoginActivity.visibility = View.VISIBLE
        viewBinding.textResendAgainVerifyInLoginActivity.visibility = View.VISIBLE
        viewBinding.viewInLoginActivity.visibility = View.VISIBLE
        viewBinding.textChangeUserNameInLoginActivity.visibility = View.VISIBLE
        /*viewBinding.etNewPassInLoginActivity.visibility = View.VISIBLE
        viewBinding.etConfirmPassInLoginActivity.visibility = View.VISIBLE*/
        viewBinding.txtInputNewPassInLoginActivity.visibility = View.VISIBLE
        viewBinding.txtInputConfirmPassInLoginActivity.visibility = View.VISIBLE
        viewBinding.textEnterUserVerifyInLoginActivity.visibility = View.VISIBLE
        viewBinding.btnVerifyCodePassInLoginActivity.visibility = View.VISIBLE
        viewBinding.txtSendVerifyCodeInLoginActivity.visibility = View.VISIBLE
        viewBinding.etForgetPassInLoginActivity.setText("")
        userName = sharedPref.getString(Constants.USER_NAME_FOR_FORGET_PASS, "").toString()
        val text: String = resources.getString(R.string.txt_verify_code_user,userName)
        viewBinding.txtSendVerifyCodeInLoginActivity.text = text
        setCountDownTimer()
        clickOnchangeUserNameAndClickOnEnterUserName()
        handleListenerVerifyForgetPass()
        verifyForgetPass()
    }

    private fun setCountDownTimer() {
        var time = 60
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isVerifyCode){
                    val text: String = resources.getString(R.string.txt_recode_again_verify_code,"0:" + checkDigit(time) )
                    viewBinding.textResendAgainVerifyInLoginActivity.text = text
                    time--
                    viewBinding.textResendAgainVerifyInLoginActivity.isClickable = false
                    Log.i("timer","0:$text")
                    viewBinding.textResendAgainVerifyInLoginActivity.setTextColor(resources.getColor(R.color.whiteLow))
                    isActiveDownTimer = true
                } else {
                    isActiveDownTimer = false
                    viewBinding.textResendAgainVerifyInLoginActivity.setText(R.string.txt_resend_code_again)
                    viewBinding.textResendAgainVerifyInLoginActivity.setTextColor(resources.getColor(R.color.RedLow))
                    viewBinding.textResendAgainVerifyInLoginActivity.isClickable = true
                    viewBinding.textResendAgainVerifyInLoginActivity.setOnClickListener {
                        requestGetVerifyCode()
                    }
                }
                if (viewBinding.textResendAgainVerifyInLoginActivity.visibility == View.GONE){
                    isActiveDownTimer = false
                    cancel()
                }
            }

            override fun onFinish() {
                viewBinding.textResendAgainVerifyInLoginActivity.setText(R.string.txt_resend_code_again)
                viewBinding.textResendAgainVerifyInLoginActivity.setTextColor(resources.getColor(R.color.RedLow))
                viewBinding.textResendAgainVerifyInLoginActivity.isClickable = true
                viewBinding.textResendAgainVerifyInLoginActivity.setOnClickListener {
                    requestGetVerifyCode()
                }
            }
        }.start()
    }


    private fun requestGetVerifyCode(){
        if (isLoadingRequest){
            return
        }
        Log.i("click","click is .... on")
        userName = sharedPref.getString(Constants.USER_NAME_FOR_FORGET_PASS, "").toString()
        val requestForgetPass = RequestForgetPass(
            userName
        )
        viewBinding.btnForgetPassInLoginActivity.isCheckable = false
        loginViewModel.requestForgetPass("fa", "asia_tehran", "celsius", "km", "liter",requestForgetPass)
        loginViewModel.requestForgetPass.observe(this, Observer { response ->
            isLoadingRequest = when (response) {
                is Resource.Success -> {
                    hideProgress()
                    stateVerifyCode()
                    isVerifyCode = true
                    false
                }
                is Resource.Error -> {
                    hideProgress()
                    isVerifyCode = false
                    false
                }
                is Resource.Loading -> {
                    showProgress()
                    isVerifyCode = true
                    true
                }
            }
        })
    }

    private fun clickOnchangeUserNameAndClickOnEnterUserName(){
        viewBinding.textChangeUserNameInLoginActivity.setOnClickListener {
            viewBinding.lnVerifyCodeInLoginActivity.visibility = View.GONE
            viewBinding.btnVerifyCodePassInLoginActivity.visibility = View.GONE
            viewBinding.textResendAgainVerifyInLoginActivity.visibility = View.GONE
            viewBinding.viewInLoginActivity.visibility = View.GONE
            viewBinding.textChangeUserNameInLoginActivity.visibility = View.GONE
            viewBinding.txtInputNewPassInLoginActivity.visibility = View.GONE
            viewBinding.txtInputConfirmPassInLoginActivity.visibility = View.GONE
            viewBinding.textEnterUserVerifyInLoginActivity.visibility = View.GONE
            viewBinding.btnVerifyCodePassInLoginActivity.visibility = View.GONE
            viewBinding.txtPersianInLoginActivity.visibility = View.GONE
            viewBinding.txtEnLanguageInLoginActivity.visibility = View.GONE
            viewBinding.txtSendVerifyCodeInLoginActivity.visibility = View.GONE

            viewBinding.btnForgetPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtTitleForgetPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.etForgetPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtEnterUserForgetPassInLoginActivity.visibility = View.VISIBLE

        }
        viewBinding.textEnterUserVerifyInLoginActivity.setOnClickListener {
            viewBinding.lnVerifyCodeInLoginActivity.visibility = View.GONE
            viewBinding.btnVerifyCodePassInLoginActivity.visibility = View.GONE
            viewBinding.textResendAgainVerifyInLoginActivity.visibility = View.GONE
            viewBinding.viewInLoginActivity.visibility = View.GONE
            viewBinding.textChangeUserNameInLoginActivity.visibility = View.GONE
            /*viewBinding.etNewPassInLoginActivity.visibility = View.GONE
            viewBinding.etConfirmPassInLoginActivity.visibility = View.GONE*/
            viewBinding.txtInputNewPassInLoginActivity.visibility = View.GONE
            viewBinding.txtInputConfirmPassInLoginActivity.visibility = View.GONE
            viewBinding.textEnterUserVerifyInLoginActivity.visibility = View.GONE
            viewBinding.btnVerifyCodePassInLoginActivity.visibility = View.GONE
            viewBinding.txtTitleForgetPassInLoginActivity.visibility = View.GONE
            viewBinding.txtSendVerifyCodeInLoginActivity.visibility = View.GONE

            viewBinding.txtInputPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.etUserInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtForgetPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtNameUserInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.btnEnterInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtPersianInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtEnLanguageInLoginActivity.visibility = View.VISIBLE
            viewBinding.etForgetPassInLoginActivity.setText("")
        }
    }

    fun checkDigit(number: Int): String? {
        return if (number <= 9) "0$number" else number.toString()
    }

    private fun forgetPass() {
        viewBinding.txtForgetPassInLoginActivity.setOnClickListener {
            viewBinding.txtInputPassInLoginActivity.visibility = View.GONE
            viewBinding.etUserInLoginActivity.visibility = View.GONE
            viewBinding.txtForgetPassInLoginActivity.visibility = View.GONE
            viewBinding.txtNameUserInLoginActivity.visibility = View.GONE
            viewBinding.txtPassInLoginActivity.visibility = View.GONE
            viewBinding.btnEnterInLoginActivity.visibility = View.GONE
            viewBinding.txtPersianInLoginActivity.visibility = View.GONE
            viewBinding.txtEnLanguageInLoginActivity.visibility = View.GONE
            viewBinding.btnForgetPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtTitleForgetPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.etForgetPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtEnterUserForgetPassInLoginActivity.visibility = View.VISIBLE
        }
        viewBinding.txtEnterUserForgetPassInLoginActivity.setOnClickListener {
            viewBinding.txtInputPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.etUserInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtForgetPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtNameUserInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtPassInLoginActivity.visibility = View.VISIBLE
            viewBinding.btnEnterInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtPersianInLoginActivity.visibility = View.VISIBLE
            viewBinding.txtEnLanguageInLoginActivity.visibility = View.VISIBLE
            viewBinding.btnForgetPassInLoginActivity.visibility = View.GONE
            viewBinding.txtTitleForgetPassInLoginActivity.visibility = View.GONE
            viewBinding.etForgetPassInLoginActivity.visibility = View.GONE
            viewBinding.txtEnterUserForgetPassInLoginActivity.visibility = View.GONE
            viewBinding.etForgetPassInLoginActivity.setText("")
        }
        viewBinding.btnForgetPassInLoginActivity.setOnClickListener {
            if (viewBinding.etForgetPassInLoginActivity.text.toString().isNotEmpty()){
                sharedPref.edit()
                    .putString(Constants.USER_NAME_FOR_FORGET_PASS, viewBinding.etForgetPassInLoginActivity.text.toString())
                    .apply()
                stateVerifyCode()
                //requestGetVerifyCode()
            } else {
                toast(this,this.getString(R.string.txt_forget_pass_user_name_is_empty))
            }
        }
    }


    private fun requestLogin() {
        viewBinding.btnEnterInLoginActivity.setOnClickListener {
            if (viewBinding.etUserInLoginActivity.text.toString().isNotEmpty()
                && viewBinding.etPassInLoginActivity.text.toString().isNotEmpty()
            ) {
                if (isLoadingRequest){
                    return@setOnClickListener
                }
                Log.i("click","click is login")
                val loginRequest = LoginRequest(
                    viewBinding.etUserInLoginActivity.text.toString(),
                    viewBinding.etPassInLoginActivity.text.toString(),
                    "android",
                    fcmToken
                )
                loginViewModel.login("fa", "asia_tehran", "celsius", "km", "liter", loginRequest)
                loginViewModel.requestLogin.observe(this, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            hideProgress()
                            isLoadingRequest = true
                            response.data.let {
                                Log.i("take","token , ${it.result.token} fcm token is $fcmToken")
                                sharedPref.edit()
                                    .putString(KEY_USER_ID, it.result.user.id.toString())
                                    .apply()
                                val tokenArray = it.result.token.split("|").toTypedArray()
                                sharedPref.edit()
                                    .putString(KEY_TOKEN, tokenArray[1])
                                    .apply()
                                sharedPref.edit()
                                    .putString(KEY_TOKEN_ID, tokenArray[0])
                                    .apply()
                                val intent = Intent(this, MainActivity2::class.java)
                                startActivity(intent)
                                this.finish()
                            }
                        }
                        is Resource.Error -> {
                            hideProgress()
                            isLoadingRequest = false
                        }
                        is Resource.Loading -> {
                            showProgress()
                            isLoadingRequest = true
                        }
                    }
                })
            } else {
                toast(this, this.getString(R.string.txt_user_name_pass_word_is_empty))
            }
        }
    }


    private fun showProgress() {
        viewBinding.progressInLoginActivity.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressInLoginActivity.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        setCountDownTimer()
    }

    override fun onStop() {
        super.onStop()
        if (isActiveDownTimer){
            isVerifyCode = false
            countDownTimer?.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}