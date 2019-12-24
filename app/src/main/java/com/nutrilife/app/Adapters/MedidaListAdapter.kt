package com.nutrilife.app.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrilife.app.Clases.ClsDeporte
import com.nutrilife.app.Clases.ClsMedida
import com.nutrilife.app.ViewHolders.DeporteViewHolder
import com.nutrilife.app.ViewHolders.MedidaViewHolder

class MedidaListAdapter(val act : Context, val list: List<ClsMedida>)
    : RecyclerView.Adapter<MedidaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedidaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MedidaViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MedidaViewHolder, position: Int) {
        val medida:ClsMedida = list[position]
        holder.bind(act,medida)
        var titulo = ""
        if(position == 0){
           titulo = "Tus datos iniciales"
        }else{
           titulo = "Control "+ position.toString()
        }
        holder.nTitulo?.text = titulo
    }

    override fun getItemCount(): Int = list.size
}