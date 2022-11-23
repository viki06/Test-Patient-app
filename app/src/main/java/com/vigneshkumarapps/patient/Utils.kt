package com.vigneshkumarapps.patient

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast

object Utils {

    fun displayToast(context : Context, msg : String){

        Toast.makeText(context,msg,Toast.LENGTH_LONG).show()

    }

    fun dispalyAlertDialog(context: Context, msg: String){

        val alertDialog = AlertDialog.Builder(context).apply {

            setCancelable(false)

            setTitle("Alert")

            setMessage(msg)

            setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }

        }

        val alertDialog1 = alertDialog.create().show()

    }

    fun isOnline(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities != null) {

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")

                return true

            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")

                return true

            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {

                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")

                return true
            }

        }

        return false
    }

}