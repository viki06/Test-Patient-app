package com.vigneshkumarapps.patient.doctorlist

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.vigneshkumarapps.patient.R
import com.vigneshkumarapps.patient.databinding.ActivityDoctorListBinding
import com.vigneshkumarapps.patient.login.LoginActivity
import com.vigneshkumarapps.patient.videocall.VideoCallActivity


class DoctorListActivity : AppCompatActivity() {

    val mViewModel: DoctorListViewModel by viewModels()

    var _binding: ActivityDoctorListBinding? = null

    val mBinding get() = _binding!!

    private val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    private val PERMISSIONS_REQUEST_ALL = 0x7c9

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        _binding = ActivityDoctorListBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        mBinding.progessBar.root.visibility = View.VISIBLE

        addObserver()

        addOnClickListener()

    }

    private fun addOnClickListener() {

        mBinding.signout.setOnClickListener {

            showPopup(it)

        }



        mBinding.call.setOnClickListener {

            requestPermissions()

        }

    }

    private fun addObserver() {

        mViewModel.doctorList.observe(this, Observer {

            if (it.success) {

                mBinding.recyclerView.adapter =
                    DoctorListAdapter(
                        context = this,
                        data = it.data
                    )

                mBinding.progessBar.root.visibility = View.GONE

            }

        })

    }

    private fun requestPermissions() {

        val permissionsNeeded: MutableList<String> = ArrayList()

        for (permission in PERMISSIONS) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) permissionsNeeded.add(permission)

        }
        if (permissionsNeeded.size > 0) {

            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(), PERMISSIONS_REQUEST_ALL
            )

        } else {

           showCallPopup(mBinding.call)

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_ALL) requestPermissions()

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

    fun showCallPopup(v: View) {

        val popupMenu: PopupMenu = PopupMenu(this, v)

        popupMenu.menuInflater.inflate(R.menu.call_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.newCall -> {

                    startActivity(VideoCallActivity.getIntent(this@DoctorListActivity, true))

                }

                R.id.joinCall -> {

                    startActivity(VideoCallActivity.getIntent(this@DoctorListActivity, false))

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