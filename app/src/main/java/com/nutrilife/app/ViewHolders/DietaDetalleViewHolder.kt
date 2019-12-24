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

class DietaDetalleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_dieta_detalle, parent, false)) {

     var nCantidad:TextView? =null
    var nMedida:TextView? =null
    var nProducto:TextView? =null

    init {
        nCantidad = itemView.findViewById(R.id.cantidad)
        nMedida = itemView.findViewById(R.id.medida)
        nProducto = itemView.findViewById(R.id.producto)

    }


}