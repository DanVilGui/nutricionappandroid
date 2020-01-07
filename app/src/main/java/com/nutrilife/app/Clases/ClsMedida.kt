package com.nutrilife.app.Clases

data class ClsMedida(val idmedida:Int, val idpersona:Int,val peso:Double, val medida:Double,
                val cintura:Int, val cadera:Int, val fecha:String ) {

    var imc:Double = 0.0

    fun getEstado():String{
        val clsIMC = ClsIMC(peso, medida)
        clsIMC.calcular()
        imc = clsIMC.imc
        return clsIMC.estado
    }

}
