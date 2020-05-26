package com.consorciosm.usuariomonitoreo.data.network.repository

import com.consorciosm.usuariomonitoreo.common.utils.SafeApiRequest
import com.consorciosm.usuariomonitoreo.data.model.Usuario
import com.consorciosm.usuariomonitoreo.data.network.retrofit.ApiRetrofitKey
import com.consorciosm.usuariomonitoreo.data.local.db.AppDB
import com.google.firebase.firestore.FirebaseFirestore

class MainRepository (
    private val db: AppDB,
    private val api: ApiRetrofitKey,
    private val firebase: FirebaseFirestore
): SafeApiRequest() {
    fun deleteUser()= db.usuarioDao().deleteUsuario()
    fun updateUserAppDb(perfilUsuario: Usuario)=db.usuarioDao().updateUsuario(perfilUsuario)
    fun getUser()=db.usuarioDao().selectUsuario()
}