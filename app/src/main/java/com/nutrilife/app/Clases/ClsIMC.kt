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
            texto = "Tu índice de masa corporal indica desnutrición severa, este plan alimenticio " +
                    "te ayudará a mejorar tus hábitos de alimentación y, cubrirá tus necesidades calóricas." +
                    " Recuerda consultar con tu médico de confianza."
        }else if(imc<=16.99){
            estado = "DESNUTRICION II"
            texto = "Tu índice de masa corporal indica desnutrición en segundo grado, " +
                    "este plan alimenticio te ayudará a mejorar tus hábitos de alimentación y, " +
                    "cubrirá tus necesidades calóricas. Recuerda consultar con tu médico de confianza."
        }else if(imc<=18.49){
            estado = "DESNUTRICION I"
            texto = "Tu índice de masa corporal indica desnutrición en primer grado, este plan alimenticio" +
                    " te ayudará a mejorar tus hábitos de alimentación y, cubrirá tus necesidades calóricas." +
                    " Recuerda consultar con tu médico de confianza."
        }
        else if(imc<=24.99){
            estado = "NORMAL"
            texto = "Tu índice de masa corporal se encuentra en el rango normal, este plan " +
                    "alimenticio te ayudará a mantenerte saludable."
        }
        else if(imc<=29.99){
            estado = "SOBREPESO"
            texto = "Tu índice de masa corporal indica sobrepeso, este plan alimenticio te ayudará " +
                    "a mejorar tus hábitos de alimentación y así mantenerte saludable. " +
                    "Recuerda consultar con tu médico de confianza."
        }
        else if(imc<=34.49){
            estado = "OBESIDAD I"
            texto = "Tu índice de masa corporal muestra obesidad en primer grado, este plan " +
                    "alimenticio te ayudará a mejorar tus hábitos de alimentación y, por lo tanto, " +
                    "tu salud. Recuerda consultar con tu médico de confianza."
        }
        else if(imc<=39.99){
            estado = "OBESIDAD II"
            texto = "Tu índice de masa corporal muestra obesidad en segundo grado, este plan " +
                    "alimenticio te ayudará a mejorar tus hábitos de alimentación y, por lo tanto, " +
                    "tu salud. Recuerda consultar con tu médico de confianza."
        }
        else {
            estado = "OBESIDAD III"
            texto = "Tu índice de masa corporal muestra obesidad mórbida, este plan alimenticio " +
                    "te ayudará a mejorar tus hábitos de alimentación y, por lo tanto, tu salud." +
                    " Recuerda consultar con tu médico de confianza."
        }
    }
}
