package com.nutrilife.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.nutrilife.app.Clases.VAR


class HolaActivity: AppCompatActivity() {

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
            sharedPref.edit {
                putBoolean(VAR.PREF_HOLA_ACTIVITY, false)
            }
            mostrarLoginActivity()
        }
    }
    fun mostrarLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}