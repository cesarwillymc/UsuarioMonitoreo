package com.consorciosm.usuariomonitoreo.data.network.repository

import com.consorciosm.usuariomonitoreo.common.constants.Constants.PREF_ID_USER
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.setSomeStringValue
import com.consorciosm.usuariomonitoreo.common.utils.SafeApiRequest
import com.consorciosm.usuariomonitoreo.data.local.db.AppDB
import com.consorciosm.usuariomonitoreo.data.model.ResponseGeneral
import com.consorciosm.usuariomonitoreo.data.model.Usuario
import com.consorciosm.usuariomonitoreo.data.model.requestSignIn
import com.consorciosm.usuariomonitoreo.data.model.requestSignUp
import com.consorciosm.usuariomonitoreo.data.network.retrofit.ApiRetrofitKey
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val db: AppDB,
    private val api:ApiRetrofitKey,
    private val firebase:FirebaseFirestore
):SafeApiRequest() {
    fun deleteUser()= db.usuarioDao().deleteUsuario()
    suspend fun saveUser(perfilUsuario: Usuario)= db.usuarioDao().insertUsuario(perfilUsuario)
    fun getUser()=db.usuarioDao().selectUsuario()
    suspend  fun SignUpAuth(model: requestSignUp): ResponseGeneral {
        return apiRequest { api.createUser(model) }
    }
    suspend fun SignInAuth(email: String, pass: String):requestSignIn{
        return apiRequest { api.loginUser(requestSignIn(email,pass)) }
    }

    suspend fun GetInformationUser():Usuario {
        val dato = apiRequest { api.getUserInfo()  }
        setSomeStringValue(PREF_ID_USER,dato._id)
        return dato
    }
}