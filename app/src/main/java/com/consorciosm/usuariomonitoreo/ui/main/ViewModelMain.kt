package com.consorciosm.usuariomonitoreo.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.consorciosm.usuariomonitoreo.common.utils.Resource
import com.consorciosm.usuariomonitoreo.common.utils.detectar_formato
import com.consorciosm.usuariomonitoreo.data.model.*
import com.consorciosm.usuariomonitoreo.data.model.vehiculo.Carro
import com.consorciosm.usuariomonitoreo.data.network.repository.MainRepository
import kotlinx.coroutines.flow.collect
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

    val obtenerNotificacionesCantidad= liveData {
        try{
            repo.obtenerNotificacionesCantidad().collect {
                emit(it)
            }
        }catch (e:Exception){
            Log.e("error",e.message)
        }
    }
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
    fun subirParteData(parteDiario: Parte, file: File, name:String):LiveData<Resource<Unit>> = liveData {
        Log.e("CREATE", parteDiario.toString())
        emit(Resource.Loading())
        try {
            val dato = repo.sendParteSave(parteDiario)
            val nameFile = RequestBody.create(MediaType.parse("text/plain"),name )
            val archivo=guardarFotoEnArchivo(name,file)
            repo.sendImgCombustible(archivo!!,nameFile,dato.message)
            emit(Resource.Success(Unit))
        }catch (e:Exception){
            emit(Resource.Failure(e))
        }
    }
    fun updateParteData(parteDiario: Parte):LiveData<Resource<Unit>> = liveData {
        Log.e("update", parteDiario.toString())
        emit(Resource.Loading())
        try {
            val dato = repo.sendParteSave(parteDiario)
            emit(Resource.Success(Unit))
        }catch (e:Exception){
            emit(Resource.Failure(e))
        }
    }
    fun getparte():LiveData<Resource<ParteDiario>> = liveData {
        emit(Resource.Loading())
        try {
            val dato = repo.getParteData()
            emit(Resource.Success(dato as ParteDiario))
        }catch (e:Exception){
            emit(Resource.Failure(e))
        }
    }
    fun obtenerParteKilometraje():LiveData<Resource<ParteDiario>> = liveData {
        emit(Resource.Loading())
        try {
            val dato = repo.obtenerParteKilometraje()
            emit(Resource.Success(dato as ParteDiario))
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

    fun getListNotificaciones(pagina:Int):LiveData<Resource<List<NotificacionesList>>> = liveData {
        emit(Resource.Loading())
        try{
            val dato=repo.getListNotificaciones(pagina)
            emit(Resource.Success(dato))
        }catch (e:Exception){
            emit(Resource.Failure(e) )
        }
    }
    fun getNotificationById(id:String):LiveData<Resource<OrdenProgramada>> = liveData {
        emit(Resource.Loading())
        try{
            val dato=repo.getListNotificacionesById(id)
            emit(Resource.Success(dato))
        }catch (e:Exception){
            emit(Resource.Failure(e) )
        }
    }
}