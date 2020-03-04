package com.nutrilife.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.nutrilife.app.Clases.VAR


class EvaluacionVerPlanActivity: AppCompatActivity() {
    var sharedPref: SharedPreferences? = null
    var lastClick: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        setContentView(R.layout.evaluacion_verplan)
        this.supportActionBar?.hide()

        val btnContinuar:Button = findViewById(R.id.btnContinuar)
        btnContinuar.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000){
                sharedPref?.edit {
                    putString(VAR.PREF_ADVERTENCIA, "1")
                    putString(VAR.FECHA_HOY, "")
                }
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            lastClick = SystemClock.elapsedRealtime()
        }
    }


}
