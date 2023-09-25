package com.example.mapproject.ui.alerts

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.model.MessageResponse
import com.example.mapproject.model.alerts.Alerts
import com.example.mapproject.model.changeEmail.VerifyCodeEmailRequest
import com.example.mapproject.repository.AlertsRepository
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
class AlertsViewModel @Inject constructor(
    application: Application,
    private val repository: AlertsRepository
) : AndroidViewModel(application){

    private val _getAlerts = MutableLiveData<Resource<Alerts>>()
    val getResultAlerts : LiveData<Resource<Alerts>> = _getAlerts

    fun getAlerts(token: String,
                  id: String,
                  from: String,
                  status: String,
                  to: String,
                  type: String,
                  orderBy: String,
                  orderType: String,
                  page: String,
                  perPage: String,
                  deviceId: String,
                  language: String,
                  tz: String,
                  tu: String,
                  du: String,
                  cu: String) = viewModelScope.launch {
        getResultAlerts(token,
            id,
            from,
            status,
            to,
            type,
            orderBy,
            orderType,
            page,
            perPage,
            deviceId,
            language,
            tz,
            tu,
            du,
            cu)
    }

    private suspend fun getResultAlerts(token: String,
                                        id: String,
                                        from: String,
                                        status: String,
                                        to: String,
                                        type: String,
                                        orderBy: String,
                                        orderType: String,
                                        page: String,
                                        perPage: String,
                                        deviceId: String,
                                        language: String,
                                        tz: String,
                                        tu: String,
                                        du: String,
                                        cu: String){
        _getAlerts.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.getAlerts(token,
                    id,
                    from,
                    status,
                    to,
                    type,
                    orderBy,
                    orderType,
                    page,
                    perPage,
                    deviceId,
                    language,
                    tz,
                    tu,
                    du,
                    cu)
                if (response.isSuccessful) {
                    _getAlerts.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    _getAlerts.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getAlerts.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (e: HttpException) {
            //toast(getApplication(), "Exception ${e.message}")
            _getAlerts.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    //  toast(getApplication(), "Exception ${t.message}")
                    _getAlerts.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }
}