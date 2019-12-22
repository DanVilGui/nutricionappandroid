package com.nutrilife.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.nutrilife.app.Clases.VAR
import org.angmarch.views.NiceSpinner
import org.json.JSONObject
import java.util.*
import java.util.Arrays.asList


class RutinasConocerActivity: AppCompatActivity() {

    var txtCaminar:NiceSpinner? = null
    var txtEscaleras:NiceSpinner?=null
    var sharedPref: SharedPreferences? = null
    var lastClick: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        setContentView(R.layout.rutina_conocer)
        this.supportActionBar?.hide()
        val dataset: List<String> = LinkedList(asList("0", "1","2","3", "4","5","6","7","8"))

       /* txtCaminar = findViewById(R.id.sp_caminar)

        val adapter: ArrayAdapter<*> =
            ArrayAdapter<String>(this, R.layout.spinner_item_hora, R.id.texto, dataset)
        txtCaminar?.adapter = adapter
        */

        txtCaminar = findViewById(R.id.sp_caminar)
        txtCaminar?.attachDataSource(dataset)

        txtEscaleras = findViewById(R.id.sp_escaleras)
        txtEscaleras?.attachDataSource(dataset)




        val btnContinuar:Button = findViewById(R.id.btnContinuar)
        btnContinuar.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000){
                val rutina = JSONObject()
                rutina.put("caminar", txtCaminar?.selectedItem.toString().toInt())
                rutina.put("escaleras", txtEscaleras?.selectedItem.toString().toInt())
                sharedPref?.edit {
                    putString(VAR.PREF_TEMP_RUTINA, rutina.toString())
                }
                val intent = Intent(applicationContext, RutinasTrabajoActivity::class.java)
                startActivity(intent)
            }

            lastClick = SystemClock.elapsedRealtime()
        }

    }
}
