package com.nutrilife.app.Clases

import android.content.SharedPreferences
import android.util.Patterns
import android.widget.EditText

class ClsLoginTipo {
    companion object{
        val NATIVO = 1
        val FACEBOOK = 2
        val GOOGLE = 3
    }
}
class ClsPersona(val idpersona: Int, val idloginTipo:Int, val nombres:String, val apellidos:String,
                 val email:String) {

    var contrasenia:String = ""
    constructor(id:Int, idtipo:Int, nom:String, ape:String, em:String, contrasenia:String )
            : this(id,idtipo,nom,ape,em) {
        this.contrasenia = contrasenia
    }

    fun registrarBasico():HashMap<String,Any?>{
        val params = HashMap<String,Any?>()
        params["nombres"] = this.nombres
        params["apellidos"] = this.apellidos
        params["idlogin_tipo"] = this.idloginTipo
        params["contrasenia"] = this.contrasenia
        params["correo"] = this.email
        return params
    }

}

class VAR {
    companion object {

        val url: String = "https://dvilchez.ovh/nutricion/ws/"
        var ext: String = ".php"
        var ACTION_ACTUALIZAR_DIETA = 1
        fun url(m: String): String {
            return url +  m + ext
        }

        val PRIVATE_MODE = 0
        val PREF_NAME = "nutrilife-app"
        val PREF_HOLA_ACTIVITY = "mostrar_hola_activity"
        val PREF_TOKEN  = "token"
        val PREF_DATA_USUARIO  = "datausuario"
        val PREF_TEMP_RUTINA= "rutina_temporal"
    }
}

class Validar {
    companion object {
        fun vacio(txt: EditText?): Boolean {
            return (txt!!.text.trim().isEmpty())
        }

        fun strMenorA(txt: EditText?,  t: Int): Boolean {
            return (txt!!.text.trim().length < t)
        }


        fun strSize(txt: EditText?, t: Int): Boolean {
            return (txt!!.text.trim().length == t)
        }

        fun strEmail(txt: EditText?): Boolean {
            val email = getString(txt!!)
            val pat = Patterns.EMAIL_ADDRESS
            return pat.matcher(email).matches()
        }

        fun getString(txt: EditText?): String {
            return txt!!.text.trim().toString()
        }

        fun txtErr(txt: EditText?, err: String) {
            txt!!.error = err
            txt.requestFocus()
        }


    }
}
