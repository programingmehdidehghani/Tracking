package com.example.mapproject.ui.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.R
import com.example.mapproject.model.*
import com.example.mapproject.model.changeInformationProfile.ChangeProfileInformationRequest
import com.example.mapproject.model.profile.GetProfile
import com.example.mapproject.repository.ProfileRepository
import com.example.mapproject.util.Resource
import com.example.mapproject.util.hasInternetConnection
import com.example.mapproject.util.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val repository: ProfileRepository
) : AndroidViewModel(application){

    private val _getProfile = MutableLiveData<Resource<GetProfile>>()
    val getProfile: LiveData<Resource<GetProfile>> = _getProfile


    fun getProfile(id:String,token: String,language: String, tz: String, tu: String,du: String , cu: String) = viewModelScope.launch(
        Dispatchers.IO)  {
        profile(id,token,language, tz, tu,du , cu)
    }


    private suspend fun profile(id:String,token: String,language: String, tz: String, tu: String,du: String , cu:String){
        _getProfile.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.getProfile(id,token,language, tz, tu ,du,cu)
                if (response.isSuccessful) {
                    Log.i("ChangePass","error is  ${response.body()!!.result.role}")
                    _getProfile.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main){
                        toast(getApplication(), errorOriginal)
                    }
                    _getProfile.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getProfile.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }
            }
        } catch (e: HttpException) {
            Log.i("ChangePass","error is exception ${e.message()}")
            //toast(getApplication(), "Exception ${e.message}")
            _getProfile.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    Log.i("ChangePass","error is exception  ${t.message}")
                    _getProfile.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

}