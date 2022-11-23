package com.vigneshkumarapps.patient.doctorlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import com.vigneshkumarapps.patient.R
import com.vigneshkumarapps.patient.databinding.ActivityDoctorListBinding
import com.vigneshkumarapps.patient.login.LoginActivity


class DoctorListActivity : AppCompatActivity() {

    val mViewModel : DoctorListViewModel by viewModels()

    var _binding: ActivityDoctorListBinding? = null

    val mBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        _binding = ActivityDoctorListBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        mBinding.progessBar.root.visibility = View.VISIBLE

        addObserver()

        addOnClickListener()

    }

    private fun addOnClickListener() {

        with(mBinding.signout) {

            setOnClickListener {

                showPopup(this)

            }

        }

    }

    private fun addObserver() {

        mViewModel.doctorList.observe(this, Observer {

            if (it.success){

                mBinding.recyclerView.adapter =
                    DoctorListAdapter(
                        context=   this,
                        data= it.data
                    )

                mBinding.progessBar.root.visibility = View.GONE

            }

        })

    }

    fun showPopup(v: View) {

        val popupMenu: PopupMenu = PopupMenu(this, v)

        popupMenu.menuInflater.inflate(R.menu.option, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.signout -> {

                    startActivity(LoginActivity.getIntent(this))

                    finish()

                }

            }

            true
        })

        popupMenu.show()
    }


    companion object {

        fun getIntent(context: Context): Intent =
            Intent(context, DoctorListActivity::class.java)

    }

}