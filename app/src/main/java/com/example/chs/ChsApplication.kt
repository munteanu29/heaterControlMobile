package com.example.chs

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.preference.PowerPreference

class ChsApplication: MultiDexApplication() {
    public val authClient = AuthClient()

    companion object {
        lateinit var instance:  ChsApplication
    }

    override fun onCreate() {
        super.onCreate()
        PowerPreference.init(this);
        instance = this
    }

}