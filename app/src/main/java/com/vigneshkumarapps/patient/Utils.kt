package com.vigneshkumarapps.patient

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast

object Utils {

    fun displayToast(context : Context, msg : String){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
    }

    fun alertDialog(context: Context, msg: String){
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setCancelable(false)
        alertDialog.setTitle("Alert")
        alertDialog.setMessage(msg)
        alertDialog.setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }
        val alertDialog1 = alertDialog.create()
        alertDialog1.show()
    }

}