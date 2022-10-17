package com.digital.statussavvy.network

import com.digital.statussavvy.data.AppOpenData
import com.digital.statussavvy.data.RegisterResponse
import com.digital.statussavvy.data.ScreensData
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




}
