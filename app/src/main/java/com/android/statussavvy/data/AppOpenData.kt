package com.android.statussavvy.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AppOpenData(
    @SerializedName("appUrl")
    @Expose
    val appUrl: String,
    @SerializedName("forceUpdate")
    @Expose
    val forceUpdate: Boolean,
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("packAge")
    @Expose
    val packAge: String,
    @SerializedName("status")
    @Expose
    val status: Int,

)