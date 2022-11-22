package com.vigneshkumarapps.patient

import com.google.gson.JsonObject
import com.vigneshkumarapps.patient.Api.ApiInterface
import com.vigneshkumarapps.patient.Api.RetrofitClient
import java.util.concurrent.TimeoutException

object Repository {


    fun loginServiceMethod(
        login_request: Models.loginRequest
    ): Models.LoginResponse? {

        val apiInterface: ApiInterface? =
            RetrofitClient().getClient()?.create(ApiInterface::class.java)

        val callback: Any?

        callback = apiInterface!!.loginServiceMethod(json = login_request)

        var s: Models.LoginResponse? = null

        try {

            val response = callback?.execute()

            s = response?.body()!!

        } catch (e: Exception) {

            e.printStackTrace()

            return null

        } catch (te: TimeoutException) {

            te.printStackTrace()

            return null

        }

        return s
    }

    fun getDoctorList(
        auth : String
    ): Models.DoctorResponse? {

        val apiInterface: ApiInterface? =
            RetrofitClient().getClient()?.create(ApiInterface::class.java)

        val callback: Any?

        callback = apiInterface!!.getDoctorList(auth = auth, language = Models.UserData!!.data.PatientProfile.sys_language_id)

        var s: Models.DoctorResponse? = null

        try {

            val response = callback?.execute()

            s = response?.body()!!

        } catch (e: Exception) {

            e.printStackTrace()

        } catch (te: TimeoutException) {

            te.printStackTrace()

        }

        return s
    }

}