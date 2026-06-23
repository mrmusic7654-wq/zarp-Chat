package com.example.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("zarp_prefs", Context.MODE_PRIVATE)

    var points: Int
        get() = prefs.getInt("points", 0)
        set(value) = prefs.edit().putInt("points", value).apply()

    var unlockedGlasses: Boolean
        get() = prefs.getBoolean("unlocked_glasses", false)
        set(value) = prefs.edit().putBoolean("unlocked_glasses", value).apply()

    var unlockedScarf: Boolean
        get() = prefs.getBoolean("unlocked_scarf", false)
        set(value) = prefs.edit().putBoolean("unlocked_scarf", value).apply()

    var currentAccessory: String
        get() = prefs.getString("current_accessory", "NONE") ?: "NONE"
        set(value) = prefs.edit().putString("current_accessory", value).apply()
}
