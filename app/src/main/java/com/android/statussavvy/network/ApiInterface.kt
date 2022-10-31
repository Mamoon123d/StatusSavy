package com.android.statussavvy.network

import com.android.statussavvy.data.AppOpenData
import com.android.statussavvy.data.InviteData
import com.android.statussavvy.data.RegisterResponse
import com.android.statussavvy.data.ScreensData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiInterface {
    @POST("userSignup")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("appOpen")
    fun getAppOpen(@Body createRequest: RequestData): Call<AppOpenData>

    @POST("screenSaver")
    fun getScreens(@Body createRequest: RequestData): Call<ScreensData>

    @POST("appInvite")
    fun getInviteData(@Body createRequest: RequestData): Call<InviteData>


}
