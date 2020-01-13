package com.nutrilife.app.Clases

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class ClsIMC(val peso:Double,val estatura:Double){
    var imc:Double = 0.0
    var estado:String = ""
    var texto:String = ""

    fun calcular(){
        imc = (peso/(estatura*estatura))
        val format = DecimalFormat("#.#", DecimalFormatSymbols(Locale.US))
        imc =  format.format(imc).toDouble()

        if(imc<16){
            estado = "DESNUTRICION III"
            texto = ""
        }else if(imc<=16.99){
            estado = "DESNUTRICION II"
            texto = ""
        }else if(imc<=18.49){
            estado = "DESNUTRICION I"
            texto = ""
        }
        else if(imc<=24.99){
            estado = "NORMAL"
            texto = ""
        }
        else if(imc<=29.99){
            estado = "SOBREPESO"
            texto = ""
        }
        else if(imc<=34.49){
            estado = "OBESIDAD I"
            texto = ""
        }
        else if(imc<=39.99){
            estado = "OBESIDAD II"
            texto = ""
        }
        else {
            estado = "OBESIDAD III"
            texto = ""
        }
    }
}
