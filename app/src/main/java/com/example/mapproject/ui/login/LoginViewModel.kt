package com.example.mapproject.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.R
import com.example.mapproject.model.MessageResponse
import com.example.mapproject.model.constant.Constant
import com.example.mapproject.model.forgetPass.RequestForgetPass
import com.example.mapproject.model.login.LoginModel
import com.example.mapproject.model.login.LoginRequest
import com.example.mapproject.model.verifyForgetPass.VerifyForgetPass
import com.example.mapproject.repository.ForgetPassRepository
import com.example.mapproject.repository.LoginRepository
import com.example.mapproject.util.Resource
import com.example.mapproject.util.hasInternetConnection
import com.example.mapproject.util.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val loginRepository: LoginRepository,
    private val forgetPassRepository: ForgetPassRepository
): AndroidViewModel(application){

    private val _getConstant = MutableLiveData<Resource<Constant>>()
    val getConstant: LiveData<Resource<Constant>> = _getConstant

    private val _requestLogin = MutableLiveData<Resource<LoginModel>>()
    val requestLogin: LiveData<Resource<LoginModel>> = _requestLogin

    private val _requestForgetPass = MutableLiveData<Resource<MessageResponse>>()
    val requestForgetPass: LiveData<Resource<MessageResponse>> = _requestForgetPass

    private val _requestVerifyForgetPass = MutableLiveData<Resource<MessageResponse>>()
    val requestVerifyForgetPass: LiveData<Resource<MessageResponse>> = _requestVerifyForgetPass

    fun login(language: String, tz: String, tu: String,du: String , cu: String ,loginRequest: LoginRequest) = viewModelScope.launch(Dispatchers.IO) {
        getLoginResult(language, tz, tu,du , cu,loginRequest)
    }

    fun constant(language: String, tz: String, tu: String,du: String , cu: String) = viewModelScope.launch {
        getConstant(language, tz, tu,du,cu)
    }

    fun requestForgetPass(language: String, tz: String, tu: String ,du: String, cu: String,forgetPass: RequestForgetPass) = viewModelScope.launch {
        resultRequestForgetPass(language, tz, tu ,du, cu,forgetPass)
    }

    fun verifyForgetPass(language: String, tz: String, tu: String ,du: String, cu: String,verifyForgetPass: VerifyForgetPass) = viewModelScope.launch {
        resultVerifyForgetPass(language, tz, tu ,du, cu,verifyForgetPass)
    }

    private suspend fun resultVerifyForgetPass(language: String, tz: String, tu: String ,du: String, cu: String,verifyForgetPass: VerifyForgetPass){
        _requestVerifyForgetPass.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = forgetPassRepository.requestVerifyForgetPass(language, tz, tu ,du, cu,verifyForgetPass)
                if (response.isSuccessful) {
                    toast(getApplication(), response.body()!!.message)
                    Log.i("Constant","error is  ${response.body()!!}")
                    _requestVerifyForgetPass.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    _requestVerifyForgetPass.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _requestVerifyForgetPass.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), R.string.no_internet.toString())
            }
        } catch (e: HttpException) {
            Log.i("Constant","error is exception ${e.message()}")
            //toast(getApplication(), "Exception ${e.message}")
            _requestVerifyForgetPass.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    Log.i("Constant","error is eception  ${t.message}")
                    _requestVerifyForgetPass.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

    private suspend fun resultRequestForgetPass(language: String, tz: String, tu: String ,du: String, cu: String,forgetPass: RequestForgetPass){
        _requestForgetPass.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = forgetPassRepository.requestForgetPass(language, tz, tu ,du, cu,forgetPass)
                if (response.isSuccessful) {
                    toast(getApplication(), response.body()!!.message)
                    _requestForgetPass.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    _requestForgetPass.postValue(Resource.Error(response.message()))
                }
            } else {
                _requestForgetPass.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (e: HttpException) {
            Log.i("Constant","error is exception ${e.message()}")
            toast(getApplication(), "Exception ${e.message}")
            _requestForgetPass.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    toast(getApplication(), "Exception ${t.message}")
                    Log.i("Constant","error is eception  ${t.message}")
                    _requestForgetPass.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

    private suspend fun getConstant(language: String, tz: String, tu: String, du: String, cu: String){
        _getConstant.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = loginRepository.getConstant(language, tz, tu ,du, cu)
                if (response.isSuccessful) {
                    toast(getApplication(), response.body()!!.result.roles.toString())
                    Log.i("Constant","error is  ${response.body()!!.result.roles}")
                    _getConstant.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    _getConstant.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getConstant.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (e: HttpException) {
            Log.i("Constant","error is exception ${e.message()}")
            //toast(getApplication(), "Exception ${e.message}")
            _getConstant.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    Log.i("Constant","error is eception  ${t.message}")
                    _getConstant.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

    private suspend fun getLoginResult(language: String, tz: String, tu: String,du: String , cu: String ,loginRequest: LoginRequest){
        _requestLogin.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = loginRepository.loginRequest(language, tz, tu ,du, cu,loginRequest)
                if (response.isSuccessful) {
                   // toast(getApplication(), response.body()!!.result.token)
                    _requestLogin.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main){
                        toast(getApplication(), errorOriginal)
                    }
                    _requestLogin.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _requestLogin.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }
            }
        } catch (e: HttpException) {
             Log.i("Login","error is eception ${e.message()}")
             //toast(getApplication(), "Exception ${e.message}")
            _requestLogin.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                  //  toast(getApplication(), "Exception ${t.message}")
                    Log.i("Login","error is eception  ${t.message}")
                    _requestLogin.postValue(Resource.Error(t.message!!))
                }
            }
        }

    }
}