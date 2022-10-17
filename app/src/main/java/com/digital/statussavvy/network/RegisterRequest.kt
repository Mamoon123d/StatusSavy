package com.digital.statussavvy.network

data class RegisterRequest(
    var deviceId: String? = null,
    var deviceType: String? = null,
    var deviceName: String? = null,

    var socialType: String? = null,
    var socialId: String? = null,
    var socialToken: String? = null,

    var socialEmail: String? = null,
    var socialName: String? = null,
    var socialImgurl: String? = null,

    var advertisingId: String? = null,
    var versionName: String? = null,
    var versionCode: String? = null,

    var utmSource: String? = null,
    var utmMedium: String? = null,
    //var utmCampaign: String=null?:"",
   // var fcmToken: String?,
    var referalUrl: String? = null


)
