package com.nutrilife.app.Clases

import bolts.Bolts
import java.util.*

data class ClsDietaBlock(
    var horario: ClsHorario ,
    var dietaHorario: List<ClsDietaHorario>
){
    var expandido:Boolean = false
    var pintar:Boolean = false
}