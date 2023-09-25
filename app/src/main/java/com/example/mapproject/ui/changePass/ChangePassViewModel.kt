package com.example.mapproject.ui.changePass

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.model.changePassWord.ChangePasswordRequest
import com.example.mapproject.model.MessageResponse
import com.example.mapproject.repository.ChangePassWordRepository
import com.example.mapproject.util.Resource
import com.example.mapproject.util.hasInternetConnection
import com.example.mapproject.util.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ChangePassViewModel @Inject constructor(
    application: Application,
    private val repository: ChangePassWordRepository
) : AndroidViewModel(application){

    private val _getResultChangePass = MutableLiveData<Resource<MessageResponse>>()
    val getResultChangePass: LiveData<Resource<MessageResponse>> = _getResultChangePass

    fun getResultChange(token:String,id: String,language: String, tz: String, tu: String,du: String , cu: String ,changePasswordRequest: ChangePasswordRequest)
    = viewModelScope.launch {
        resultChange(token,id,language, tz, tu,du , cu,changePasswordRequest)
    }

    private suspend fun resultChange(token:String,id: String,language: String, tz: String, tu: String, du: String, cu: String, changePasswordRequest: ChangePasswordRequest){
        _getResultChangePass.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.getResultChangePass(token,id,language, tz, tu ,du, cu,changePasswordRequest)
                if (response.isSuccessful) {
                    Log.i("ChangePass","error is  ${response.body()!!.message}")
                    _getResultChangePass.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    Log.i("ChangePass","error is  ${response.message()}")
                    _getResultChangePass.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getResultChangePass.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (e: HttpException) {
            Log.i("ChangePass","error is eception ${e.message()}")
            //toast(getApplication(), "Exception ${e.message}")
            _getResultChangePass.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    Log.i("ChangePass","error is eception  ${t.message}")
                    _getResultChangePass.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }
}