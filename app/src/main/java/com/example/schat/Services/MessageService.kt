package com.example.schat.Services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.schat.Controller.App
import com.example.schat.Model.Channel
import com.example.schat.Model.Message
import com.example.schat.Utilities.BROADCAST_USER_DATA_CHANGE
import com.example.schat.Utilities.URL_GET_CHANNEL
import com.example.schat.Utilities.URL_GET_MESSAGE
import com.example.schat.Utilities.URL_GET_USER
import org.json.JSONException

object MessageService {
    val channels = ArrayList<Channel>()
    val messages= ArrayList<Message>()

    fun getChannels(complete:(Boolean)->Unit){
        val channelRequest = object: JsonArrayRequest(Method.GET,"$URL_GET_CHANNEL${App.sharedPrefs.userEmail}",null, Response.Listener { response->
            try {
               for(x in 0 until response.length()){
                   val channel = response.getJSONObject(x)
                   val name = channel.getString("name")
                   val description = channel.getString("description")
                   val id = channel.getString("_id")
                   val newChannel  = Channel(name,description,id)
                   this.channels.add(newChannel)
               }
                complete(true)
            }catch (e: JSONException){
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
        App.sharedPrefs.requestQueue.add(channelRequest)
    }
    fun getMessages(channelId:String,complete:(Boolean)->Unit){

        val messageRequest = object: JsonArrayRequest(Method.GET,"$URL_GET_MESSAGE${channelId}",null, Response.Listener { response->
            clearMessages()
            try {
               for(x in 0 until response.length()){
                   val message = response.getJSONObject(x)
                   val messageBody = message.getString("messageBody")
                   val channelId = message.getString("channelId")
                   val id= message.getString("_id")
                   val userName= message.getString("userName")
                   val userAvatar= message.getString("userAvatar")
                   val userAvatarColor = message.getString("userAvatarColor")
                   val timeStamp= message.getString("timeStamp")

                   val newMessage  = Message(messageBody,userName,channelId,userAvatar,userAvatarColor,id,timeStamp)
                   this.messages.add(newMessage)
               }
                complete(true)
            }catch (e: JSONException){
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
        App.sharedPrefs.requestQueue.add(messageRequest)
    }
    fun clearChannels(){
        channels.clear()
    }
    fun clearMessages(){
        messages.clear()
    }
}