package com.digital.statussavvy.network

import com.digital.statussavvy.BuildConfig

data class RequestData(
    var userId: String,
    val securityToken: String,
    val versionName: String? = BuildConfig.VERSION_NAME,
    val versionCode: Int? = BuildConfig.VERSION_CODE,
)
