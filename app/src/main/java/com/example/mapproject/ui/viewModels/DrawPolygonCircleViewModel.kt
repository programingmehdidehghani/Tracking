package com.example.mapproject.ui.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.model.MessageResponse
import com.example.mapproject.model.circleDrawMap.DrawCircleMap
import com.example.mapproject.model.drawPolygon.DrawPolygon
import com.example.mapproject.model.editFences.EditFences
import com.example.mapproject.repository.DrawPolygonRepository
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
class DrawPolygonCircleViewModel @Inject constructor(
    application: Application,
    private val repository: DrawPolygonRepository
): AndroidViewModel(application) {

    private val _drawPolygon = MutableLiveData<Resource<MessageResponse>>()
    val drawPolygon : LiveData<Resource<MessageResponse>> = _drawPolygon

    private val _drawCircle = MutableLiveData<Resource<MessageResponse>>()
    val drawCircle : LiveData<Resource<MessageResponse>> = _drawCircle

    private val _editFences = MutableLiveData<Resource<MessageResponse>>()
    val editFences : LiveData<Resource<MessageResponse>> = _editFences

    fun circleDraw(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,drawCircleMap: DrawCircleMap) = viewModelScope.launch(Dispatchers.IO) {
        getDrawCircle(token,id,language,tz,tu,du,cu,drawCircleMap)
    }

    fun drawPolygon(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,drawPolygon: DrawPolygon) = viewModelScope.launch  {
        getDrawPolygon(token,id,language,tz,tu,du,cu,drawPolygon)
    }

    fun editFences(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,editFences: EditFences) = viewModelScope.launch (Dispatchers.IO)  {
        getEditFences(token,id,language,tz,tu,du,cu,editFences)
    }

    private suspend fun getEditFences(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,editFences: EditFences){
        _editFences.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.editFences(token,id,language,tz,tu,du,cu,editFences)
                if (response.isSuccessful) {
                    _editFences.postValue(Resource.Success(response.body()!!))
                    withContext(Dispatchers.Main) {
                        toast(getApplication(), response.body()!!.message)
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main) {
                        toast(getApplication(), errorOriginal)
                    }
                    _editFences.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _editFences.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }
            }
        } catch (e: HttpException) {
            _editFences.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _editFences.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }


    private suspend fun getDrawCircle(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,drawCircleMap: DrawCircleMap){
        _drawCircle.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.drawCircle(token,id,language,tz,tu,du,cu,drawCircleMap)
                if (response.isSuccessful) {
                    _drawCircle.postValue(Resource.Success(response.body()!!))
                    withContext(Dispatchers.Main) {
                        toast(getApplication(), response.body()!!.message)
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    withContext(Dispatchers.Main) {
                        toast(getApplication(), errorOriginal)
                    }
                    _drawCircle.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _drawCircle.postValue(Resource.Error("No Internet Connection.!"))
                withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
                }
            }
        } catch (e: HttpException) {
            _drawCircle.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _drawCircle.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

    private suspend fun getDrawPolygon(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,drawPolygon: DrawPolygon){
        _drawPolygon.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.drawPolygon(token,id,language,tz,tu,du,cu,drawPolygon)
                if (response.isSuccessful) {
                    _drawPolygon.postValue(Resource.Success(response.body()!!))
                   // withContext(Dispatchers.Main) {
                        toast(getApplication(), response.body()!!.message)
                 //   }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                //    withContext(Dispatchers.Main) {
                        _drawPolygon.postValue(Resource.Error(response.message()))
                 //   }
                    Resource.Error(response.message())
                }
            } else {
                _drawPolygon.postValue(Resource.Error("No Internet Connection.!"))
            //    withContext(Dispatchers.Main){
                    toast(getApplication(), "No Internet Connection.!")
            //    }
            }
        } catch (e: HttpException) {
            _drawPolygon.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _drawPolygon.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }
}