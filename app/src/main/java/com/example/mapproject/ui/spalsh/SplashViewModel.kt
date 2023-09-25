package com.example.mapproject.ui.spalsh

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.model.MessageResponse
import com.example.mapproject.model.constant.Constant
import com.example.mapproject.model.forgetPass.RequestForgetPass
import com.example.mapproject.model.login.LoginModel
import com.example.mapproject.model.login.LoginRequest
import com.example.mapproject.repository.ForgetPassRepository
import com.example.mapproject.repository.LoginRepository
import com.example.mapproject.util.Resource
import com.example.mapproject.util.hasInternetConnection
import com.example.mapproject.util.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    application: Application,
    private val loginRepository: LoginRepository,
): AndroidViewModel(application){

    private val _getConstant = MutableLiveData<Resource<Constant>>()
    val getConstant: LiveData<Resource<Constant>> = _getConstant


    fun constant(language: String, tz: String, tu: String,du: String , cu: String) = viewModelScope.launch {
        getConstant(language, tz, tu,du,cu)
    }


    private suspend fun getConstant(language: String, tz: String, tu: String, du: String, cu: String){
        _getConstant.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = loginRepository.getConstant(language, tz, tu ,du, cu)
                if (response.isSuccessful) {
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

}
