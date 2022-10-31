package com.android.statussavvy.data


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class ScreensData(
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("status")
    @Expose
    val status: Int,
    @SerializedName("wallpaperList")
    @Expose
    val wallpaperList: List<Wallpaper>
) {
    data class Wallpaper(
        @SerializedName("original_url")
        @Expose
        val originalUrl: String,
        @SerializedName("small_url")
        @Expose
        val smallUrl: String
    )
}