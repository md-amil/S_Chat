package com.example.schat.Services

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.provider.SyncStateContract
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.schat.Controller.App
import com.example.schat.Utilities.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {
//    var isLoggedIn = false
//    var userEmail = ""
//    var authToken = ""

    fun registerUser(email:String,password:String,complete:(Boolean)->Unit){

        val jsonBody = JSONObject()
        jsonBody.put("email",email)
        jsonBody.put("password",password)
        val requestBody= jsonBody.toString()
        val registerRequest = object:StringRequest(Method.POST,URL_REGISTER, Response.Listener {
            complete(true)
        }, Response.ErrorListener {
            complete(false)

        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.sharedPrefs.requestQueue.add(registerRequest)
    }
    fun loginUser(email: String,password: String,complete:(Boolean)->Unit){
        val jsonBody = JSONObject()
        jsonBody.put("email",email)
        jsonBody.put("password",password)
        val requestBody= jsonBody.toString()
        val loginRequest = object: JsonObjectRequest(Method.POST,URL_LOGIN,null, Response.Listener {
            try {
                App.sharedPrefs.userEmail = it.getString("user")
                App.sharedPrefs.authToken = it.getString("token")
                App.sharedPrefs.isLoggedIn = true
                complete(true)
            }catch (e:JSONException){
                Log.d("JSON","EXC"+e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener {
            complete(false)
            println(it)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        App.sharedPrefs.requestQueue.add(loginRequest)
    }
    fun createUser(name:String,email:String,avatarName:String,avatarColor:String,complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put("name",name)
        jsonBody.put("email",email)
        jsonBody.put("avatarName",avatarName)
        jsonBody.put("avatarColor",avatarColor)
        val requestBody = jsonBody.toString()
        val createUserRequest = object: JsonObjectRequest(Method.POST,URL_CREATE_USER,null, Response.Listener {
            try {
                UserDataService.id = it.getString("_id")
                UserDataService.name = it.getString("name")
                UserDataService.email = it.getString("email")
                UserDataService.avatarColor = it.getString("avatarColor")
                UserDataService.avatarName = it.getString("avatarName")
                complete(true)
            }catch (e:JSONException){
                Log.d("JSON","EXC"+e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener {
            complete(false)
            println(it)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization","Bearer ${App.sharedPrefs.authToken}")
                return headers
            }
        }
        App.sharedPrefs.requestQueue.add(createUserRequest)
    }

    fun findUserByEmail(context: Context,complete: (Boolean) -> Unit){
        val findUserRequest = object: JsonObjectRequest(Method.GET,"$URL_GET_USER${App.sharedPrefs.userEmail}",null, Response.Listener {
            try {
                UserDataService.name = it.getString("name")
                UserDataService.email = it.getString("email")
                UserDataService.avatarColor= it.getString("avatarColor")
                UserDataService.avatarName = it.getString("avatarName")
                UserDataService.id = it.getString("_id")
                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                complete(true)
            }catch (e:JSONException){
                Log.d("JSON","EXC"+e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener {
            complete(false)

        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization","Bearer ${App.sharedPrefs.authToken}")
                return headers
            }
        }
        App.sharedPrefs.requestQueue.add(findUserRequest)
    }





}