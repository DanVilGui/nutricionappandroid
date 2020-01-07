package com.nutrilife.app.Fragments

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.nutrilife.app.R


class AdvertenciaDietaDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.dialog_advertencia, container)
        val btnClose:ImageView = view.findViewById(R.id.close)
        btnClose.setOnClickListener{
            dismiss()
        }
        return view
    }


    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {

    }
}