package com.nutrilife.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.nutrilife.app.Clases.VAR
import org.json.JSONObject


class HolaActivity: AppCompatActivity() {
    var lastClick: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hola_main)
        this.supportActionBar?.hide()
        val btnEmpezar:Button = findViewById(R.id.btnEmpezar)
        val sharedPref: SharedPreferences = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        btnEmpezar.setOnClickListener {

            if (SystemClock.elapsedRealtime() - lastClick >= 1000){
                sharedPref.edit {
                    putBoolean(VAR.PREF_HOLA_ACTIVITY, false)
                }
                mostrarLoginActivity()
            }
            lastClick = SystemClock.elapsedRealtime()

        }
    }
    fun mostrarLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}