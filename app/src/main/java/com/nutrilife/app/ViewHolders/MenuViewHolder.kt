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

class MenuViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_menu, parent, false)) {

     var nTitulo:TextView? =null

    init {
        nTitulo = itemView.findViewById(R.id.titulo)
    }

    fun bind(act: Context,  menu: String) {
        nTitulo?.text = menu
    }

}