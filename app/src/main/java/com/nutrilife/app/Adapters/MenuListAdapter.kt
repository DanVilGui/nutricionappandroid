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
import com.nutrilife.app.ViewHolders.MenuViewHolder

class MenuListAdapter(val act : Context, val list: List<String>)
    : RecyclerView.Adapter<MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MenuViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu:String = list[position]
        holder.bind(act,menu)
    }

    override fun getItemCount(): Int = list.size
}