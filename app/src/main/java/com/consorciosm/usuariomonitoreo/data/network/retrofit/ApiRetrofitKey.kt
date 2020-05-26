package com.consorciosm.usuariomonitoreo.data.network.retrofit

import com.consorciosm.usuariomonitoreo.data.model.ResponseGeneral
import com.consorciosm.usuariomonitoreo.data.model.Usuario
import com.consorciosm.usuariomonitoreo.data.model.requestSignIn
import com.consorciosm.usuariomonitoreo.data.model.requestSignUp
import com.consorciosm.usuariomonitoreo.common.constants.Constants.BASE_URL_API
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiRetrofitKey {

    @POST("admin/registro")
    suspend fun createUser(
        @Body usuario: requestSignUp
    ): Response<ResponseGeneral>
    @POST("admin/login")
    suspend fun loginUser(
        @Body request: requestSignIn
    ): Response<requestSignIn>
    @GET("admin/informacion")
    suspend fun getUserInfo(): Response<Usuario>

    companion object{
        operator fun invoke() : ApiRetrofitKey{
            val okHttpClienteBuilder= OkHttpClient.Builder().addInterceptor(InterceptorToken()).build()

            val cliente: OkHttpClient = okHttpClienteBuilder
            return   Retrofit.Builder()
                .baseUrl(BASE_URL_API)
                .client(cliente)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiRetrofitKey::class.java)
        }
    }
}