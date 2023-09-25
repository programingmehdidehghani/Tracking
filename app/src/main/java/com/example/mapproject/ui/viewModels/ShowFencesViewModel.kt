package com.example.mapproject.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.R
import com.example.mapproject.model.MessageResponse
import com.example.mapproject.model.showFences.Fences
import com.example.mapproject.repository.GetFencesRepository
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
class ShowFencesViewModel @Inject constructor(
    application: Application,
    private val repository: GetFencesRepository
): AndroidViewModel(application) {

    private val _getFences = MutableLiveData<Resource<Fences>>()
    val getFences: LiveData<Resource<Fences>> = _getFences

    private val _deletedFences = MutableLiveData<Resource<MessageResponse>>()
    val deletedFences: LiveData<Resource<MessageResponse>> = _deletedFences

    fun getFences(token: String,id:String,language: String, tz: String, tu: String,du: String , cu: String) = viewModelScope.launch (
        Dispatchers.IO) {
        getResultFences(token,id,language, tz, tu,du ,cu)
    }

    fun deletedFences(token: String,id:String,language: String, tz: String, tu: String,du: String , cu: String) = viewModelScope.launch {
        deletedFencesResult(token,id,language, tz, tu,du ,cu)
    }


    private suspend fun deletedFencesResult(token: String,id:String,language: String, tz: String, tu: String,du: String ,cu:String){
        _deletedFences.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.deleteFences(token,id,language, tz, tu ,du,cu)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main){
                        toast(getApplication(), response.body()!!.message)
                    }
                    _deletedFences.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main){
                        toast(getApplication(), errorOriginal)
                    }
                    _deletedFences.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _deletedFences.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }
            }
        } catch (e: HttpException) {
            //toast(getApplication(), "Exception ${e.message}")
            _deletedFences.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    _deletedFences.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }



    private suspend fun getResultFences(token: String, id:String, language: String, tz: String, tu: String, du: String, cu:String){
        _getFences.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.getFences(token,id,language, tz, tu ,du,cu)
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
                }
            }
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
}