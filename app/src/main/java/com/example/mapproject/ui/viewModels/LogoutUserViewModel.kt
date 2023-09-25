package com.example.mapproject.ui.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.model.MessageResponse
import com.example.mapproject.model.constant.Constant
import com.example.mapproject.repository.LogoutRepository
import com.example.mapproject.util.Resource
import com.example.mapproject.util.hasInternetConnection
import com.example.mapproject.util.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LogoutUserViewModel @Inject constructor(
    application: Application,
    private val repository: LogoutRepository
) : AndroidViewModel(application){

    private val _getResultLogout = MutableLiveData<Resource<MessageResponse>>()
    val getResultLogout: LiveData<Resource<MessageResponse>> = _getResultLogout

    fun resultLogoutUser(token: String, id: String, language: String, tz: String, tu: String, du: String, cu: String) = viewModelScope.launch {
        logoutResult(token,id,language, tz, tu ,du,cu)
    }

    private suspend fun logoutResult(token: String, id: String, language: String, tz: String, tu: String, du: String, cu: String){
        _getResultLogout.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.logoutUser(token,id,language, tz, tu ,du,cu)
                if (response.isSuccessful) {
                    toast(getApplication(), response.body()!!.message)
                    Log.i("Constant","error is  ${response.body()!!.message}")
                    _getResultLogout.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    _getResultLogout.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getResultLogout.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (e: HttpException) {
            Log.i("Constant","error is exception ${e.message()}")
            //toast(getApplication(), "Exception ${e.message}")
            _getResultLogout.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    _getResultLogout.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }
}