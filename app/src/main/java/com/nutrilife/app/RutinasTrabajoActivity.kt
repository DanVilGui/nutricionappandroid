package com.nutrilife.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.JsonReader
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.nutrilife.app.Clases.VAR
import es.dmoral.toasty.Toasty
import org.angmarch.views.NiceSpinner
import org.json.JSONObject
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList


class RutinasTrabajoActivity: AppCompatActivity() {

    var txtCasa:NiceSpinner? = null
    var txtLigero:NiceSpinner?=null
    var txtActivo:NiceSpinner?=null
    var txtMuyActivo:NiceSpinner?=null
    var radioGroup:RadioGroup? = null
    var listaSpinner:MutableList<NiceSpinner>? = null
    var listaRadioButtons:MutableList<RadioButton>? = null
    var sharedPref: SharedPreferences? = null

    var rutinaAnterior:JSONObject? = null
    var lastClick: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        setContentView(R.layout.rutina_trabajo)
        this.supportActionBar?.hide()
        radioGroup = findViewById(R.id.radioGroup)
        val rutinaStr =  sharedPref?.getString(VAR.PREF_TEMP_RUTINA,null)
        if(rutinaStr!=null){
            rutinaAnterior = JSONObject(rutinaStr)
        }
        val dataset: List<String> =
            LinkedList(asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))


        txtCasa = findViewById(R.id.sp_casa)
        txtCasa?.attachDataSource(dataset)

        txtLigero = findViewById(R.id.sp_ligero)
        txtLigero?.attachDataSource(dataset)

        txtActivo = findViewById(R.id.spActivo)
        txtActivo?.attachDataSource(dataset)

        txtMuyActivo = findViewById(R.id.spMuyActivo)
        txtMuyActivo?.attachDataSource(dataset)

        listaSpinner =  mutableListOf( txtCasa!!, txtLigero!!, txtActivo!!, txtMuyActivo!!)
        listaSpinner?.forEach {
            it.visibility = View.INVISIBLE
        }
        val rbCasa: RadioButton = findViewById(R.id.rbCasa)
        val rbLigero: RadioButton = findViewById(R.id.rbLigero)
        val rbActivo: RadioButton = findViewById(R.id.rbActivo)
        val rbMuyActivo: RadioButton = findViewById(R.id.rbMuyActivo)
        val rbNinguno: RadioButton = findViewById(R.id.rbNinguno)


        listaRadioButtons = mutableListOf( rbLigero, rbCasa, rbActivo, rbMuyActivo, rbNinguno)
        listaRadioButtons?.forEach {
            it.setOnClickListener {
                val copiaRb =  mutableListOf<RadioButton>()
                copiaRb.addAll(listaRadioButtons!!)
                copiaRb.remove(it)
                visiblidadSpinnerCercano(it, View.VISIBLE)
                copiaRb.forEach{
                    it.isChecked = false
                    visiblidadSpinnerCercano(it, View.INVISIBLE)
                }

            }
        }
        /*
            Cargar Rutina anterior
         */
        val datosPersona = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
        if(datosPersona!= ""){
            val data = JSONObject(datosPersona)
            if( !data.isNull("rutina") ){

                val rutina = data.getJSONObject("rutina")
                val trabajo_ligero =  rutina.getInt("trabaja_ligero")
                val trabajo_casa =  rutina.getInt("trabaja_casa")
                val trabajo_activo =  rutina.getInt("trabaja_activo")
                val trabajo_muyactivo =  rutina.getInt("trabaja_muyactivo")
                Log.e("myerror", rutina.toString())
                when (true){
                    trabajo_ligero!=0-> {
                        listaRadioButtons!![0].performClick()
                        txtLigero?.selectedIndex = dataset.indexOf(trabajo_ligero.toString())
                    }
                    trabajo_casa!=0-> {
                        listaRadioButtons!![1].performClick()
                        txtCasa?.selectedIndex = dataset.indexOf(trabajo_casa.toString())
                    }
                    trabajo_activo!=0-> {
                        listaRadioButtons!![2].performClick()
                        txtActivo?.selectedIndex = dataset.indexOf(trabajo_activo.toString())
                    }
                    trabajo_muyactivo!=0-> {
                        listaRadioButtons!![3].performClick()
                        txtMuyActivo?.selectedIndex = dataset.indexOf(trabajo_muyactivo.toString())
                    }
                    else->{
                        listaRadioButtons!![4].performClick()
                    }

                }
            }
        }


        val btnContinuar:Button = findViewById(R.id.btnContinuar)
        btnContinuar.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000){
                if(haySeleccion()){
                    guardarTrabajo()
                }else{
                    Toasty.error(applicationContext, "Seleccione una opción.", Toast.LENGTH_SHORT, true).show()
                }
            }
            lastClick = SystemClock.elapsedRealtime()

        }
    }


    fun guardarTrabajo(){
        if(rutinaAnterior!=null){
            listaRadioButtons?.forEach {
                if(it.isChecked){
                    when(it.id){

                        R.id.rbLigero->{
                            rutinaAnterior?.put("trabaja_ligero", txtLigero?.selectedItem.toString().toInt())
                        }
                        R.id.rbCasa->{
                            rutinaAnterior?.put("trabaja_casa", txtCasa?.selectedItem.toString().toInt())
                        }
                        R.id.rbActivo->{
                            rutinaAnterior?.put("trabaja_activo", txtActivo?.selectedItem.toString().toInt())
                        }
                        R.id.rbMuyActivo->{
                            rutinaAnterior?.put("trabaja_muyactivo", txtMuyActivo?.selectedItem.toString().toInt())
                        }
                        R.id.rbNinguno->{
                            rutinaAnterior?.put("trabaja", 0)
                        }

                    }
                }
            }

            sharedPref?.edit {
                putString(VAR.PREF_TEMP_RUTINA, rutinaAnterior?.toString())
            }

            val r = sharedPref?.getString(VAR.PREF_TEMP_RUTINA,null)
            if(r!=null){
                Log.e("myerror", JSONObject(r).toString() )
            }

            val intent = Intent(this, RutinasDeporteActivity::class.java)
            startActivity(intent)
        }

    }

    fun haySeleccion():Boolean{
        listaRadioButtons?.forEach {
            if(it.isChecked) return true
        }
        return false
    }
    fun visiblidadSpinnerCercano(radioButton: View, estado :Int){
        val contenedor :LinearLayout = radioButton.parent as LinearLayout
        val spinner:NiceSpinner? = contenedor.findViewWithTag("select")
        if(spinner!=null){
            spinner.visibility = estado
        }
    }


}
