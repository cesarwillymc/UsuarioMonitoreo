package com.consorciosm.usuariomonitoreo.data.model

import com.consorciosm.usuariomonitoreo.data.model.vehiculo.Carro


data class ParteDiario(
    val parte:Parte,
    val partePendiente:Boolean,
    val kilometraje:String
)
