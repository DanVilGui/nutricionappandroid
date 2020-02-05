package com.nutrilife.app

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.nutrilife.app.Clases.ClsIMC
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.Clases.Validar
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.util.HashMap


class IMCActivity: AppCompatActivity() {

    var sharedPref: SharedPreferences? = null
    var peso:Double = 1.0
    var medida:Double = 1.0
    var cintura:Int = 0
    var cadera:Int = 0
    var valorImc:Double = 0.0
    var preguntar_actualizar_ruta = false
    var PROCESAR_AGREGAR = true
    var lastClick: Long = 0
    var control = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imc)
        this.supportActionBar?.hide()
        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        val lblIMC:TextView = findViewById(R.id.imc)
        val lblIMCTipo:TextView = findViewById(R.id.imcTipo)
        val textoIMC:TextView = findViewById(R.id.imcTexto)
        val b = intent.extras
        if(b!=null){
            peso = b.getDouble("peso")
            medida = b.getDouble("medida")
            cintura = b.getInt("cintura")
            cadera = b.getInt("cadera")
            control = b.getBoolean("control", false)
            preguntar_actualizar_ruta = b.getBoolean("preguntar", false)
            val imc = ClsIMC(peso, medida)
            imc.calcular()
            textoIMC.text = imc.texto
            valorImc = imc.imc
            lblIMC.text = imc.imc.toString()
            lblIMCTipo.text=imc.estado
        }

        val btnContinuar:Button = findViewById(R.id.btnContinuar)
        btnContinuar.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000){
                if( PROCESAR_AGREGAR && valorImc!= 0.0 &&  cintura!=0){
                    agregarMedida()
                }
            }
            lastClick = SystemClock.elapsedRealtime()
        }
    }



    fun agregarMedida(){
        PROCESAR_AGREGAR = false

        val parameters = JSONObject()
        parameters.put("peso", peso)
        parameters.put("medida", medida)
        parameters.put("cintura", cintura)
        parameters.put("cadera", cadera)
        parameters.put("imc", valorImc)

        val request : JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, VAR.url("persona_agregar_medida"),parameters,
                Response.Listener { response ->
                    if(response!=null){
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if(success){
                            val medidas = response.getJSONArray("medidas")
                            val datosPersona = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
                            val data = JSONObject(datosPersona)
                            data.put("medidas", medidas)
                            sharedPref?.edit {
                                putString(VAR.PREF_DATA_USUARIO, data.toString())
                            }
                            Toasty.success(applicationContext, message, Toast.LENGTH_LONG, true).show()
                            mostrarActualizarRutina()
                        }else{
                            Toasty.warning(applicationContext, message, Toast.LENGTH_LONG, true).show()
                        }

                        PROCESAR_AGREGAR = true
                    }

                },
                Response.ErrorListener{
                    try {
                        PROCESAR_AGREGAR = true
                        Toasty.error(applicationContext, "Error de conexión.", Toast.LENGTH_LONG, true).show()
                        Log.e("myerror",  (it.message))
                        val nr = it.networkResponse
                        val r = String(nr.data)
                    }catch (ex:Exception){
                        Log.e("myerror", ex.message.toString())

                    }

                }) {
                override fun getHeaders(): Map<String, String> {
                    var params: MutableMap<String, String> = HashMap()
                    params["TOKEN"] =  sharedPref?.getString("token", "")!!
                    return params
                }
            }

            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(request)
    }

    fun mostrarActualizarRutina(){
        if(control){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Modificar Rutina")
            builder.setMessage("Está seguro que desea modificar su rutina?")
            val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
                when(which){
                    DialogInterface.BUTTON_POSITIVE ->{
                        sharedPref?.edit {
                            putString(VAR.PREF_CAMBIARRUTINA, "1")
                        }
                        val intent = Intent(this, RutinasConocerActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    DialogInterface.BUTTON_NEGATIVE ->{
                        sharedPref?.edit {
                            putString(VAR.PREF_CAMBIARRUTINA, "")
                        }
                        val intent = Intent(this, EvaluacionActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            builder.setPositiveButton("SI",dialogClickListener)
            builder.setNegativeButton("NO",dialogClickListener)
            val dialog = builder.create()
            dialog.show()
            return
        }

        val datosPersona = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
        if(datosPersona!=""){
            val data = JSONObject(datosPersona)
            if( data.isNull("rutina") ){
                val intent = Intent(applicationContext, RutinasConocerActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else{
                /*
                    enviar a preferencias !!!
                 */
                val intent = Intent(applicationContext, PreferenciasActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }



}
