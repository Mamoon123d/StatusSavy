package com.digital.statussavvy.activity

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.digital.statussavvy.R
import com.digital.statussavvy.data.AppOpenData
import com.digital.statussavvy.databinding.SplashBinding
import com.digital.statussavvy.network.ApiClient
import com.digital.statussavvy.network.ApiRequest
import com.digital.statussavvy.network.NetworkHelper
import com.digital.statussavvy.utils.MyPreference
import com.moon.baselibrary.base.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Splash:BaseActivity<SplashBinding>() {

    companion object {
        fun AppOpen(context: Context) {
            val isNetwork = NetworkHelper.isNetworkAvailable(context)
            if (isNetwork) {
                val call = ApiClient.getApi().getAppOpen(ApiRequest.createRequest(context))
                call.enqueue(object : Callback<AppOpenData> {
                    override fun onResponse(
                        call: Call<AppOpenData>,
                        response: Response<AppOpenData>
                    ) {
                        val data = response.body()
                        if (data != null) {
                            if (data.forceUpdate) {
                                setUpdateApp(context, data.appUrl)
                            } else {
                                val b = Bundle()
                               // b.putString(DataSet.User.USER_BALANCE, data.userAmount)
                                context.startActivity(Intent(context, Home::class.java).putExtras(b))
                                (context as Activity).finish()

                            }

                        }
                    }

                    override fun onFailure(call: Call<AppOpenData>, t: Throwable) {

                    }

                })
            } else {
                Toast.makeText(context, "Network Not Available", Toast.LENGTH_SHORT).show()

            }

        }

        private fun setUpdateApp(context: Context, appUrl: String) {

            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setTitle("Update App")
            dialog.setContentView(R.layout.update_dialog)
            val update_bt = dialog.findViewById<TextView>(R.id.update_bt)
            update_bt.setOnClickListener { v: View? ->
                //final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appUrl)))
                } catch (ante: ActivityNotFoundException) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appUrl)))
                }
            }
            dialog.show()
        }

    }

    override fun setLayoutId(): Int {
    return R.layout.splash
    }

    override fun initM() {
        val userId = MyPreference.getUserId(this).toInt()
        val securityToken = MyPreference.getUserId(this)
        Handler(Looper.getMainLooper()).postDelayed({
            if (userId > 0 && !securityToken.isNullOrEmpty()) {
                AppOpen(this)
            } else {
                goActivity(Login())
                finish()
            }

        }, 100)

    }
}