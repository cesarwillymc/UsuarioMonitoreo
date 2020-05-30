package com.consorciosm.usuariomonitoreo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.consorciosm.usuariomonitoreo.common.utils.Resource
import com.consorciosm.usuariomonitoreo.data.model.Usuario
import com.consorciosm.usuariomonitoreo.data.model.vehiculo.Carro
import com.consorciosm.usuariomonitoreo.data.network.repository.MainRepository

class ViewModelMain(private val repo: MainRepository) :ViewModel(){
    val getLoggetUser = repo.getUser()
    fun deleteUser()= repo.deleteUser()
    fun updateUserDB(item: Usuario)=repo.updateUserAppDb(item)
    val getCarroData=repo.getCarroVinculado()
    fun deleteCarro()= repo.deleteCarro()
    fun getVehiculoVinculado():LiveData<Resource<Carro>> = liveData {
        emit(Resource.Loading())
        try {
            val dato = repo.getVehiculoVinculado()
            if (dato!=null){
                repo.saveCarro(dato)
            }else{
                repo.deleteCarro()
            }
            emit(Resource.Success(dato))
        }catch (e:Exception){
            emit(Resource.Failure(e))
        }
    }
}