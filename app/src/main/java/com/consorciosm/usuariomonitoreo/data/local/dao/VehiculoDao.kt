package com.consorciosm.usuariomonitoreo.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.consorciosm.usuariomonitoreo.common.constants.Constants
import com.consorciosm.usuariomonitoreo.data.model.vehiculo.Carro

@Dao
interface VehiculoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCarro(carro: Carro)
    @Update
    fun updateCarro(carro: Carro)
    @Query("DELETE FROM ${Constants.NAME_TABLE_CARRO}")
    fun deleteCarro()
    @Query("SELECT * FROM ${Constants.NAME_TABLE_CARRO}" )
    fun selectCarro(): LiveData<Carro>
}