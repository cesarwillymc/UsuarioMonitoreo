package com.consorciosm.usuariomonitoreo.data.model

data class PuntosFirebase(
    var latitude:Double,
    var longitude:Double,
    var state:Boolean,
    var color:Int,
    var placa:String,
    var id:String,
    var latAnterior:Double,
    var latPosterior:Double
)