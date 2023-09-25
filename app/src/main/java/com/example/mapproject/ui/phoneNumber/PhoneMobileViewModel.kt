package com.example.mapproject.ui.phoneNumber

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.model.*
import com.example.mapproject.model.changePhoneMobile.ConfirmPhoneMobileRequest
import com.example.mapproject.model.changePhoneMobile.PhoneMobileRequest
import com.example.mapproject.repository.PhoneMobileRepository
import com.example.mapproject.util.Resource
import com.example.mapproject.util.hasInternetConnection
import com.example.mapproject.util.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PhoneMobileViewModel @Inject constructor(
    application: Application,
    private val repository: PhoneMobileRepository
) : AndroidViewModel(application){

    private val _getResultPhoneMobile = MutableLiveData<Resource<MessageResponse>>()
    val getResultPhoneMobile : LiveData<Resource<MessageResponse>> = _getResultPhoneMobile

    private val _getResultConfirmPhoneNumber = MutableLiveData<Resource<MessageResponse>>()
    val getResultConfirmPhoneNumber : LiveData<Resource<MessageResponse>> = _getResultConfirmPhoneNumber

    fun getResultPhoneMobile(token:String,id: String,language: String, tz: String, tu: String,du: String , cu: String ,phoneMobileRequest: PhoneMobileRequest) = viewModelScope.launch {
        resultPhoneMobile(token,id,language, tz, tu,du , cu,phoneMobileRequest)
    }

    fun getResultConfirmPhoneMobile(token:String, id: String, language: String, tz: String, tu: String, du: String, cu: String, confirmPhoneMobileRequest: ConfirmPhoneMobileRequest)
    = viewModelScope.launch {
        resultConfirmPhoneNumber(token,id,language, tz, tu,du , cu,confirmPhoneMobileRequest)
    }

    private suspend fun resultConfirmPhoneNumber(token:String, id: String, language: String, tz: String, tu: String, du: String, cu: String, confirmPhoneMobileRequest: ConfirmPhoneMobileRequest){
        _getResultConfirmPhoneNumber.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.confirmPhoneMobile(token,id,language, tz, tu ,du, cu,confirmPhoneMobileRequest)
                if (response.isSuccessful) {
                    Log.i("PhoneMobile","response is  ${response.body()!!.message}")
                    _getResultConfirmPhoneNumber.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    _getResultConfirmPhoneNumber.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getResultConfirmPhoneNumber.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (e: HttpException) {
            Log.i("PhoneMobile","error is eception ${e.message()}")
            _getResultConfirmPhoneNumber.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    Log.i("PhoneMobile","error is exception  ${t.message}")
                    _getResultConfirmPhoneNumber.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

    private suspend fun resultPhoneMobile(token:String, id: String, language: String, tz: String, tu: String, du: String, cu: String, phoneMobileRequest: PhoneMobileRequest){
        _getResultPhoneMobile.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.phoneMobileRequest(token,id,language, tz, tu ,du, cu,phoneMobileRequest)
                if (response.isSuccessful) {
                    Log.i("PhoneMobile","error is  ${response.body()!!.message}")
                    _getResultPhoneMobile.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    _getResultPhoneMobile.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getResultPhoneMobile.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (e: HttpException) {
            Log.i("PhoneMobile","error is eception ${e.message()}")
            //toast(getApplication(), "Exception ${e.message}")
            _getResultPhoneMobile.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    Log.i("PhoneMobile","error is eception  ${t.message}")
                    _getResultPhoneMobile.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }
}