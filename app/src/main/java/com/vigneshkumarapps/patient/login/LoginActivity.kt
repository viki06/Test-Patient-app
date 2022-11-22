package com.vigneshkumarapps.patient.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.vigneshkumarapps.patient.Models
import com.vigneshkumarapps.patient.R
import com.vigneshkumarapps.patient.Utils
import com.vigneshkumarapps.patient.databinding.ActivityLoginBinding
import com.vigneshkumarapps.patient.doctorlist.DoctorListActivity


class LoginActivity : AppCompatActivity(), LoginViewModel.onServiceCallListener {

    private val mViewModel: LoginViewModel by viewModels()

    private var _binding: ActivityLoginBinding? = null

    private val mBinding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        addOnClickListener()

        mBinding.userName.setText("patient")

        mBinding.pass.setText("Test@987")

        mViewModel.setViewModelListener(this)

    }

    fun addOnClickListener() {

        mBinding.btnLogin.setOnClickListener {

            if (mBinding.userName.text.isEmpty()) {

                Utils.displayToast(this, getString(R.string.alert_user_empty))

                mBinding.userName.requestFocus()

            } else if (mBinding.pass.text.isEmpty()) {

                Utils.displayToast(this, getString(R.string.alert_pass_empty))

                mBinding.pass.requestFocus()

            } else {

                mViewModel.loginMethod(
                    this,
                    mBinding.userName.text.toString().trim(),
                    mBinding.pass.text.toString().trim()
                )

            }

        }

    }

    override fun onBegin() {

        mBinding.progessBar.root.visibility = View.VISIBLE

    }


    override fun onLoginResponse(msg: String, isSuccess: Boolean) {

        runOnUiThread {

            mBinding.progessBar.root.visibility = View.GONE

            Utils.displayToast(this, msg)

            if (isSuccess) startActivity(DoctorListActivity.getIntent(this))

        }

    }


}