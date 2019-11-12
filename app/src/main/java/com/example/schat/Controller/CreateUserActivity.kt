package com.example.schat.Controller

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.schat.R
import com.example.schat.Services.AuthService
import com.example.schat.Services.UserDataService
import com.example.schat.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {
        var userAvatar = "profileDefault"
        var avatarColor = "[0.5,0.5,0.5,1]"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        progressBar.visibility = INVISIBLE
//        val avatar =UserDataService.returnAvatarColor(avatarColor)
//        Toast.makeText(this,"color$avatar",Toast.LENGTH_LONG).show()

    }
    fun generateUserAvatar(view: View){
        var random = Random()
        var color = random.nextInt(2)
        var avatar = random.nextInt(28)
        if(color==0){
            userAvatar = "dark$avatar"
        }else{
            userAvatar = "light$avatar"
        }
        val resourceId = resources.getIdentifier(userAvatar,"drawable",packageName)
            createAvatarImgView.setImageResource(resourceId)
    }
    fun backgroundColorBtnClicked(view: View){
        var random = Random()
        var r = random.nextInt(255)
        var g = random.nextInt(255)
        var b = random.nextInt(255)
        createAvatarImgView.setBackgroundColor(Color.rgb(r,g,b))
        var saveR = r.toDouble()/255
        var saveG = g.toDouble()/255
        var saveB = b.toDouble()/255
        avatarColor = "[$saveR,$saveG,$saveB,1]"
    }
    fun createUserBtnClicked(view: View){
//        isEnableSpinner(true)
        val name = createUserNameTxt.text.toString()
        val email =createUserEmailTxt.text.toString()
        val password = createUserpwdTxt.text.toString()
        if(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            AuthService.registerUser(email,password){
                if(it){
                    AuthService.loginUser(email,password){
                        if(it) {
                            AuthService.createUser(name,email,userAvatar,avatarColor){
                                if(it){
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
//                                    isEnableSpinner(false)
                                    finish()
                                }else{
                                    errorToast()
                                }
                            }
                        }else{
                            errorToast()
                        }
                    }
                }else{
                    Toast.makeText(this,"something went wrong with register user",Toast.LENGTH_SHORT).show()

//                    errorToast()
                }
            }
        }else{
            Toast.makeText(this,"plz make sure name email and password are filled in",Toast.LENGTH_SHORT).show()
//            isEnableSpinner(false)
        }
    }

    private fun errorToast(){
        Toast.makeText(this,"somthing went wrong plzzzz try again latre",Toast.LENGTH_SHORT).show()
//        isEnableSpinner(false)
    }

    private fun isEnableSpinner(enable:Boolean){
        if(enable) {
            progressBar.visibility = VISIBLE
        }else{
            progressBar.visibility = INVISIBLE
        }
        loginCreateUserBtn.isEnabled = !enable
        createAvatarImgView.isEnabled = !enable
        backgroundColorBtn.isEnabled = !enable
    }


}
