package com.nutrilife.app.ViewHolders

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.nutrilife.app.Clases.ClsDeporte
import com.nutrilife.app.Clases.ClsDietaBlock
import com.nutrilife.app.Clases.ClsMedida
import com.nutrilife.app.R
import org.angmarch.views.NiceSpinner

class DietaBlockViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_horario, parent, false)) {

    var nContenedor: LinearLayout? =null
    var nContenido: LinearLayout? =null
    var nTitulo:TextView? = null
    var nDietasHorario:RecyclerView ? =null
    init {
        nContenedor = itemView.findViewById(R.id.expandible)
        nContenido = itemView.findViewById(R.id.contenido)
        nTitulo = itemView.findViewById(R.id.titulo)
        nDietasHorario = itemView.findViewById(R.id.dietahorario)
    }

}