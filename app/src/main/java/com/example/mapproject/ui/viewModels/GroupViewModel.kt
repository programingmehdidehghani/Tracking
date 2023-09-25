package com.example.mapproject.ui.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.R
import com.example.mapproject.model.MessageResponse
import com.example.mapproject.model.SendNameToServer
import com.example.mapproject.model.devices.DevicesResponse
import com.example.mapproject.repository.DeviceAndAddressRepository
import com.example.mapproject.repository.GroupRepository
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
class GroupViewModel @Inject constructor(
    application: Application,
    private val groupRepository: GroupRepository,
    private val repository: DeviceAndAddressRepository,
    ) : AndroidViewModel(application){

    private val _addGroups = MutableLiveData<Resource<MessageResponse>>()
    val addGroups : LiveData<Resource<MessageResponse>> = _addGroups

    private val _getDevices = MutableLiveData<Resource<DevicesResponse>>()
    val getDevices : LiveData<Resource<DevicesResponse>> = _getDevices

    private val _deletedGroup = MutableLiveData<Resource<MessageResponse>>()
    val deletedGroup: LiveData<Resource<MessageResponse>> = _deletedGroup

    private val _editGroup = MutableLiveData<Resource<MessageResponse>>()
    val editGroup: LiveData<Resource<MessageResponse>> = _editGroup



    fun editGroup(token: String,id:String,language: String, tz: String, tu: String,du: String , cu: String,sendNameToServer: SendNameToServer) = viewModelScope.launch(Dispatchers.IO)  {
        editGroupResult(token,id,language, tz, tu,du ,cu,sendNameToServer)
    }


    private suspend fun editGroupResult(token: String,id:String,language: String, tz: String, tu: String,du: String ,cu:String,sendNameToServer: SendNameToServer){
        _editGroup.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = groupRepository.editGroup(token,id,language, tz, tu ,du,cu,sendNameToServer)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main){
                        toast(getApplication(), response.body()!!.message)
                    }
                    _editGroup.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main){
                        toast(getApplication(), errorOriginal)
                    }
                    _editGroup.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _editGroup.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }
            }
        } catch (e: HttpException) {
            //toast(getApplication(), "Exception ${e.message}")
            _editGroup.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    _editGroup.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

    fun deletedGroup(token: String,id:String,language: String, tz: String, tu: String,du: String , cu: String) = viewModelScope.launch (Dispatchers.IO) {
        deletedGroupResult(token,id,language, tz, tu,du ,cu)
    }


    private suspend fun deletedGroupResult(token: String,id:String,language: String, tz: String, tu: String,du: String ,cu:String){
        _deletedGroup.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = groupRepository.deleteGroup(token,id,language, tz, tu ,du,cu)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main){
                        toast(getApplication(), response.body()!!.message)
                    }
                    _deletedGroup.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main){
                        toast(getApplication(), errorOriginal)
                    }
                    _deletedGroup.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _deletedGroup.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }
            }
        } catch (e: HttpException) {
            //toast(getApplication(), "Exception ${e.message}")
            _deletedGroup.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    _deletedGroup.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }


    fun addGroups(token: String, id: String, language: String, tz: String, tu: String, du: String, cu: String, sendNameToServer: SendNameToServer) = viewModelScope.launch (Dispatchers.IO){
        getResultAddGroup(token,id,language,tz,tu,du,cu,sendNameToServer)
    }

    fun getDevices(token: String,id: String,position: String, deviceId: String,language: String, tz: String, tu: String ,du: String, cu: String) = viewModelScope.launch (Dispatchers.IO) {
        getResultDevices(token, id, position, deviceId, language, tz, tu, du, cu)
    }

    private suspend fun getResultDevices(token: String,id: String,position: String, deviceId: String,language: String, tz: String, tu: String ,du: String, cu: String){
        _getDevices.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.getDevices(token,id,position,deviceId,language,tz,tu,du,cu)
                if (response.isSuccessful) {
                    Log.i("PhoneMobile","response is  ${response.body()!!}")
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
            Log.i("PhoneMobile","error is eception ${e.message()}")
            _getDevices.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    Log.i("PhoneMobile","error is exception  ${t.message}")
                    _getDevices.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

    private suspend fun getResultAddGroup(token: String, id: String, language: String, tz: String, tu: String, du: String, cu: String, sendNameToServer: SendNameToServer){
        _addGroups.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = groupRepository.addGroup(token,id,language,tz,tu,du,cu,sendNameToServer)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main){
                        toast(getApplication(), response.body()!!.message)
                    }
                    _addGroups.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main){
                        toast(getApplication(), errorOriginal)
                    }
                    _addGroups.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _addGroups.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }
            }
        } catch (e: HttpException) {
            _addGroups.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _addGroups.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

}