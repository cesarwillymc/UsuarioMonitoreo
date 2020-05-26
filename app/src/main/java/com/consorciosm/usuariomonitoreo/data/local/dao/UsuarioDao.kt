package com.consorciosm.usuariomonitoreo.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.consorciosm.usuariomonitoreo.data.model.Usuario
import com.consorciosm.usuariomonitoreo.common.constants.Constants

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsuario(usuario: Usuario)
    @Update
    fun updateUsuario(usuario: Usuario)
    @Query("DELETE FROM ${Constants.NAME_TABLE_USER}")
    fun deleteUsuario()
    @Query("SELECT * FROM ${Constants.NAME_TABLE_USER}" )
    fun selectUsuario():LiveData<Usuario>
}