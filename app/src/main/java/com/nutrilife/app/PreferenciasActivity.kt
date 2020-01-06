package com.nutrilife.app

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.forEachIndexed
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
import java.util.*
import java.util.Arrays.asList


class PreferenciasActivity: AppCompatActivity() {
    var requestQueue:RequestQueue? = null
    var loadingDialog: Dialog? = null
    var sharedPref: SharedPreferences? = null
    var lastClick: Long = 0
    var PROCESAR_AGREGAR = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        setContentView(R.layout.preferencias_alimenticias)
        this.supportActionBar?.hide()

       requestQueue =  Volley.newRequestQueue(this)

        val datasetComidas: LinkedList<String> = LinkedList(asList("1","2","3", "4","5","6","7","8"))

        val datasetHambre: LinkedList<String> = LinkedList(asList("FRUTA","COMIDA RAPIDA","GOLOSINAS",
            "CEREALES","NINGUNA"))

        val dataSetVasos: LinkedList<String> = LinkedList()
        val j:Int =1
        for(i in 1 until 21){
            dataSetVasos.add(i.toString())
        }

        val spComida:NiceSpinner = findViewById(R.id.spComidas)
        val spVasos:NiceSpinner = findViewById(R.id.spVasos)
        spComida.attachDataSource(datasetComidas)
        spVasos.attachDataSource(dataSetVasos)
        val groupComidas :RadioGroup = findViewById(R.id.rgComida)
        val groupDieta :RadioGroup = findViewById(R.id.rgDieta)
        val btnContinuar:Button = findViewById(R.id.btnContinuar)

        val datosPersona = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
        if(datosPersona!= ""){
            val data = JSONObject(datosPersona)
            if( !data.isNull("preferencia") ){
                val preferencia = data.getJSONObject("preferencia")
                val cant_comidas = preferencia.getInt("cant_comidas")
                val cant_vasos = preferencia.getInt("cant_vasos")
                val comida_hambre = preferencia.getString("comida_hambre")
                var index_hambre = datasetHambre.indexOf(comida_hambre)
                var index_comida = datasetComidas.indexOf(cant_comidas.toString())
                if(index_comida == -1) index_comida  = 0
                if(index_hambre == -1) index_hambre = 4

                val hace_dieta = preferencia.getInt("hace_dieta")
                spComida.selectedIndex = index_comida
                spVasos.selectedIndex = dataSetVasos.indexOf(cant_vasos.toString())
                ( groupComidas.getChildAt(index_hambre) as RadioButton).isChecked = true
                val index_dieta = if(hace_dieta == 1 ) 0 else 1
                ( groupDieta.getChildAt(index_dieta) as RadioButton).isChecked = true

            }
        }

        btnContinuar.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000) {

                val indexComidas: Int =
                    groupComidas.indexOfChild(findViewById(groupComidas.getCheckedRadioButtonId()))
                val indexDieta: Int =
                    groupDieta.indexOfChild(findViewById(groupDieta.getCheckedRadioButtonId()))

                if(indexComidas==-1){
                    Toasty.warning(applicationContext, "Seleccione que prefiere comer durante el día", Toast.LENGTH_SHORT, true).show()
                }else if(indexDieta == -1){
                    Toasty.warning(applicationContext, "Sigue actualmente alguna dieta?", Toast.LENGTH_SHORT, true).show()
                }else{
                    val hizoDieta = if (indexDieta == 0) 1 else 0
                    val parametros = JSONObject()
                    parametros.put("cant_comidas", spComida.selectedItem.toString().toInt())
                    parametros.put("cant_vasos", spVasos.selectedItem.toString().toInt())
                    parametros.put("comida_hambre", indexComidas +1)
                    parametros.put("hace_dieta", hizoDieta)
                    if(PROCESAR_AGREGAR){
                        actualizarPreferencia(parametros)
                    }
                }
            }
            lastClick = SystemClock.elapsedRealtime()

        }


    }
    fun actualizarPreferencia(parameters:JSONObject){
        PROCESAR_AGREGAR = false
        Log.e("myerror", parameters.toString())
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("persona_registrar_preferencia"),parameters,
            Response.Listener { response ->
                if(response!=null){
                    val success = response.getBoolean("success")
                    val message = response.getString("message")
                    if(success){
                        val rutina = response.getJSONObject("preferencia")
                        val datosPersona = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
                        val data = JSONObject(datosPersona)
                        data.put("preferencia", rutina)
                        sharedPref?.edit {
                            putString(VAR.PREF_DATA_USUARIO, data.toString())
                        }
                        Log.e("myerror", data.toString())
                        Toasty.success(applicationContext, message, Toast.LENGTH_SHORT, true).show()


                        val intent = Intent(applicationContext, EvaluacionActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

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
        val socketTimeout = 25000

        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        request.retryPolicy = policy

        requestQueue?.add(request)
    }

}
