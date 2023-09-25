package com.example.mapproject.ui.editProfile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapproject.MyApp
import com.example.mapproject.model.MessageResponse
import com.example.mapproject.model.changeInformationProfile.ChangeProfileInformationRequest
import com.example.mapproject.repository.EditProfileRepository
import com.example.mapproject.util.Resource
import com.example.mapproject.util.hasInternetConnection
import com.example.mapproject.util.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    application: Application,
    private val repository: EditProfileRepository
) : AndroidViewModel(application) {

    private val _getResultEditProfile = MutableLiveData<Resource<MessageResponse>>()
    val getResultEditProfile : LiveData<Resource<MessageResponse>> = _getResultEditProfile

    private val _getResultPictureProfile = MutableLiveData<Resource<MessageResponse>>()
    val getResultPictureProfile : LiveData<Resource<MessageResponse>> = _getResultPictureProfile

    fun editProfile(token:String,id: String,language: String, tz: String, tu: String,du: String , cu: String ,changeProfileInformationRequest: ChangeProfileInformationRequest) = viewModelScope.launch (
        Dispatchers.IO) {
        getResultEditProfile(token,id,language, tz, tu,du , cu,changeProfileInformationRequest)
    }

    fun updatePictureProfile(
        token: String,
        id: String,
        language: String, tz: String, tu: String,
        du: String, cu: String,
        pictureProfile: MultipartBody.Part
    ) = viewModelScope.launch {
        resultUpdatePictureProfile(token,id,language, tz, tu ,du,cu,pictureProfile)
    }

    private suspend fun resultUpdatePictureProfile(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,pictureProfile: MultipartBody.Part){
        _getResultPictureProfile.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.updatePictureProfile(id,token,language, tz, tu ,du,cu,pictureProfile)
                if (response.isSuccessful) {
                    toast(getApplication(), response.body()!!.message)
                    _getResultPictureProfile.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    _getResultPictureProfile.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getResultPictureProfile.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (e: HttpException) {
            Log.i("ChangeInformation","error is exception ${e.message()}")
            _getResultPictureProfile.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    Log.i("ChangeInformation","error is exception  ${t.message}")
                    _getResultPictureProfile.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }


    private suspend fun getResultEditProfile(token:String,id: String,language: String, tz: String, tu: String,du: String , cu: String ,changeProfileInformationRequest: ChangeProfileInformationRequest){
        _getResultEditProfile.postValue(Resource.Loading)
        try {
            if (hasInternetConnection<MyApp>()) {
                val response = repository.editProfile(id,token,language, tz, tu ,du,cu,changeProfileInformationRequest)
                if (response.isSuccessful) {
                    toast(getApplication(), response.body()!!.message)
                    _getResultEditProfile.postValue(Resource.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    val separated: Array<String> = errorMessage?.split(":")!!.toTypedArray()
                    val error1 = separated[2]
                    val errorResponse = error1.replace("\"}","")
                    val errorOriginal = errorResponse.replace("\"","")
                    toast(getApplication(), errorOriginal)
                    _getResultEditProfile.postValue(Resource.Error(response.message()))
                    Resource.Error(response.message())
                }
            } else {
                _getResultEditProfile.postValue(Resource.Error("No Internet Connection.!"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (e: HttpException) {
            Log.i("ChangeInformation","error is exception ${e.message()}")
            _getResultEditProfile.postValue(Resource.Error(e.message()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    Log.i("ChangeInformation","error is exception  ${t.message}")
                    _getResultEditProfile.postValue(Resource.Error(t.message!!))
                }
            }
        }
    }

}