package com.nutrilife.app.ViewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nutrilife.app.R

class DietaBlockViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_horario, parent, false)) {
    var nContenedor: LinearLayout? =null

    var nExpandible: LinearLayout? =null
    var nContenido: LinearLayout? =null
    var nTitulo:TextView? = null
    var nDietasHorario:RecyclerView ? =null
    var nIconMostrar:ImageView? = null
    init {
        nContenedor = itemView.findViewById(R.id.contenedor)
        nExpandible = itemView.findViewById(R.id.expandible)
        nContenido = itemView.findViewById(R.id.contenido)
        nTitulo = itemView.findViewById(R.id.titulo)
        nDietasHorario = itemView.findViewById(R.id.dietahorario)
        nIconMostrar = itemView.findViewById(R.id.iconmostrar)
    }

}