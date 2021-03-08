package com.example.chs

import com.preference.PowerPreference
import com.preference.Preference

object PreferencesController {

    const val KEY_TOKEN ="token"


    private val preferences: Preference by lazy {
        PowerPreference.getDefaultFile()
    }


    var token : String
        get() = preferences.getString(KEY_TOKEN, "")
        set(value) {
            preferences.putString(KEY_TOKEN, value)
        }


}