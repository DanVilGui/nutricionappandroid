package com.nutrilife.app.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrilife.app.Clases.ClsDeporte
import com.nutrilife.app.Clases.ClsDietaHorario
import com.nutrilife.app.Clases.ClsMedida
import com.nutrilife.app.ViewHolders.DeporteViewHolder
import com.nutrilife.app.ViewHolders.DietaDetalleViewHolder
import com.nutrilife.app.ViewHolders.MedidaViewHolder
import com.nutrilife.app.ViewHolders.MenuViewHolder

class DietaDetalleListAdapter(val act : Context, val list: List<ClsDietaHorario>)
    : RecyclerView.Adapter<DietaDetalleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietaDetalleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DietaDetalleViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: DietaDetalleViewHolder, position: Int) {
        val detalle = list[position]
        var med = ""
        var cant = ""
        when(detalle.medida){
            "unidad" ->{
                med =  "un."
                cant = String.format("%.1f", detalle.cantidad)
            }
            "gramos"->{
                med = "gr."
                cant =  detalle.cantidad.toInt().toString()
            }
            "mililitros"->{
                med = "ml."
                cant =  detalle.cantidad.toInt().toString()
            }
        }

        holder.nMedida?.text = med

        holder.nCantidad?.text = cant
        holder.nProducto?.text = detalle.producto

    }

    override fun getItemCount(): Int = list.size

}