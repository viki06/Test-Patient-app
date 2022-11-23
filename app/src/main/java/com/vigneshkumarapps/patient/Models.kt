package com.vigneshkumarapps.patient

import com.google.gson.annotations.SerializedName

object Models {

    var UserData: LoginResponse? = null

    data class loginRequest(
        @SerializedName("username") var username: String = "",
        @SerializedName("password") var password: String = "",
        @SerializedName("device_id") var device_id: String = "",
        @SerializedName("os_id") var os_id: String = "",
        @SerializedName("time_zone") var time_zone: String = "",
        @SerializedName("role_id") var role_id: String = ""
    )

    data class LoginResponse(
        val `data`: Data,
        val msg: String,
        val success: Boolean
    )

    data class Data(
        val Currency: Currency,
        val NotitificationDetails: NotitificationDetails,
        val PatientProfile: PatientProfile,
        val PatientProfileLocation: PatientProfileLocation,
        val User: User,
        val access_token: String,
        val counts: Counts,
        val device: Device,
        val pushStatus: PushStatus,
        val userType: String
    )

    data class Currency(
        val country_id: String,
        val hospital: String,
        val prefix: String,
        val symbol: String
    )

    data class NotitificationDetails(
        val notified: Int,
        val read: Int,
        val totalappointment: Int,
        val totalcall: Int,
        val totalpush: Int,
        val unnotified: Int,
        val unread: Int
    )

    data class PatientProfile(
        val alternate_email: String,
        val bmi: String,
        val display_name: String,
        val dob: String,
        val driving_licence: String,
        val expiry_date: String,
        val first_name: String,
        val gender: String,
        val height: String,
        val id: String,
        val insurance_back_side: String,
        val insurance_front_side: String,
        val last_name: String,
        val linkedin_url: String,
        val middle_name: String,
        val nationality: String,
        val note: String,
        val preferedlanguageid: String,
        val preferedlanguagename: String,
        val profile_picture: String,
        val salute: String,
        val ssno: String,
        val suffix: String,
        val sys_ethnicity_id: String,
        val sys_language_id: String,
        val sys_race_id: String,
        val sys_time_zone_id: String,
        val user_id: String,
        val website_url: String,
        val weight: String
    )

    data class PatientProfileLocation(
        val city_id: String,
        val city_name: String,
        val country_id: String,
        val country_name: String,
        val door_no: String,
        val id: String,
        val landmark: String,
        val locality: String,
        val notes: String,
        val phone1: String,
        val phone2: String,
        val postal_code: String,
        val state_id: String,
        val state_name: String,
        val street_name: String,
        val user_id: String
    )

    data class User(
        val avaya_ext: String,
        val avaya_password: String,
        val avaya_username: String,
        val email: String,
        val hospital_id: String,
        val id: String,
        val password: String,
        val ribbon_ext: String,
        val ribbon_password: String,
        val ribbon_username: String,
        val status: Int,
        val username: String
    )

    data class Counts(
        val todayappointment: Int,
        val todaycall: Int,
        val totalappointment: Int,
        val totalcall: Int
    )

    data class Device(
        val device_id: String,
        val os_id: String,
        val status: String,
        val voip_token: String
    )

    data class PushStatus(
        val Android: Android
    )

    data class Android(
        val `55c3389cb5ddd720dc0297617f3561c43a36218a277c974c8d43d545a643f45c`: String
    )

    data class DoctorResponse(
        @SerializedName("data") val data: ArrayList<DoctorData>,
        val msg: String,
        val success: Boolean
    )

    data class DoctorData(
        val additional_minutes: Int,
        val avaya_ext: String,
        val avaya_password: String,
        val avaya_username: String,
        val busy_on: String,
        val email: String,
        val first_name: String,
        val fixed_charge: String,
        val fixed_duration: String,
        val id: String,
        val name: String,
        val online_status: String,
        val profile_picture: String,
        val rating: Int,
        val specialty: ArrayList<Specialty>,
        val state_id: String
    )

    data class Specialty(
        val id: String,
        val name: String
    )

}

