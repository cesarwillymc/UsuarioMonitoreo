package com.consorciosm.usuariomonitoreo.ui.main

import androidx.lifecycle.ViewModel
import com.consorciosm.usuariomonitoreo.data.model.Usuario
import com.consorciosm.usuariomonitoreo.data.network.repository.MainRepository

class ViewModelMain(private val repo: MainRepository) :ViewModel(){
    val getLoggetUser = repo.getUser()
    fun deleteUser()= repo.deleteUser()
    fun updateUserDB(item: Usuario)=repo.updateUserAppDb(item)
}