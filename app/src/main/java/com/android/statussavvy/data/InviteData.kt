package com.android.statussavvy.data


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class InviteData(
    @SerializedName("howitWork")
    @Expose
    val howitWork: List<String>,
    @SerializedName("inviteFbUrl")
    @Expose
    val inviteFbUrl: String,
    @SerializedName("inviteImgurl")
    @Expose
    val inviteImgurl: String,
    @SerializedName("inviteText")
    @Expose
    val inviteText: String,
    @SerializedName("inviteTextUrl")
    @Expose
    val inviteTextUrl: String,
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("referralCode")
    @Expose
    val referralCode: String,
    @SerializedName("status")
    @Expose
    val status: Int
)