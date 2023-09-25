package com.example.mapproject.ui.monitor

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.model.devices.DevicesResponse
import com.example.mapproject.model.getAddressLocation.AddressLocation
import com.example.mapproject.model.showFences.Fences
import com.example.mapproject.repository.DeviceAndAddressRepository
import com.example.mapproject.repository.GetFencesRepository
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.hasInternetConnection
import com.example.mapproject.util.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MonitorViewModel @Inject constructor(
    application: Application,
    private val repository: DeviceAndAddressRepository,
    private val getFencesRepository: GetFencesRepository,
    private val sharedPref: SharedPreferences
) : AndroidViewModel(application){

    private val _getDevices = MutableLiveData<Resource<DevicesResponse>>()
    val getDevices : LiveData<Resource<DevicesResponse>> = _getDevices

    private val _getAddress = MutableLiveData<Resource<AddressLocation>>()
    val getAddress : LiveData<Resource<AddressLocation>> = _getAddress

    private val _getFences = MutableLiveData<Resource<Fences>>()
    val getFences: LiveData<Resource<Fences>> = _getFences

    private var isGetDevices: Boolean = false
    private var isGetDevicesRunning = false

    fun getFences(token: String,id:String,language: String, tz: String, tu: String,du: String , cu: String) = viewModelScope.launch(Dispatchers.IO) {
        getResultFences(token,id,language, tz, tu,du ,cu)
    }

    private suspend fun getResultFences(token: String, id:String, language: String, tz: String, tu: String, du: String, cu:String){
        _getFences.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = getFencesRepository.getFences(token,id,language, tz, tu ,du,cu)
                if (response.isSuccessful) {
                    _getFences.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main){
                        toast(getApplication(), errorOriginal)
                    }
                    _getFences.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getFences.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }            }
        } catch (e: HttpException) {
            //toast(getApplication(), "Exception ${e.message}")
            _getFences.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    _getFences.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun getDevices(token: String, id: String, position: String, deviceId: String, language: String, tz: String, tu: String, du: String, cu: String) = viewModelScope.launch (Dispatchers.IO) {
        isGetDevices = sharedPref.getBoolean(Constants.IS_GET_DEVICES, false)
        while (!isGetDevices) {
            var deviceId = ""
            deviceId = sharedPref.getString(Constants.DEVICE_ID, "").toString()
            if (!isGetDevicesRunning) {
                isGetDevicesRunning = true
                getResultDevices(token, id, position, deviceId, language, tz, tu, du, cu)
                isGetDevicesRunning = false
            }
            delay(10000)
        }
    }

    fun getDevicesAgain(token: String, id: String, position: String, deviceId: String, language: String, tz: String, tu: String, du: String, cu: String) = viewModelScope.launch (Dispatchers.IO) {
        getResultDevices(token, id, position, deviceId, language, tz, tu, du, cu)
    }

    private suspend fun getResultDevices(token: String,id: String,position: String, deviceId: String,language: String, tz: String, tu: String ,du: String, cu: String){
        _getDevices.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.getDevices(token,id,position,deviceId,language,tz,tu,du,cu)
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

    fun getAddress(token: String,latitude: String,longitude: String,language: String, tz: String, tu: String ,du: String, cu: String) = viewModelScope.launch(Dispatchers.IO) {
        getResultAddress(token,latitude,longitude,language,tz,tu,du,cu)
    }

    private suspend fun getResultAddress(token: String,latitude: String,longitude: String,language: String, tz: String, tu: String ,du: String, cu: String){
        _getAddress.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.getAddressLocation(token,latitude,longitude,language,tz,tu,du,cu)
                if (response.isSuccessful) {
                    _getAddress.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main){
                        toast(getApplication(), errorOriginal)
                    }
                    _getAddress.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getAddress.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }
            }
        } catch (e: HttpException) {
            _getAddress.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _getAddress.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }
}