package com.digital.statussavvy.instaApp

import android.content.Context
import android.content.SharedPreferences



class InstagramSession(context: Context) {
    private val sharedPref: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }


    public fun storeAccessToken(
        accessToken: String?, id: String?,
        username: String?, name: String?, image: String?
    ) {
        editor.putString(API_ID, id)
        editor.putString(API_NAME, name)
        editor.putString(API_ACCESS_TOKEN, accessToken)
        editor.putString(API_USERNAME, username)
        editor.putString(API_USER_IMAGE, image)
        editor.commit()
    }

    fun storeAccessToken(accessToken: String?) {
        editor.putString(API_ACCESS_TOKEN, accessToken)
        editor.commit()
    }

    /**
     * Reset access token and user name
     */
    fun resetAccessToken() {
        editor.putString(API_ID, null)
        editor.putString(API_NAME, null)
        editor.putString(API_ACCESS_TOKEN, null)
        editor.putString(API_USERNAME, null)
        editor.putString(API_USER_IMAGE, null)
        editor.commit()
    }

    /**
     * Get user name
     *
     * @return User name
     */
    val username: String?
        get() = sharedPref.getString(API_USERNAME, null)

    /**
     *
     * @return
     */
    val id: String?
        get() = sharedPref.getString(API_ID, null)

    /**
     *
     * @return
     */
    val name: String?
        get() = sharedPref.getString(API_NAME, null)

    /**
     * Get access token
     *
     * @return Access token
     */
    val accessToken: String?
        get() = sharedPref.getString(API_ACCESS_TOKEN, null)


    val userImage: String?
        get() = sharedPref.getString(API_USER_IMAGE, null)

    companion object {
        private const val SHARED = "Instagram_Preferences"
        private const val API_USERNAME = "username"
        private const val API_ID = "id"
        private const val API_NAME = "name"
        private const val API_ACCESS_TOKEN = "access_token"
        private const val API_USER_IMAGE = "user_image"
    }
}