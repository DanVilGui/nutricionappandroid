package com.nutrilife.app.ViewHolders

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nutrilife.app.Clases.ClsDeporte
import com.nutrilife.app.Clases.ClsMedida
import com.nutrilife.app.R
import org.angmarch.views.NiceSpinner

class MedidaViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_medida, parent, false)) {

     var nTitulo:TextView? =null
     var nPeso:TextView? = null
     var nAltura: TextView? = null
     var nIMC: TextView? = null
     var nEstado: TextView? = null
     var nCintura: TextView? = null
     var nCadera: TextView? = null

    init {
        nTitulo = itemView.findViewById(R.id.titulo)
        nPeso = itemView.findViewById(R.id.peso)
        nAltura = itemView.findViewById(R.id.altura)
        nIMC = itemView.findViewById(R.id.imc)
        nEstado = itemView.findViewById(R.id.estado)
        nCintura = itemView.findViewById(R.id.cintura)
        nCadera = itemView.findViewById(R.id.cadera)
    }

    fun bind(act: Context,  medida: ClsMedida) {
        nPeso?.text = medida.peso.toString() +" kg"
        nAltura?.text = medida.medida.toString() +" m"
        nEstado?.text = medida.getEstado()
        nCintura?.text = medida.cintura.toString() +" cm"
        nCadera?.text = medida.cadera.toString() + " cm"

    }

}