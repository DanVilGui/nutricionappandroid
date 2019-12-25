package com.nutrilife.app.Adapters

import android.app.Activity
import android.content.Context
import androidx.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import com.nutrilife.app.Clases.ClsDeporte
import com.nutrilife.app.Clases.ClsDietaBlock
import com.nutrilife.app.Clases.ClsMedida
import com.nutrilife.app.R
import com.nutrilife.app.ViewHolders.DeporteViewHolder
import com.nutrilife.app.ViewHolders.DietaBlockViewHolder
import com.nutrilife.app.ViewHolders.MedidaViewHolder
import java.util.*

class DietaBlockListAdapter(val act : Context, val list: List<ClsDietaBlock>)
    : RecyclerView.Adapter<DietaBlockViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietaBlockViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DietaBlockViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: DietaBlockViewHolder, position: Int) {
        val dieta:ClsDietaBlock = list[position]
        if(dieta.expandido){
           // holder.nIconMostrar?.setImageResource(R.drawable.arrow_up)
            TransitionManager.beginDelayedTransition(holder.nContenedor!!, AutoTransition() )
            holder.nContenido?.visibility = View.VISIBLE
        }else{
            holder.nIconMostrar?.setImageResource(R.drawable.arrow_down)
            TransitionManager.beginDelayedTransition(holder.nContenedor!!, AutoTransition() )
            holder.nContenido?.visibility = View.GONE
        }
        holder.nTitulo?.text = dieta.horario.nombre
        holder.nContenedor?.setOnClickListener {
            if(dieta.expandido){
                dieta.expandido = false
            }else{
                dieta.expandido = true
                list.forEachIndexed { index, clsDietaBlock ->
                    if(index!= position) clsDietaBlock.expandido = false
                }
                notifyDataSetChanged()
            }
        }

        val dietahorario = dieta.dietaHorario

        val adaptador = DietaDetalleListAdapter(act,dietahorario)
        holder.nDietasHorario?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }

    }



    override fun getItemCount(): Int = list.size
}