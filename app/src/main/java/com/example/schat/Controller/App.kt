package com.example.schat.Controller

import android.app.Application
import com.example.schat.Utilities.SharedPrefs

class App:Application() {
    companion object{
        lateinit var sharedPrefs:SharedPrefs
    }
    override fun onCreate() {
        sharedPrefs = SharedPrefs(applicationContext)
        super.onCreate()
    }
}