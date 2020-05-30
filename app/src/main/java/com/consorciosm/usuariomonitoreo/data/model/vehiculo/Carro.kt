package com.consorciosm.usuariomonitoreo.data.model.vehiculo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.consorciosm.usuariomonitoreo.common.constants.Constants

@Entity(tableName = Constants.NAME_TABLE_CARRO)
data class Carro(
    val _id: String="test",
    val anotaciones: String="test",
    val color: Int=-262162,
    val conductor: String="test",
    val estado: String="test",
    val imagenCarro: String="test",
    val isAsigned: Boolean=false,
    val kilometros: Int=0,
    val marca: String="test",
    val modelo: String="test",
    val nivelCombustible: Int=700,
    val numeroMotor: String="test",
    val numeroPlaca: String="test",
    val numeroSerie: String="test",
    val numeroVin: String="test",
    val placaAnterior: String="test",
    val placaVigente: String="test",
    val registerDate: String="test",
    val sede: String="test",
    @PrimaryKey(autoGenerate = false)
    val id:Int=0
)