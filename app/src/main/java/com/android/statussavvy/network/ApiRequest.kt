package com.android.statussavvy.network

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.android.statussavvy.BuildConfig
import com.android.statussavvy.utils.MyPreference
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

public class ApiRequest {
    companion object {
        fun createRequest(context: Context?): RequestData {
            return RequestData(
                userId = MyPreference.getUserId(context),
                securityToken = MyPreference.getSecurityToken(context),
            )
        }


        fun registerRequest(
            context: Context,
            account: GoogleSignInAccount,
            utm_source_: String,
            utm_medium_: String,
            utm_campaign: String,
            referrerUrl_: String,
            gaid: String

        ): RegisterRequest {
            @SuppressLint("HardwareIds") val android_id =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val registerRequest = RegisterRequest(
                android_id ?: "",
                "",
                Build.MODEL,
                "Google",
                socialId = account.id,
                socialToken = account.idToken,
                socialEmail = account.email,
                socialName = account.displayName,
                socialImgurl = account.photoUrl.toString(),
                advertisingId = gaid,
                versionName = BuildConfig.VERSION_NAME,
                versionCode = BuildConfig.VERSION_CODE.toString(),
                utmSource = utm_source_,
                utmMedium = utm_medium_,
                //  utmCampaign = utm_campaign,

                referalUrl = referrerUrl_,

                )


            return registerRequest


        }
    }
}