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
import com.nutrilife.app.R
import org.angmarch.views.NiceSpinner

class DeporteViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_deporte, parent, false)) {

    private var nTxtDeporte: TextView? = null
    private var nSpinner: NiceSpinner? = null
    init {
        nTxtDeporte = itemView.findViewById(R.id.txtDeporte)
        nSpinner = itemView.findViewById(R.id.spDeporte)
    }

    fun bind(act: Context, deporte: ClsDeporte) {
        nTxtDeporte?.text = deporte.texto
        nSpinner?.attachDataSource(deporte.source)
        nSpinner?.selectedIndex = deporte.source.indexOf(deporte.horas.toString())
        nSpinner?.setOnSpinnerItemSelectedListener { parent, view, position, id ->
            deporte.horas = parent.getItemAtPosition(position).toString().toInt()
        }
    }

}