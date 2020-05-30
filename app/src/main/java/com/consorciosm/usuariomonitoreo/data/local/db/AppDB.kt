package com.consorciosm.usuariomonitoreo.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.consorciosm.usuariomonitoreo.data.model.Usuario
import com.consorciosm.usuariomonitoreo.common.constants.Constants
import com.consorciosm.usuariomonitoreo.data.local.dao.UsuarioDao
import com.consorciosm.usuariomonitoreo.data.local.dao.VehiculoDao
import com.consorciosm.usuariomonitoreo.data.model.vehiculo.Carro

@Database(entities = [Usuario::class,Carro::class],version = 6)
abstract class AppDB:RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun vehiculoDao(): VehiculoDao
    companion object{
        @Volatile
        private var INSTANCE:AppDB?=null
        private val LOCK= Any()
        operator fun invoke(context: Context)= INSTANCE?: synchronized(LOCK){
            INSTANCE?:buildDatabase(context)
        }
        private fun buildDatabase(context: Context)= Room.databaseBuilder(context,AppDB::class.java, Constants.NAME_DATABASE)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()


    }
}