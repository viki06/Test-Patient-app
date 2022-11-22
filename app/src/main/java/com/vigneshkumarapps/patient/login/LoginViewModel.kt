package com.vigneshkumarapps.patient.login

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.vigneshkumarapps.patient.AppConstant
import com.vigneshkumarapps.patient.Models
import android.provider.Settings
import androidx.lifecycle.viewModelScope
import com.vigneshkumarapps.patient.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.xml.transform.ErrorListener


class LoginViewModel : ViewModel() {

    var listener: onServiceCallListener? = null

    @SuppressLint("HardwareIds")
    fun loginMethod(context: Context, userName: String, pass: String) {

        listener?.onBegin()

        val mId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        val login_request = Models.loginRequest(
            username = userName,
            password = pass,
            os_id = AppConstant.OSID,
            role_id = AppConstant.ROLEID,
            time_zone = AppConstant.TIMEZONE,
            device_id = mId

        )

        viewModelScope.launch(Dispatchers.IO) {

            Models.UserData = Repository.loginServiceMethod(login_request)

            Models.UserData?.let {

                listener?.onLoginResponse(it.msg, it.success)

            }



        }




    }

    fun setViewModelListener(listener : onServiceCallListener){

        this.listener = listener

    }


    interface onServiceCallListener{

        fun onBegin()

        fun onLoginResponse(msg : String, isSuccess : Boolean)

    }

}