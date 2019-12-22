package com.nutrilife.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Adapters.DeporteListAdapter
import com.nutrilife.app.Clases.ClsDeporte
import com.nutrilife.app.Clases.VAR
import es.dmoral.toasty.Toasty
import org.angmarch.views.NiceSpinner
import org.json.JSONObject
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList


class RutinasDeporteActivity: AppCompatActivity() {

    var recyclerView: RecyclerView?=null
    var sharedPref: SharedPreferences? = null
    var rutinaAnterior:JSONObject? = null
    var lastClick: Long = 0
    var PROCESAR_AGREGAR = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        setContentView(R.layout.rutina_deportes)
        this.supportActionBar?.hide()

        val rutinaStr =  sharedPref?.getString(VAR.PREF_TEMP_RUTINA,null)
        if(rutinaStr!=null){
            rutinaAnterior = JSONObject(rutinaStr)
        }

        val dataset: List<String> = LinkedList(asList("0", "1","2","3", "4","5","6","7","8"))

        recyclerView = findViewById(R.id.recyclerView)
        val listaDeportes:ArrayList<ClsDeporte> = ArrayList()
        listaDeportes.add(ClsDeporte("ciclismo", "CICLISMO", dataset, 0))
        listaDeportes.add(ClsDeporte("futbol", "FÚTBOL", dataset, 0))
        listaDeportes.add(ClsDeporte("danza", "DANZA", dataset, 0))
        listaDeportes.add(ClsDeporte("baloncesto", "BALONCESTO", dataset, 0))
        listaDeportes.add(ClsDeporte("natacion", "NATACIÓN", dataset, 0))
        listaDeportes.add(ClsDeporte("tenis", "TENIS", dataset, 0))
        listaDeportes.add(ClsDeporte("correr", "CORRER", dataset, 0))

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = DeporteListAdapter(applicationContext,listaDeportes)
        }

        val btnContinuar:Button = findViewById(R.id.btnContinuar)
        btnContinuar.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000){
                val copiaRutina = JSONObject(rutinaAnterior.toString())
                listaDeportes.forEach {
                    copiaRutina.put(it.tipo, it.horas)
                }


                if(PROCESAR_AGREGAR){
                  actualizarRutina(copiaRutina)
                }


            }
            lastClick = SystemClock.elapsedRealtime()

        }




    }
    fun actualizarRutina(parameters:JSONObject){
        PROCESAR_AGREGAR = false


        Log.e("myerror", parameters.toString())

        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("persona_registrar_rutina"),parameters,
            Response.Listener { response ->
                if(response!=null){
                    val success = response.getBoolean("success")
                    val message = response.getString("message")
                    if(success){
                        val rutina = response.getJSONObject("rutina")
                        val datosPersona = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
                        val data = JSONObject(datosPersona)
                        data.put("rutina", rutina)
                        sharedPref?.edit {
                            putString(VAR.PREF_DATA_USUARIO, data.toString())
                        }
                        Log.e("myerror", data.toString())
                        Toasty.success(applicationContext, message, Toast.LENGTH_SHORT, true).show()
                        actualizarPreferencias()
                    }else{
                        Toasty.warning(applicationContext, message, Toast.LENGTH_SHORT, true).show()
                    }

                    PROCESAR_AGREGAR = true
                }

            },
            Response.ErrorListener{
                try {
                    PROCESAR_AGREGAR = true
                    Toasty.error(applicationContext, "Error de conexión.", Toast.LENGTH_SHORT, true).show()
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

    fun actualizarPreferencias(){
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
