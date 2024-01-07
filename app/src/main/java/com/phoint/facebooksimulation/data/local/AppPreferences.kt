package com.phoint.facebooksimulation.data.local

import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(private val preferences: SharedPreferences) {
    fun saveId(id: Long) {
        preferences.edit().putLong("id", id).apply()
    }

    fun getId(): Long {
        return preferences.getLong("id", 0) ?: 0
    }

    fun saveIdPost(id: Long) {
        preferences.edit().putLong("idPost", id).apply()
    }

    fun getIdPost(): Long {
        return preferences.getLong("idPost", 0) ?: 0
    }

    fun saveLoginStatus(isLoggedIn: Boolean, userId: Long) {
        val editor = preferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.putLong("userId", userId)
        editor.apply()
    }

    fun getLoginStatus(): Boolean {
        return preferences.getBoolean("isLoggedIn", false)
    }

    fun getUserId(): Long {
        return preferences.getLong("userId", 0)
    }

    fun logout() {
        val editor = preferences.edit()
        editor.remove("isLoggedIn")
        editor.remove("userId")
        editor.apply()
        Log.d("AppPreferences", "User logged out")
    }
}