package com.nutrilife.app.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrilife.app.Clases.ClsDeporte
import com.nutrilife.app.ViewHolders.DeporteViewHolder

class DeporteListAdapter(val act : Context, val list: List<ClsDeporte>)
    : RecyclerView.Adapter<DeporteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeporteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DeporteViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: DeporteViewHolder, position: Int) {
        val deporte:ClsDeporte = list[position]
        holder.bind(act,deporte)

    }

    override fun getItemCount(): Int = list.size
}