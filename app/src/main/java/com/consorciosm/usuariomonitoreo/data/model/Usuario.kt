package com.consorciosm.usuariomonitoreo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.consorciosm.usuariomonitoreo.common.constants.Constants

@Entity(tableName = Constants.NAME_TABLE_USER)
data class Usuario(
    @PrimaryKey(autoGenerate = false)
    var _id:String,
    var registerDate:String,
    var role:String,
    var accountActive:Boolean,
    var dni:String,
    var nombres:String,
    var apellidos:String,
    var telefono:String,
    var direccion:String,
    var telefonoReferenciaA:String,
    var telefonoReferenciaB:String,
    var isAsigned:Boolean
)