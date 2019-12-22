package com.nutrilife.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import es.dmoral.toasty.Toasty
import org.angmarch.views.NiceSpinner
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rutina_trabajo)
        this.supportActionBar?.hide()
        radioGroup = findViewById(R.id.radioGroup)

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


        listaRadioButtons = mutableListOf(rbCasa, rbLigero, rbActivo, rbMuyActivo, rbNinguno)
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


    }

    fun visiblidadSpinnerCercano(radioButton: View, estado :Int){
        val contenedor :LinearLayout = radioButton.parent as LinearLayout
        val spinner:NiceSpinner? = contenedor.findViewWithTag("select")
        if(spinner!=null){
            spinner.visibility = estado
        }
    }
}
