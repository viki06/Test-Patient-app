package com.vigneshkumarapps.patient.doctorlist

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.vigneshkumarapps.patient.databinding.ActivityDoctorListBinding

class DoctorListActivity : AppCompatActivity() {

    val mViewModel : DoctorListViewModel by viewModels()

    var _binding: ActivityDoctorListBinding? = null

    val mBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        _binding = ActivityDoctorListBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        addObserver()

    }

    private fun addObserver() {

        mViewModel.doctorList.observe(this, Observer {

            if (it.success){

                mBinding.recyclerView.adapter = DoctorListAdapter(this,it.data)

                mBinding.progessBar.root.visibility = View.GONE

            }

        })

    }


    companion object {

        fun getIntent(context: Context): Intent =
            Intent(context, DoctorListActivity::class.java)

    }

}