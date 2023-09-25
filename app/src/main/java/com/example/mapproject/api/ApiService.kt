package com.example.mapproject.api

import com.example.mapproject.model.*
import com.example.mapproject.model.SendNameToServer
import com.example.mapproject.model.alerts.Alerts
import com.example.mapproject.model.changeEmail.EmailRequest
import com.example.mapproject.model.changeEmail.VerifyCodeEmailRequest
import com.example.mapproject.model.changeInformationProfile.ChangeProfileInformationRequest
import com.example.mapproject.model.constant.Constant
import com.example.mapproject.model.login.LoginModel
import com.example.mapproject.model.profile.GetProfile
import com.example.mapproject.model.changePassWord.ChangePasswordRequest
import com.example.mapproject.model.changePhoneMobile.ConfirmPhoneMobileRequest
import com.example.mapproject.model.changePhoneMobile.PhoneMobileRequest
import com.example.mapproject.model.circleDrawMap.DrawCircleMap
import com.example.mapproject.model.devices.DevicesResponse
import com.example.mapproject.model.drawPolygon.DrawPolygon
import com.example.mapproject.model.editFences.EditFences
import com.example.mapproject.model.forgetPass.RequestForgetPass
import com.example.mapproject.model.getAddressLocation.AddressLocation
import com.example.mapproject.model.login.LoginRequest
import com.example.mapproject.model.showFences.Fences
import com.example.mapproject.model.verifyForgetPass.VerifyForgetPass
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("constants")
    suspend fun getConstant(
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
    ): Response<Constant>

    @POST("auth/login")
    suspend fun login(
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body loginRequest: LoginRequest
    ): Response<LoginModel>


    @GET("user/{id}")
    suspend fun getProfile(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        ): Response<GetProfile>

    @PUT("user/{id}/password")
    suspend fun changePassword(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body changePassword: ChangePasswordRequest
    ) : Response<MessageResponse>

    @POST("user/{id}/mobile/code")
    suspend fun getCodePhoneNumber(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body phoneMobileRequest: PhoneMobileRequest
    ) : Response<MessageResponse>

    @PUT("user/{id}/mobile")
    suspend fun confirmPhoneNumber(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body confirmPhoneMobileRequest: ConfirmPhoneMobileRequest
    ) : Response<MessageResponse>

    @POST("user/{id}/email/code")
    suspend fun changeEmail(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body emailRequest: EmailRequest
    ) : Response<MessageResponse>

    @PUT("user/{id}/email")
    suspend fun verifyCodeEmail(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body verifyCodeEmailRequest: VerifyCodeEmailRequest
    ) : Response<MessageResponse>

    @PUT("user/{id}/profile")
    suspend fun editProfile(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body changeProfileInformationRequest: ChangeProfileInformationRequest
    ) : Response<MessageResponse>

    @GET("user/{id}/devices/list")
    suspend fun getDevices(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("positions") positions: String,
        @Query("device_id") device_id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
    ) : Response<DevicesResponse>

    @POST("user/{id}/logout")
    suspend fun logoutUser(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
    ) : Response<MessageResponse>

    @POST("auth/forgot/code")
    suspend fun requestForgetPass(
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body forgetPass: RequestForgetPass
    ) : Response<MessageResponse>

    @POST("auth/forgot")
    suspend fun verifyForgetPass(
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body verifyForgetPass: VerifyForgetPass
    ) : Response<MessageResponse>

    @Multipart
    @POST("user/{id}/avatar")
    suspend fun updatePictureProfile(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Part profileImage: MultipartBody.Part
    ) : Response<MessageResponse>

    @GET("user/{id}/alerts")
    suspend fun getAlerts(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("from") from: String,
        @Query("status") status: String,
        @Query("to") to: String,
        @Query("type") type: String,
        @Query("order_by") orderBy: String,
        @Query("order_type") orderType: String,
        @Query("page") page: String,
        @Query("per_page") perPage: String,
        @Query("device_id") deviceId: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String
    ) : Response<Alerts>

    @POST("user/{id}/geofences/polygon")
    suspend fun drawPolyGon(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body drawPolygon: DrawPolygon
    ) : Response<MessageResponse>

    @POST("user/{id}/geofences/circle")
    suspend fun drawCircleMap(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body drawCircleMap: DrawCircleMap
    ) : Response<MessageResponse>

    @GET("user/{id}/geofences")
    suspend fun getFences(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
    ) : Response<Fences>


    @DELETE("geofences/{id}")
    suspend fun deleteFences(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
    ) : Response<MessageResponse>

    @PUT("geofences/{id}")
    suspend fun editFences(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body editFences: EditFences
    ) : Response<MessageResponse>

    @POST("user/{id}/groups")
    suspend fun addGroups(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body sendNameToServer: SendNameToServer
    ) : Response<MessageResponse>

    @DELETE("groups/{id}")
    suspend fun deleteGroup(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
    ) : Response<MessageResponse>

    @PUT("groups/{id}")
    suspend fun editGroup(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
        @Body sendNameToServer: SendNameToServer
    ) : Response<MessageResponse>

    @GET("user/{id}/devices/list")
    suspend fun searchGroup(
        @Header("Authorization:") token: String,
        @Path("id") id: String,
        @Query("query") query: String,
        @Query("positions") positions: String,
        @Query("device_id") device_id: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
    ) : Response<DevicesResponse>

    @GET("map/reverse_geocoding")
    suspend fun getAddressLocation(
        @Header("Authorization:") token: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("language") language: String,
        @Query("tz") tz: String,
        @Query("tu") tu: String,
        @Query("du") du: String,
        @Query("cu") cu: String,
    ) : Response<AddressLocation>


}