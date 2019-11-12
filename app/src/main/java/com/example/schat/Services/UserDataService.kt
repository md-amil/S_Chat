package com.example.schat.Services

import android.graphics.Color
import android.util.Log
import com.example.schat.Controller.App
import java.util.*

object UserDataService {
    var id = ""
    var avatarColor = ""
    var avatarName =""
    var email = ""
    var name = ""
    fun logout(){
        id = ""
        avatarColor = ""
        avatarName =""
        email = ""
        name = ""
        App.sharedPrefs.authToken = ""
        App.sharedPrefs.userEmail = ""
        App.sharedPrefs.isLoggedIn = false
        MessageService.clearMessages()
        MessageService.clearChannels()
    }
    fun returnAvatarColor(component:String): Int {
        val stripColor = component
            .replace("["," ")
            .replace("]"," ")
            .replace(","," ")
        var r = 0
        var g = 0
        var b = 0
        val scanner = Scanner(stripColor)
        if(scanner.hasNextDouble()){

            r = (scanner.nextDouble()*255).toInt()
            g = (scanner.nextDouble()*255).toInt()
            b = (scanner.nextDouble()*255).toInt()

        }
        return Color.rgb(r,g,b)
    }
}