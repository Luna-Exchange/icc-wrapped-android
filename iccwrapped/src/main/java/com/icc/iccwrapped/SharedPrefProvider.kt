package com.icc.iccwrapped

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson

class SharedPrefProvider(private val context: Context) {
    val gson = Gson()

    private fun readString(settingName: String, defaultValue: String?): String? {
        val sharedPref = context.getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE)
        return sharedPref.getString(settingName, defaultValue)
    }

    private fun saveString(settingName: String, settingValue: String?) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(settingName, settingValue)
        editor.apply()
    }

    fun saveAccessToken(token: String) {
        saveString(TOKEN, token)
    }

    fun getAccessToken(): String {
        return readString(TOKEN, "") ?: ""
    }

    fun saveState(action: SdkActions) {
        saveString(STATE, action.name)
    }

    fun getState(): String? {
        return readString(STATE, SdkActions.DEFAULT.name)
    }

    companion object {
        const val PREFERENCES_FILE = "pref_file"
        const val TOKEN = "access_token"
        const val STATE = "state_action"
    }

}