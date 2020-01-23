package com.nutrilife.app.ViewHolders

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nutrilife.app.R
import com.sackcentury.shinebuttonlib.ShineButton
import org.angmarch.views.NiceSpinner

class TipViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_post_resumen, parent, false)) {

    var nTitulo:TextView? =null
    var nTexto:TextView? =null
    var nImagen:ImageView? =null
    var nContador:TextView? =null
    var likeButton: ShineButton? =null
    init {
        nTitulo = itemView.findViewById(R.id.titulo)
        nTexto = itemView.findViewById(R.id.texto)
        nImagen = itemView.findViewById(R.id.imagen)
        nContador = itemView.findViewById(R.id.contador)
        likeButton = itemView.findViewById(R.id.likebutton)
    }

    fun cambiarContador(cant:Int):String{

        var text = "0"
        val k = 1000
        val m = 1000000
        if(cant<k){
            return cant.toString()
        }

        if(cant < m){
            val t:Double = 1.0* cant/k
            val round = String.format("%.1f", t)
            if(round.endsWith(".0")){
                return t.toInt().toString()+" K"
            }else{
                return "$round K"
            }
        }

        val t:Double = 1.0* cant/m
        val round = String.format("%.1f", t)
        if(round.endsWith(".0")){
            return t.toInt().toString()+" M"
        }else{
            return "$round M"
        }
    }


}