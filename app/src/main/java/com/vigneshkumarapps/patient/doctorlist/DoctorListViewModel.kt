package com.vigneshkumarapps.patient.doctorlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vigneshkumarapps.patient.Models
import com.vigneshkumarapps.patient.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DoctorListViewModel : ViewModel(){

    var doctorList = MutableLiveData<Models.DoctorResponse>()



    init {

        viewModelScope.launch(Dispatchers.IO) {

            val response = Repository.getDoctorList(auth = Models.UserData!!.data.access_token)

            doctorList.apply {
                postValue(response)
            }

        }
    }

}