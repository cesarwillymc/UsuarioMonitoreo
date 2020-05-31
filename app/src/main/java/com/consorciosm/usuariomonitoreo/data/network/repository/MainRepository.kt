package com.consorciosm.usuariomonitoreo.data.network.repository

import com.consorciosm.usuariomonitoreo.common.utils.SafeApiRequest
import com.consorciosm.usuariomonitoreo.data.model.Usuario
import com.consorciosm.usuariomonitoreo.data.network.retrofit.ApiRetrofitKey
import com.consorciosm.usuariomonitoreo.data.local.db.AppDB
import com.consorciosm.usuariomonitoreo.data.model.ParteDiario
import com.consorciosm.usuariomonitoreo.data.model.ResponseGeneral
import com.consorciosm.usuariomonitoreo.data.model.vehiculo.Carro
import com.google.firebase.firestore.FirebaseFirestore
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

    fun getCarroVinculado()= db.vehiculoDao().selectCarro()
    fun deleteCarro()=db.vehiculoDao().deleteCarro()
    fun saveCarro(carro:Carro)=db.vehiculoDao().insertCarro(carro)

    suspend fun getVehiculoVinculado()= apiRequest { api.getVehiculoVinculado() }
    suspend fun sendParteSave(parteDiario: ParteDiario)=apiRequest { api.sendParte(parteDiario) }
    suspend fun sendImgCombustible(file: MultipartBody.Part, name: RequestBody, key:String?=null): ResponseGeneral {
        return apiRequest { api.createImgConbustible(file,name,key) }
    }
    suspend fun getParteData() = apiRequest { api.obtenerParte() }
}