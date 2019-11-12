package com.example.schat.Controller

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schat.Adapter.MessageAdapter
import com.example.schat.R
import com.example.schat.Services.AuthService
import com.example.schat.Services.MessageService
import com.example.schat.Services.UserDataService
import com.example.schat.Model.Channel
import com.example.schat.Model.Message
import com.example.schat.Utilities.BROADCAST_USER_DATA_CHANGE
import com.example.schat.Utilities.SOCKET_URL
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {
    lateinit var channelAdapter: ArrayAdapter<Channel>
    lateinit var messageAdapter: MessageAdapter
    private val socket: Socket = IO.socket(SOCKET_URL)
    var selectedChannel:Channel?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        socket.connect()
        socket.on("channelCreated",onNewChannel)
        socket.on("messageCreated",onNewMessage)

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE))
        setupAdapter()
        channel_list.setOnItemClickListener { _, _, i, _ ->
            selectedChannel = MessageService.channels[i]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }
        if(App.sharedPrefs.isLoggedIn){
            AuthService.findUserByEmail(this){}
        }
    }
    private fun setupAdapter(){
        channelAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,MessageService.channels)
        channel_list.adapter = channelAdapter
        messageAdapter = MessageAdapter(this,MessageService.messages)
        messageListView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager

    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
        
    }

    private val userDataChangeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {

            if(App.sharedPrefs.isLoggedIn){
                userNameNavheader.text = UserDataService.name
                userEmailNavheader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName,"drawable",packageName)
                userImageNavheader.setImageResource(resourceId)
                userImageNavheader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavheader.text = "Logout"
                MessageService.getChannels{
                    if(it){
                        if(MessageService.channels.count() > 0){
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }
                }
            }
        }
    }
    fun updateWithChannel(){
        mainChannelName.text = "${selectedChannel?.name}"
        if(selectedChannel != null){
            MessageService.getMessages(selectedChannel!!.id){
                if(it){
                    messageAdapter.notifyDataSetChanged()
                    if(messageAdapter.itemCount>0){
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount -1)
                    }
                }
            }
        }
    }

    fun loginBtnNavClicked(view:View){
        if(App.sharedPrefs.isLoggedIn){
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            UserDataService.logout()
            userNameNavheader.text = ""
            userEmailNavheader.text = ""
            userImageNavheader.setImageResource(R.drawable.profiledefault)
            userImageNavheader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavheader.text = "Login"
            mainChannelName.text = "please log in"
        }else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
    fun addChannelBtnClicked(view: View){
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog,null)
        builder.setView(dialogView)
            .setPositiveButton("add"){_ , _ ->
                val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameField)
                val descTextField = dialogView.findViewById<EditText>(R.id.addChennelDescField)
                val channelName = nameTextField.text.toString()
                val channelDesc = descTextField.text.toString()
                socket.emit("new channel",channelName,channelDesc)
            }
            .setNegativeButton("cancel"){ _ , _ ->
            }
            .show()
    }
    fun sendMsgBtnClicked(view:View){
        if(App.sharedPrefs.isLoggedIn && messageTextField.text.toString().isNotEmpty() && selectedChannel != null){
            val userId =  UserDataService.id
            val channelId = selectedChannel!!.id
            socket.emit("newMessage",messageTextField.text.toString(),userId,channelId,UserDataService.name,UserDataService.avatarName,UserDataService.avatarColor)
            messageTextField.text.clear()
            hideKeyboard()
        }
    }
    private fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }
    private val onNewChannel = Emitter.Listener {
        if(App.sharedPrefs.isLoggedIn){
            runOnUiThread {
                val channelName = it[0] as String
                val channelDescription = it[1] as String
                val channelId = it[2] as String
                val newChannel =Channel (channelName,channelDescription,channelId)
                MessageService.channels.add(newChannel)
            }
        }
    }
    private val onNewMessage = Emitter.Listener {
        if(App.sharedPrefs.isLoggedIn){
            runOnUiThread {
                val channelId = it[1] as String
                if(channelId == selectedChannel?.id){
                    val msgBody = it[0] as String
                    val userName = it[2] as String
                    val userAvatar = it[3] as String
                    val userAvatarColor = it[4] as String
                    val id = it[5] as String
                    val timestamp = it[6] as String
                    val newMessage =Message(msgBody,userName,channelId,userAvatar,userAvatarColor,id,timestamp)
                    MessageService.messages.add(newMessage)
                    messageAdapter.notifyDataSetChanged()
                    messageListView.smoothScrollToPosition(messageAdapter.itemCount-1)
                }
            }
        }
    }
}

