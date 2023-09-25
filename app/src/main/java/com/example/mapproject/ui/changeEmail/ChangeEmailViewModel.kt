package com.example.mapproject.ui.changeEmail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.model.changeEmail.EmailRequest
import com.example.mapproject.model.MessageResponse
import com.example.mapproject.model.changeEmail.VerifyCodeEmailRequest
import com.example.mapproject.repository.ChangeEmailRepository
import com.example.mapproject.util.Resource
import com.example.mapproject.util.hasInternetConnection
import com.example.mapproject.util.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    application: Application,
    private val repository: ChangeEmailRepository
) : AndroidViewModel(application){

    private val _getResultChangeEmail = MutableLiveData<Resource<MessageResponse>>()
    val getResultChangeEmail : LiveData<Resource<MessageResponse>> = _getResultChangeEmail

    private val _getResultVerifyEmail = MutableLiveData<Resource<MessageResponse>>()
    val getResultVerifyEmail: LiveData<Resource<MessageResponse>> = _getResultVerifyEmail

    fun getResultChangeEmail(token: String, id: String, language: String, tz: String, tu: String, du: String, cu: String, emailRequest: EmailRequest) = viewModelScope.launch {
        resultChangeEmail(token,id,language, tz, tu ,du,cu,emailRequest)
    }

    fun getResultVerifyEmail(token: String, id: String, language: String, tz: String, tu: String, du: String, cu: String, verifyCodeEmailRequest: VerifyCodeEmailRequest) = viewModelScope.launch {
        resultVerifyEmail(token,id,language, tz, tu ,du,cu,verifyCodeEmailRequest)
    }

    private suspend fun resultVerifyEmail(token: String, id: String, language: String, tz: String, tu: String, du: String, cu: String, verifyCodeEmailRequest: VerifyCodeEmailRequest){
        _getResultVerifyEmail.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.verifyEmail(token,id,language, tz, tu ,du, cu,verifyCodeEmailRequest)
                if (response.isSuccessful) {
                    Log.i("verifyEmail","error is  ${response.body()!!.message}")
                    _getResultVerifyEmail.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    _getResultVerifyEmail.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getResultVerifyEmail.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (e: HttpException) {
            Log.i("verifyEmail","error is exception ${e.message()}")
            //toast(getApplication(), "Exception ${e.message}")
            _getResultVerifyEmail.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    Log.i("verifyEmail","error is exception  ${t.message}")
                    _getResultVerifyEmail.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

    private suspend fun resultChangeEmail(token: String, id: String, language: String, tz: String, tu: String, du: String, cu: String, emailRequest: EmailRequest){
        _getResultChangeEmail.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.changeEmail(token,id,language, tz, tu ,du, cu,emailRequest)
                if (response.isSuccessful) {
                    Log.i("AddEmail","error is  ${response.body()!!.message}")
                    _getResultChangeEmail.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    _getResultChangeEmail.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getResultChangeEmail.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (e: HttpException) {
            Log.i("AddEmail","error is exception ${e.message()}")
            //toast(getApplication(), "Exception ${e.message}")
            _getResultChangeEmail.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    Log.i("AddEmail","error is exception  ${t.message}")
                    _getResultChangeEmail.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }
}