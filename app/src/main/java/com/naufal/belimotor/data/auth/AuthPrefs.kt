package com.naufal.belimotor.data.auth

import android.content.SharedPreferences
import javax.inject.Inject

class AuthPrefs @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun getLoginState(): Boolean {
        return sharedPreferences.getBoolean(KEY_LOGIN_STATE, false)
    }

    fun setLoginState(loginState: Boolean) {
        sharedPreferences.edit().putBoolean(
            KEY_LOGIN_STATE, loginState
        ).apply()
    }

    companion object {
        const val KEY_LOGIN_STATE = "login_state"
    }

}