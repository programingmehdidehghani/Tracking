package com.example.mapproject.ui.devicesFragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.model.devices.DevicesResponse
import com.example.mapproject.repository.DeviceAndAddressRepository
import com.example.mapproject.repository.SearchRepository
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
class DevicesFragmentViewModel @Inject constructor(
    application: Application,
    private val repository: SearchRepository,
    private val repositoryDevice: DeviceAndAddressRepository,
) : AndroidViewModel(application){

    private val _getResultSearchGroup = MutableLiveData<Resource<DevicesResponse>>()
    val getResultSearchGroup : LiveData<Resource<DevicesResponse>> = _getResultSearchGroup


    private val _getDevices = MutableLiveData<Resource<DevicesResponse>>()
    val getDevices : LiveData<Resource<DevicesResponse>> = _getDevices


    fun getDevices(token: String,id: String,position: String, deviceId: String,language: String, tz: String, tu: String ,du: String, cu: String) = viewModelScope.launch(Dispatchers.IO) {
        getResultDevices(token, id, position, deviceId, language, tz, tu, du, cu)
    }

    private suspend fun getResultDevices(token: String,id: String,position: String, deviceId: String,language: String, tz: String, tu: String ,du: String, cu: String){
        _getDevices.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repositoryDevice.getDevices(token,id,position,deviceId,language,tz,tu,du,cu)
                if (response.isSuccessful) {
                    _getDevices.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main){
                        toast(getApplication(), errorOriginal)
                    }
                    _getDevices.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getDevices.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }
            }
        } catch (e: HttpException) {
            _getDevices.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _getDevices.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }


    fun getResultSearchGroup(token: String,id: String,query: String,position: String, deviceId: String,language: String, tz: String, tu: String ,du: String, cu: String) = viewModelScope.launch(Dispatchers.IO) {
        getResultSearchDevicesRepository(token,id,query,position,deviceId,language,tz,tu,du,cu)
    }

    private suspend fun getResultSearchDevicesRepository(token: String,id: String,query: String,position: String, deviceId: String,language: String, tz: String, tu: String ,du: String, cu: String){
        _getResultSearchGroup.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.searchGroup(token,id,query,position,deviceId,language,tz,tu,du,cu)
                if (response.isSuccessful) {
                    Log.i("PhoneMobile","response is  ${response.body()!!}")
                    _getResultSearchGroup.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main){
                        toast(getApplication(), errorOriginal)
                    }
                    _getResultSearchGroup.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getResultSearchGroup.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }
            }
        } catch (e: HttpException) {
            Log.i("PhoneMobile","error is eception ${e.message()}")
            _getResultSearchGroup.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    Log.i("PhoneMobile","error is exception  ${t.message}")
                    _getResultSearchGroup.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }
}