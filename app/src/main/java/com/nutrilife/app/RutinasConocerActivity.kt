package com.nutrilife.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import es.dmoral.toasty.Toasty
import org.angmarch.views.NiceSpinner
import java.util.*
import java.util.Arrays.asList


class RutinasConocerActivity: AppCompatActivity() {

    var txtCaminar:NiceSpinner? = null
    var txtEscaleras:NiceSpinner?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            val intent = Intent(applicationContext, RutinasTrabajoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }



}
