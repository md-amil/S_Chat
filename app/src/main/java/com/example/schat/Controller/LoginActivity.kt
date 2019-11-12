package com.example.schat.Controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.schat.R
import com.example.schat.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = View.INVISIBLE
    }
    fun loginLoginBtnClicked(){
        isEnableSpinner(true)
        val email = loginEmailTxt.text.toString()
        val password = loginPwdTxt.text.toString()
        hideKeyboard()
        if(email.isNotEmpty() && password.isNotEmpty()){
            AuthService.loginUser(email,password){
                if(it){
                    AuthService.findUserByEmail(this){
                        if(it){
                            finish()
                            isEnableSpinner(false)
                        }else{
                            errorToast()
                        }
                    }
                }else{
                    errorToast() 
                }
            }
        }else{
            Toast.makeText(this,"make sure email and password are filled in", Toast.LENGTH_SHORT).show()
            isEnableSpinner(false)

        }

    }
    fun loginCreateUserBtnClicked(view: View){
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
    }
    private fun errorToast(){
        Toast.makeText(this,"somthing went wrong plzzzz try again latre", Toast.LENGTH_SHORT).show()
        isEnableSpinner(false)
    }

    private fun isEnableSpinner(enable:Boolean){
        if(enable) {
            loginSpinner.visibility = View.VISIBLE
        }else{
            loginSpinner.visibility = View.INVISIBLE
        }
        loginLoginBtn.isEnabled = !enable
        loginCreateUserBtn.isEnabled = !enable
    }
    private fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }
}
