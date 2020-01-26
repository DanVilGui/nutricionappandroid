package com.nutrilife.app.Fragments

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.R


class AdvertenciaDietaDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.dialog_advertencia, container)
        val btnClose:ImageView = view.findViewById(R.id.close)
        val sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        btnClose.setOnClickListener{
            sharedPref?.edit {
                putString(VAR.PREF_ADVERTENCIA, "")
            }
            dismiss()
        }
        return view
    }


    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {

    }
}