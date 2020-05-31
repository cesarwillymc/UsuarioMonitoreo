package com.consorciosm.usuariomonitoreo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.consorciosm.usuariomonitoreo.common.utils.Resource
import com.consorciosm.usuariomonitoreo.common.utils.detectar_formato
import com.consorciosm.usuariomonitoreo.data.model.ParteDiario
import com.consorciosm.usuariomonitoreo.data.model.Usuario
import com.consorciosm.usuariomonitoreo.data.model.vehiculo.Carro
import com.consorciosm.usuariomonitoreo.data.network.repository.MainRepository
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

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
    fun subirParteData(parteDiario: ParteDiario, file: File, name:String):LiveData<Resource<Unit>> = liveData {
        emit(Resource.Loading())
        try {
            val dato = repo.sendParteSave(parteDiario)
            val nameFile = RequestBody.create(MediaType.parse("text/plain"),name )
            val archivo=guardarFotoEnArchivo(name,file)
            repo.sendImgCombustible(archivo!!,nameFile,dato.parteId)
            emit(Resource.Success(Unit))
        }catch (e:Exception){
            emit(Resource.Failure(e))
        }
    }
    private fun guardarFotoEnArchivo(name: String, file: File): MultipartBody.Part? {
        var body: MultipartBody.Part? = null
        if (detectar_formato(file.path) == "ninguno") {
            throw Exception("Formato no valido de imagen")
        } else {
            val requestFile: RequestBody = RequestBody.create(
                MediaType.parse("image/" + detectar_formato(file.path)),
                file
            )
            body = MultipartBody.Part.createFormData(name,file.name, requestFile)
        }
        return body
    }
}