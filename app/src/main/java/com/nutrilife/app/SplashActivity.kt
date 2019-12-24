package com.nutrilife.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.nutrilife.app.Clases.VAR


class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        this.supportActionBar?.hide()
  //      FacebookSdk.sdkInitialize(this)
//        AppEventsLogger.activateApp(this)


        val sharedPref: SharedPreferences = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        val mostrarHolaActivity = sharedPref.getBoolean(VAR.PREF_HOLA_ACTIVITY, true)
        if(mostrarHolaActivity){
            mostrarHolaActivity()
        }else{
            mostrarLoginActivity()
        }

    }
    fun mostrarHolaActivity(){
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, HolaActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }, 2000)
    }

    fun mostrarLoginActivity(){
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }, 1700)
    }
}