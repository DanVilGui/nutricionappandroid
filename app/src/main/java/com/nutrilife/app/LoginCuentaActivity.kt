package com.nutrilife.app

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Clases.ClsPersona
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.Clases.Validar
import es.dmoral.toasty.Toasty
import org.json.JSONObject


class LoginCuentaActivity: AppCompatActivity() {
    var sharedPref: SharedPreferences? = null
    var lastClick: Long = 0
    var loadingDialog: Dialog? = null
    var txtCorreo:EditText? = null
    var txtContrasenia:EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_cuenta)
        this.title = "Ingreso con cuenta"
        // this.supportActionBar?.hide()
        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        val correo = sharedPref?.getString(VAR.PREF_CORREO, "")
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        txtCorreo = findViewById(R.id.correo)
        txtCorreo?.setText(correo)
        txtContrasenia = findViewById(R.id.contrasenia)
        val btnLogin:Button = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000) {
                val valido = validarForm()!=null
                if(valido){
                    accederCuenta()
                }
            }
            lastClick = SystemClock.elapsedRealtime()
        }
    }

    fun validarForm():Boolean?{
        if(Validar.vacio(txtCorreo)){
            Validar.txtErr(txtCorreo, "Ingrese correo electrónico" )
        }else if(! Validar.strEmail(txtCorreo)){
            Validar.txtErr(txtCorreo, "Correo inválido" )
        }else if(Validar.vacio(txtContrasenia)){
            Validar.txtErr(txtContrasenia, "Ingrese su contraseña" )
        }
        return true
    }

    fun mostrarEspereDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.loading_dialog)
        loadingDialog = builder.create()
        loadingDialog?.setCancelable(false)
        loadingDialog?.setCanceledOnTouchOutside(false)
        loadingDialog?.show()
    }
    fun accederCuenta(){
        mostrarEspereDialog()
        val parameters = JSONObject()
        val correo = txtCorreo!!.text?.trim().toString()
        parameters.put("correo", correo )
        parameters.put("contrasenia",  txtContrasenia?.text?.trim())
        Log.e("myerror", parameters.toString())
        val request = JsonObjectRequest(
            Request.Method.POST, VAR.url("acceder_cuenta"), parameters,
            Response.Listener { response ->

                val success = response.getBoolean("success")
                val message = response.getString("message")
                if(success){
                    val datosPersona = response.getJSONObject("datos")
                    sharedPref?.edit {
                        putString(VAR.PREF_TOKEN, datosPersona.getString("token"))
                        putString(VAR.PREF_DATA_USUARIO, datosPersona.toString())
                        putString(VAR.PREF_CAMBIARRUTINA, "")
                        putString(VAR.PREF_ADVERTENCIA, "")
                        putString(VAR.PREF_CORREO, correo)
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        loadingDialog?.dismiss()
                        finish()
                    }
                }else{
                    loadingDialog?.dismiss()
                    Toasty.warning(applicationContext, message, Toast.LENGTH_SHORT, true).show()
                }
            },
            Response.ErrorListener{
                try {
                    Toasty.error(applicationContext, "Error de conexión.", Toast.LENGTH_SHORT, true).show()
                    loadingDialog?.dismiss()
                    Log.e("myerror",  (it.message))
                    val nr = it.networkResponse
                    val r = String(nr.data)
                }catch (ex:Exception){
                    Log.e("myerror", ex.message.toString())

                }
            })


        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}