package com.consorciosm.usuariomonitoreo.data.network.retrofit

import com.consorciosm.usuariomonitoreo.data.model.Usuario
import com.consorciosm.usuariomonitoreo.data.model.requestSignIn
import com.consorciosm.usuariomonitoreo.common.constants.Constants.BASE_URL_API
import com.consorciosm.usuariomonitoreo.data.model.vehiculo.Carro
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiRetrofitKey {


    @POST("user/login")
    suspend fun loginUser(
        @Body request: requestSignIn
    ): Response<requestSignIn>
    @GET("user/informacion")
    suspend fun getUserInfo(): Response<Usuario>
    @GET("user/choferInfo")
    suspend fun getVehiculoVinculado(): Response<Carro>

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