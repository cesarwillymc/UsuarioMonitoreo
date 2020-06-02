package com.consorciosm.usuariomonitoreo.data.network.repository

import com.consorciosm.usuariomonitoreo.common.constants.Constants.PREF_ID_USER
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.getSomeStringValue
import com.consorciosm.usuariomonitoreo.common.utils.SafeApiRequest
import com.consorciosm.usuariomonitoreo.data.model.Usuario
import com.consorciosm.usuariomonitoreo.data.network.retrofit.ApiRetrofitKey
import com.consorciosm.usuariomonitoreo.data.local.db.AppDB
import com.consorciosm.usuariomonitoreo.data.model.Parte
import com.consorciosm.usuariomonitoreo.data.model.ParteDiario
import com.consorciosm.usuariomonitoreo.data.model.ResponseGeneral
import com.consorciosm.usuariomonitoreo.data.model.vehiculo.Carro
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainRepository (
    private val db: AppDB,
    private val api: ApiRetrofitKey,
    private val firebase: FirebaseFirestore
): SafeApiRequest() {
    fun deleteUser()= db.usuarioDao().deleteUsuario()
    fun updateUserAppDb(perfilUsuario: Usuario)=db.usuarioDao().updateUsuario(perfilUsuario)
    fun getUser()=db.usuarioDao().selectUsuario()
    suspend fun obtenerNotificacionesCantidad(): Flow<Int> = callbackFlow {
        val conexion=firebase.collection("notify").document(getSomeStringValue(PREF_ID_USER)!!).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException==null){
                if (documentSnapshot!!.exists()){
                    val dato=documentSnapshot!!.getLong("cantidad")!!.toInt()
                    offer(dato)
                }else{
                    offer(0)
                }


            }else{
                channel.close(Exception("Error al traer datos, revisa tu conexion"))
            }
        }
        awaitClose { conexion.remove() }
    }
    suspend fun DecrementValue(id:String){
        firebase.collection("notify").document(id).update("cantidad", FieldValue.increment(1))
    }
    fun getCarroVinculado()= db.vehiculoDao().selectCarro()
    fun deleteCarro()=db.vehiculoDao().deleteCarro()
    fun saveCarro(carro:Carro)=db.vehiculoDao().insertCarro(carro)

    suspend fun getVehiculoVinculado()= apiRequest { api.getVehiculoVinculado() }
    suspend fun sendParteSave(parteDiario: Parte)=apiRequest { api.sendParte(parteDiario) }
    suspend fun sendImgCombustible(file: MultipartBody.Part, name: RequestBody, key:String?=null): ResponseGeneral {
        return apiRequest { api.createImgConbustible(file,name,key) }
    }
    suspend fun getParteData() = apiRequest { api.obtenerParte() }

    suspend fun getListNotificaciones(pagina:Int)= apiRequest { api.getListNotificaciones(pagina) }
    suspend fun getListNotificacionesById(id:String)= apiRequest { api.getListNotificacionesById(id) }
}