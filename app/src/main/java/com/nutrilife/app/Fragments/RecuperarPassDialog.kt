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
import com.google.android.material.textfield.TextInputLayout
import com.nutrilife.app.Clases.ClsLoginTipo
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.Clases.Validar
import com.nutrilife.app.LoginCuentaActivity
import com.nutrilife.app.R
import es.dmoral.toasty.Toasty
import org.json.JSONObject


class RecuperarPassDialog: DialogFragment() {

    var txtCorreo:EditText? = null
    var txtCodigo:EditText? = null
    var txtPass:EditText? = null

    var lastClick:Long = 0
    var loadingDialog: Dialog? = null
    var tieneCodigo:CheckBox? = null
    var btnSolicitar:Button? = null
    var contenedorCodigo:LinearLayout ? =null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.recuperar_pass, container)
        txtCorreo = view.findViewById(R.id.correo)
        txtCodigo = view.findViewById(R.id.codigo)
        txtPass = view.findViewById(R.id.pass)

        tieneCodigo = view.findViewById(R.id.tengocodigo)
        contenedorCodigo = view.findViewById(R.id.codigocontenedor)
        cambioTieneCodigo()
        btnSolicitar = view.findViewById(R.id.btnSolicitar)

        tieneCodigo?.setOnCheckedChangeListener { buttonView, isChecked ->
            cambioTieneCodigo()
        }
        val btnClose:ImageView = view.findViewById(R.id.close)
        btnClose.setOnClickListener{
            dismiss()
        }


        btnSolicitar?.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000) {
                if(validarForm()){
                    recuperar()
                }
            }
            lastClick = SystemClock.elapsedRealtime()
        }
        return view
    }
    fun cambioTieneCodigo(){
        txtCodigo?.setText("")
        txtPass?.setText("")
        if(tieneCodigo!!.isChecked){
            contenedorCodigo?.visibility = View.VISIBLE
            btnSolicitar?.text = "CAMBIAR CONTRASEÑA"
        }else{
            contenedorCodigo?.visibility = View.GONE
            btnSolicitar?.text = "SOLICITAR CÓDIGO"
        }
    }
    fun validarForm():Boolean{
        if(Validar.vacio(txtCorreo)){
            Validar.txtErr(txtCorreo, "Ingrese correo electrónico" )
        }else if(! Validar.strEmail(txtCorreo)){
            Validar.txtErr(txtCorreo, "Correo inválido" )
        }
        else if(tieneCodigo!!.isChecked){
            if(Validar.vacio(txtCodigo)){
                Validar.txtErr(txtCodigo, "Ingrese su código" )
            }else if(Validar.vacio(txtPass)){
                Validar.txtErr(txtPass, "Ingrese su contraseña" )
            }else if(Validar.strMenorA(txtPass, 6)){
                Validar.txtErr(txtPass, "Ingrese al menos 6 caracteres." )
            }else return true
        }else return true

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
    fun recuperar(){
        val cambiarpass = tieneCodigo!!.isChecked
        mostrarEspereDialog()
        val correo = txtCorreo?.text?.trim().toString()
        val codigo = txtCodigo?.text?.trim().toString()
        val pass = txtPass?.text?.trim().toString()

        val parametros = JSONObject()

        parametros.put("correo", correo)
        if(tieneCodigo!!.isChecked){
            parametros.put("codigo", codigo)
            parametros.put("pass", pass)

        }

        val request = JsonObjectRequest(
            Request.Method.POST, VAR.url("recuperar_pass"), parametros,
            Response.Listener { response ->
                Log.e("myerror", response.toString())

                val success = response.getBoolean("success")
                val message = response.getString("message")
                if(success){
                    tieneCodigo!!.isChecked = true
                    Toasty.success(activity!!, message, Toast.LENGTH_SHORT, true).show()
                    if(cambiarpass){
                        val act :LoginCuentaActivity = activity as LoginCuentaActivity
                        act.txtCorreo!!.setText(correo)
                        dismiss()
                    }
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