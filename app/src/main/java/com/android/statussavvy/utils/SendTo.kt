package com.android.statussavvy.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

open class SendTo(private val context: Context) {
    public open fun Whatsapp(msg: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.whatsapp")
        try {
            context.startActivity(sendIntent)

        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "There is no WhatsApp application installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }



    public open fun Telegram(msg: String) {


        val appName = "org.telegram.messenger"
        val isAppInstalled: Boolean = isAppAvailable(context, appName)
        if (isAppInstalled) {
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = msg
            myIntent.setPackage(appName)
            myIntent.putExtra(Intent.EXTRA_TEXT, msg) //
            context.startActivity(Intent.createChooser(myIntent, "Share with"))
        } else {
            Toast.makeText(context, "Telegram not Installed", Toast.LENGTH_SHORT).show()
        }
    }

    open fun More(msg: String) {

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, msg)

        shareIntent.type = "text/plain"

        context.startActivity(Intent.createChooser(shareIntent, "Share with friends..."))
    }

    open fun Facebook(msg: String, fbUrl: String) {
        val urlToShare =
            "https://play.google.com/store/apps/details?id=com.facebook.katana&hl=en"
        try {
            val mIntentFacebook = Intent()
            mIntentFacebook.setClassName(
                "com.facebook.katana",
                "com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias"
            )
            mIntentFacebook.action = "android.intent.action.SEND"
            mIntentFacebook.type = "text/plain"
            mIntentFacebook.putExtra("android.intent.extra.TEXT", fbUrl)
            context.startActivity(mIntentFacebook)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            val intent: Intent
            val mStringURL = "https://www.facebook.com/sharer/sharer.php?u=$urlToShare"
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(mStringURL))
            context.startActivity(intent)
        }
    }
    open fun Instagram(msg: String){

        if (isAppAvailable(context,"com.instagram.android")){
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/*"
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          //  shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.putExtra(Intent.EXTRA_TEXT, msg)
            shareIntent.setPackage("com.instagram.android")
        }else{
            Toast.makeText(context, "Instagram not Installed", Toast.LENGTH_SHORT).show()

        }

    }


    open fun isAppAvailable(context: Context, appName: String?): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(appName!!, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: Exception) {
            false
        }
    }
}