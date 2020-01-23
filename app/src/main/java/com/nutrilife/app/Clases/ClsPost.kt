package com.nutrilife.app.Clases

data class ClsPost(
    var idpost:Int ,
    var titulo:String ,
    var texto:String,
    var resumen:String,
    var imagen:String,
    var likes:Int
){
    var liked = false

    var cargoImagen =false

}