package com.example.schat.Adapter

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.schat.Model.Message
import com.example.schat.R
import com.example.schat.Services.UserDataService
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MessageAdapter (val context: Context, val messages:ArrayList<Message>):RecyclerView.Adapter<MessageAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMessage(context,messages[position])
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val userImage: ImageView = itemView.findViewById(R.id.messageUserImage)
        private val timeStamp: TextView = itemView.findViewById(R.id.timeStampLbl)
        private val userName:TextView = itemView.findViewById(R.id.messageUsernameLbl)
        private val messageBody:TextView = itemView.findViewById(R.id.messageBodyLbl)
        fun bindMessage(context: Context,messages:Message){
            val resourceId = context.resources.getIdentifier(messages.userAvatar,"drawable",context.packageName)
            userImage.setImageResource(resourceId)
            userImage.setBackgroundColor(UserDataService.returnAvatarColor(messages.userAvatarColor))
            userName.text = messages.UserName
            timeStamp.text = returnDateString(messages.timeStamp)
            messageBody.text = messages.message
        }
        private fun returnDateString(isoString:String):String{
          val isoDateFormatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'" , Locale.getDefault())
            isoDateFormatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
//
            var convertedDate = Date()
            try {
                convertedDate = isoDateFormatter.parse(isoString)
            }catch (e:ParseException){
                Log.d("PARSE","cannot parse date")
            }
            val outDateString = java.text.SimpleDateFormat("E,h:mm a", Locale.getDefault())
             return outDateString.format(convertedDate)
        }

    }
}