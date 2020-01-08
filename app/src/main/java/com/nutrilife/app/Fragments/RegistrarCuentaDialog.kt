package com.nutrilife.app.Fragments

import android.app.Dialog
import android.media.Image
import android.os.Bundle
import android.os.SystemClock
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Clases.ClsLoginTipo
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.Clases.Validar
import com.nutrilife.app.LoginCuentaActivity
import com.nutrilife.app.R
import es.dmoral.toasty.Toasty
import org.json.JSONObject


class RegistrarCuentaDialog: DialogFragment() {

    var txtNombre:EditText? = null
    var txtApellido:EditText? = null
    var txtCorreo:EditText? = null
    var txtContrasenia:EditText? = null
    var lastClick:Long = 0
    var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.registrar_cuenta, container)
        txtNombre = view.findViewById(R.id.nombre)
        txtApellido = view.findViewById(R.id.apellido)
        txtCorreo = view.findViewById(R.id.correo)
        txtContrasenia = view.findViewById(R.id.contrasenia)
        txtNombre!!.filters += InputFilter.AllCaps()
        txtApellido!!.filters += InputFilter.AllCaps()

        val btnClose:ImageView = view.findViewById(R.id.close)
        btnClose.setOnClickListener{
            dismiss()
        }

        val tengoCuenta:TextView = view.findViewById(R.id.tengocuenta)
        tengoCuenta.setOnClickListener {
            dismiss()
        }
        val btnRegistrar:Button = view.findViewById(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000) {
                if(validarForm()){
                    registrar()
                }
            }
            lastClick = SystemClock.elapsedRealtime()
        }

        return view
    }

    fun validarForm():Boolean{
        if(Validar.vacio(txtNombre)){
            Validar.txtErr(txtNombre, "Ingrese su nombre" )
        }else if(Validar.vacio(txtApellido)){
            Validar.txtErr(txtApellido, "Ingrese su apellido" )
        }else if(Validar.vacio(txtCorreo)){
            Validar.txtErr(txtCorreo, "Ingrese correo electrónico" )
        }else if(! Validar.strEmail(txtCorreo)){
            Validar.txtErr(txtCorreo, "Correo inválido" )
        }else if(Validar.vacio(txtContrasenia)){
            Validar.txtErr(txtContrasenia, "Ingrese su contraseña" )
        }else if(Validar.strMenorA(txtContrasenia,6)){
            Validar.txtErr(txtContrasenia, "Al menos 6 carácteres" )
        }
        else return true
        return false
    }
    fun mostrarEspereDialog(){
        val builder = AlertDialog.Builder(activity!!)
        builder.setView(R.layout.loading_dialog)
        loadingDialog = builder.create()
        loadingDialog?.setCancelable(false)
        loadingDialog?.setCanceledOnTouchOutside(false)
        loadingDialog?.show()
    }
    fun registrar(){
        mostrarEspereDialog()
        val nombre = txtNombre?.text?.trim().toString()
        val apellido = txtApellido?.text?.trim().toString()
        val correo = txtCorreo?.text?.trim().toString()
        val contrasenia = txtContrasenia?.text?.trim().toString()

        val parametros = JSONObject()
        parametros.put("nombres", nombre)
        parametros.put("apellidos", apellido)
        parametros.put("correo", correo)
        parametros.put("contrasenia", contrasenia)
        parametros.put("idlogin_tipo", ClsLoginTipo.NATIVO)

        val request = JsonObjectRequest(
            Request.Method.POST, VAR.url("registrar"), parametros,
            Response.Listener { response ->
                Log.e("myerror", response.toString())

                val success = response.getBoolean("success")
                val message = response.getString("message")
                if(success){
                    val act :LoginCuentaActivity = activity as LoginCuentaActivity
                    act.txtCorreo!!.setText(correo)
                    Toasty.success(activity!!, message, Toast.LENGTH_SHORT, true).show()
                    dismiss()
                }else{
                    Toasty.warning(activity!!, message, Toast.LENGTH_SHORT, true).show()
                }
                loadingDialog?.dismiss()
            },
            Response.ErrorListener{
                try {
                    Toasty.error(activity!!, "Error de conexión.", Toast.LENGTH_SHORT, true).show()
                    loadingDialog?.dismiss()
                    Log.e("myerror",  (it.message))
                    val nr = it.networkResponse
                    val r = String(nr.data)
                }catch (ex:Exception){
                    Log.e("myerror", ex.message.toString())

                }
            })


        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(request)
    }
}