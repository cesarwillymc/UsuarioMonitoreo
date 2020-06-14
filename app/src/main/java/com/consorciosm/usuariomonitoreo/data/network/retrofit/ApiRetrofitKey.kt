package com.consorciosm.usuariomonitoreo.data.network.retrofit

import com.consorciosm.usuariomonitoreo.common.constants.Constants.BASE_URL_API
import com.consorciosm.usuariomonitoreo.data.model.*
import com.consorciosm.usuariomonitoreo.data.model.vehiculo.Carro
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
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

    @PUT("user/parte")
    suspend fun sendParte(
        @Body parteDiario: Parte
    ): Response<ResponseGeneral>
    @Multipart
    @PUT("user/carCombustible/{carId}")
    suspend fun createImgConbustible(
        @Part file: MultipartBody.Part,
        @Part("carImg") name: RequestBody,
        @Path("carId") carId:String?
    ): Response<ResponseGeneral>
    @GET("user/parte")
    suspend fun obtenerParte(): Response<ParteDiario>
    @GET("user/getKilometraje")
    suspend fun obtenerParteKilometraje(): Response<ParteDiario>
    @GET("user/getListMensajes")
    suspend fun getListNotificaciones(
        @Query("pagina") pagina:Int
    ):Response<List<NotificacionesList>>
    @GET("supervisor/orden/{id}")
    suspend fun getListNotificacionesById(
        @Path("id") id:String
    ):Response<OrdenProgramada>
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