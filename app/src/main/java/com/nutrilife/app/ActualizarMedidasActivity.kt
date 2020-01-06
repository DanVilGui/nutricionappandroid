package com.nutrilife.app

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.Clases.Validar
import es.dmoral.toasty.Toasty


class ActualizarMedidasActivity: AppCompatActivity() {

    var txtPeso:EditText ? = null
    var txtTamanio:EditText?=null
    var txtCadera:EditText?=null
    var txtCintura:EditText?=null
    var lastClick: Long = 0
    var control:Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actualizar_medidas)
        this.supportActionBar?.hide()

        txtPeso = findViewById(R.id.peso)
        txtTamanio = findViewById(R.id.tamanio)
        txtCintura = findViewById(R.id.cintura)
        txtCadera = findViewById(R.id.cadera)

        val btnContinuar:Button = findViewById(R.id.btnContinuar)

        control = intent.getBooleanExtra("control", false)


        if(control){
            val txtTitulo:TextView = findViewById(R.id.txtTitulo)
            txtTitulo.text = "Hoy es día de control"
            btnContinuar.text = "Registrar"
        }
        btnContinuar.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000){
                val valido = validarCampos()
                if(valido){
                    val intent = Intent(applicationContext, IMCActivity::class.java)
                    val b = Bundle()
                    b.putDouble("peso",txtPeso?.text.toString().toDouble())
                    b.putDouble("medida",txtTamanio?.text.toString().toDouble())
                    b.putInt("cintura",txtCintura?.text.toString().toInt())
                    b.putInt("cadera",txtCadera?.text.toString().toInt())
                    b.putBoolean("control", control)
                    intent.putExtras(b)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                    //finish()
                }else{
                    Toasty.error(applicationContext, "Verifique los campos", Toast.LENGTH_LONG, true).show()
                }
            }
            lastClick = SystemClock.elapsedRealtime()

        }
    }

    fun validarCampos():Boolean{
        //peso
        if(Validar.vacio(txtPeso)){
            Validar.txtErr(txtPeso, "Ingrese peso!")
            return false
        }
        val peso = txtPeso?.text.toString().toDouble()
        if(peso<40){
            Validar.txtErr(txtPeso, "Ingrese un peso mayor o igual a 40 Kg")
            return false
        }
        if(peso>300){
            Validar.txtErr(txtPeso, "Ingrese un peso menor o igual a 300 Kg")
            return false
        }
        //medida
        if(Validar.vacio(txtTamanio)){
            Validar.txtErr(txtTamanio, "Ingrese su medida!")
            return false
        }
        val tamanio = txtTamanio?.text.toString().toDouble()
        if(tamanio<0.4){
            Validar.txtErr(txtTamanio, "Ingrese una medida mayor a 0.4 metros")
            return false
        }
        if(tamanio>2.5){
            Validar.txtErr(txtTamanio, "Ingrese una medida menor a 2.5 metros")
            return false
        }

        //Cintura
        if(Validar.vacio(txtCintura)){
            Validar.txtErr(txtCintura, "Ingrese su cintura en cm")
            return false
        }
        val cintura = txtCintura?.text.toString().toDouble()
        if(cintura<30){
            Validar.txtErr(txtCintura, "Ingrese una valor mayor a 30cm")
            return false
        }
        if(cintura>200){
            Validar.txtErr(txtCintura, "Ingrese un peso menor a 200cm")
            return false
        }

        //cadera
        if(Validar.vacio(txtCadera)){
            Validar.txtErr(txtCadera, "Ingrese su cadera en cm!")
            return false
        }
        val cadera = txtCadera?.text.toString().toDouble()
        if(cadera<30){
            Validar.txtErr(txtCadera, "Ingrese una valor mayor a 30cm")
            return false
        }
        if(cadera>200){
            Validar.txtErr(txtCadera, "Ingrese un peso menor a 200cm")
            return false
        }

        return true
    }

}
