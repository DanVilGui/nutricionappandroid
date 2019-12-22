package com.nutrilife.app

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.nutrilife.app.Clases.VAR
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.sexo_edad.*
import org.json.JSONObject
import java.util.*


class SexoEdadActivity: AppCompatActivity() {

    var fechaNacimiento:String = ""
    var sharedPref: SharedPreferences? = null
    var PROCESAR_REGISTRAR = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sexo_edad)
        this.supportActionBar?.hide()
        val imagenMasculino:ImageView = findViewById(R.id.sexoMasculino)
        val imagenFemenino:ImageView = findViewById(R.id.sexoFemenino)
        val rbMasculino:RadioButton = findViewById(R.id.rbMasculino)
        val rbFemenino:RadioButton = findViewById(R.id.rbFemenino)
        val txtFechaNacimiento:EditText = findViewById(R.id.fecha_nacimiento)
        val btnCalendario:ImageView = findViewById(R.id.btnCalendario)
        val btnContinuar:Button = findViewById(R.id.btnContinuar)
         sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        val datosPersona = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
        var fechaNac:String? = null
        if(datosPersona!="") {
            val data = JSONObject(datosPersona)
            if(!data.isNull("fecha_nacimiento")){
                fechaNac = data.getString("fecha_nacimiento")
            }
            val sexo = data.getString("sexo")
            if(sexo == "M") rbMasculino.isChecked = true
            else rbFemenino.isChecked = true
        }

        if(fechaNac==null){
            val c = Calendar.getInstance()
            val anio = c.get(Calendar.YEAR)
            val mes = c.get(Calendar.MONTH)
            val dia = c.get(Calendar.DAY_OF_MONTH)
            DatePickerFragment.fecha = anio.toString() +"-"+ (mes+1).toString().padStart(2,'0') +"-" + dia
        }else{
            try {
                val arr = fechaNac.split("-")
                val year = arr[0].toInt()
                val month = arr[1].toInt()-1
                val day = arr[2].toInt()
                DatePickerFragment.fecha = year.toString() +"-"+
                        (month-1).toString().padStart(2,'0') +"-" + day
            }catch (ex:Exception){

            }
        }





        imagenMasculino.setOnClickListener {
            rbMasculino.isChecked = true
        }
        imagenFemenino.setOnClickListener {
            rbFemenino.isChecked = true
        }

        btnCalendario.setOnClickListener {
            val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val dia =  day.toString().padStart(2, '0')
                val mes = (month + 1).toString().padStart(2,'0')
                val selectedDate = dia+ " / " + mes + " / " + year
                DatePickerFragment.fecha = year.toString() +"-"+ mes +"-" + dia
                try {
                    txtFechaNacimiento.setError(null)
                }catch (ex:Exception){

                }
                txtFechaNacimiento.setText(selectedDate)
            })
            newFragment.show(supportFragmentManager, "datePicker")
        }

        btnContinuar?.setOnClickListener{
            if(PROCESAR_REGISTRAR){

                val sexo = if (rbFemenino.isChecked) "F" else   "M"
                registrarSexoEdad( sexo, DatePickerFragment.fecha )
            }

        }
    }

    fun mostrarActualizarMedidasActivity(){
        val intent = Intent(this, ActualizarMedidasActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    fun registrarSexoEdad(sexo:String, fechaNac:String){
        PROCESAR_REGISTRAR = false

        val parameters = JSONObject()
        parameters.put("sexo", sexo)
        parameters.put("fecha_nacimiento", fechaNac)
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("persona_cambiar_sexo_edad"),parameters,
            Response.Listener { response ->
                if(response!=null){
                    val success = response.getBoolean("success")
                    val message = response.getString("message")
                    if(success){
                        val datosPersona = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
                        val data = JSONObject(datosPersona)
                        data.put("fecha_nacimiento", fechaNac)
                        data.put("sexo",sexo)
                        sharedPref?.edit {
                            putString(VAR.PREF_DATA_USUARIO, data.toString())
                        }
                        Toasty.success(applicationContext, message, Toast.LENGTH_LONG, true).show()
                        mostrarActualizarMedidasActivity()
                    }else{
                        Toasty.warning(applicationContext, message, Toast.LENGTH_LONG, true).show()
                    }

                    PROCESAR_REGISTRAR = true
                }

            },
            Response.ErrorListener{
                try {
                    PROCESAR_REGISTRAR = true
                    Toasty.error(applicationContext, "Error de conexión.", Toast.LENGTH_LONG, true).show()
                    Log.e("myerror",  (it.message))
                    val nr = it.networkResponse
                    val r = String(nr.data)
                }catch (ex:Exception){
                    Log.e("myerror", ex.message.toString())

                }

            }) {
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String> =HashMap()
                params["TOKEN"] =  sharedPref?.getString("token", "")!!
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }

    class DatePickerFragment : DialogFragment() {

        private var listener: DatePickerDialog.OnDateSetListener? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val arr = DatePickerFragment.fecha.split("-")
            val year = arr[0].toInt()
            val month = arr[1].toInt()-1
            val day = arr[2].toInt()
            val datePickerDialog  = DatePickerDialog(activity!!, listener, year, month, day)
            datePickerDialog.datePicker.maxDate = Date().time
            return datePickerDialog

        }

        companion object {
            var fecha:String = ""

            fun newInstance(listener: DatePickerDialog.OnDateSetListener): DatePickerFragment {
                val fragment = DatePickerFragment()
                fragment.listener = listener
                return fragment
            }
        }

    }
}