package com.nutrilife.app.Clases


data class ClsHorario(
    var idhorario:Int ,
    var nombre:String
){
    var minHora = ""
    companion object{
        fun indicadorHora(hora:String):Int{
            val a =hora.split(":")
            val hora = a[0].toInt()
            val min = a[1].toInt()
            return hora*60 + min
        }
        var tolerancia = 30*60 // 30 minutos
        val horariosRango: Array<String> = arrayOf( "06:00", "10:00", "13:30", "17:00", "21:00")
    }
}

