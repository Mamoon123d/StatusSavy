package com.android.statussavvy.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("securityToken")
    @Expose
    val securityToken: String,
    @SerializedName("status")
    @Expose
    val status: Int,
    @SerializedName("userId")
    @Expose
    val userId: Int,

    @SerializedName("socialImgurl")
    @Expose
    val socialImgurl: String,
)