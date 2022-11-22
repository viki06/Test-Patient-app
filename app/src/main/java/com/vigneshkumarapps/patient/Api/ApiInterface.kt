package com.vigneshkumarapps.patient.Api

import com.google.gson.JsonObject
import com.vigneshkumarapps.patient.AppConstant
import com.vigneshkumarapps.patient.Models
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {


    @POST("web/api/default/login")
    fun loginServiceMethod(
        @Header("Content-Type") content_type: String = "application/json",
        @Header("APPID") appID: String = AppConstant.APPID,
        @Header("lang") language: String = AppConstant.LANGAUGEID,
        @Body json: Models.loginRequest?
    ): Call<Models.LoginResponse>?

    //web/api/doctor/doc-list

    @POST("web/api/doctor/doc-list")
    fun getDoctorList(
        @Header("APPID") appID: String = AppConstant.APPID,
        @Header("lang") language: String = AppConstant.LANGAUGEID,
        @Header("Authorization") auth : String = AppConstant.EMPTYSTRING
    ): Call<Models.DoctorResponse>?


}