package com.example.readitonce

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AlertDialog


internal class loadingDialog(private val activity: Activity) {
    private var alertDialog: AlertDialog? = null
    @SuppressLint("InflateParams")
    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.alert_loading, null))
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog!!.show()
    }

    fun dismisDialog() {
        alertDialog!!.dismiss()
    }
}