package com.nutrilife.app

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Clases.VAR
import es.dmoral.toasty.Toasty
import org.angmarch.views.NiceSpinner
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.*
import java.util.Arrays.asList


class EvaluacionActivity: AppCompatActivity() {
    var requestQueue:RequestQueue? = null
    var loadingDialog: Dialog? = null
    var sharedPref: SharedPreferences? = null
    var lastClick: Long = 0
    var PROCESAR_AGREGAR = true

    var txtKcal:TextView? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        setContentView(R.layout.evaluacion)
        this.supportActionBar?.hide()
        txtKcal = findViewById(R.id.kcal)
        requestQueue =  Volley.newRequestQueue(this)
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.loading_dialog)
        loadingDialog = builder.create()
        procesarAlgoritmo()
        val btnContinuar:Button = findViewById(R.id.btnContinuar)
        btnContinuar.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000){
                if(PROCESAR_AGREGAR){
                    val intent = Intent(applicationContext, EvaluacionVerPlanActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            lastClick = SystemClock.elapsedRealtime()

        }
    }

    fun procesarAlgoritmo(){
        loadingDialog?.setCancelable(false)
        loadingDialog?.setCanceledOnTouchOutside(false)

        loadingDialog?.show()
        PROCESAR_AGREGAR = false
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("persona_algoritmos"), null,
            Response.Listener { response ->
                if(response!=null){
                    val success = response.getBoolean("success")
                    val message = response.getString("message")
                    if(success){
                        loadingDialog?.dismiss()
                        val kcal = response.getInt("kcal")
                        txtKcal?.text = kcal.toString()
                        Toasty.success(applicationContext, message, Toast.LENGTH_SHORT, true).show()

                        val datosPersona = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
                        if(datosPersona!="") {
                            val data = JSONObject(datosPersona)
                            val rutina = data.getJSONObject("rutina")
                            rutina.put("recalcular", 0)
                            data.put("rutina", rutina)
                            sharedPref?.edit {
                                putString(VAR.PREF_DATA_USUARIO, data.toString())
                                putString(VAR.PREF_CAMBIARRUTINA, "")
                            }
                            PROCESAR_AGREGAR = true
                        }
                    }else{
                        PROCESAR_AGREGAR = false
                        loadingDialog?.dismiss()
                        Toasty.warning(applicationContext, message, Toast.LENGTH_SHORT, true).show()
                    }
                }

            },
            Response.ErrorListener{
                try {
                    PROCESAR_AGREGAR = true
                    loadingDialog?.dismiss()
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
        val socketTimeout = 2*60*1000

        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        request.retryPolicy = policy

        requestQueue?.add(request)

    }



}
