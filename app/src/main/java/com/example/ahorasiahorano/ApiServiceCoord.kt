package com.example.ahorasiahorano

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiServiceCoord {
    //@GET("http://www.cartociudad.es/geocoder/api/geocoder/reverseGeocode?lon={longitud}&lat={latitud}&type=refcatastral")
    @GET
   suspend fun getRefCatastral(@Url url: String): Response<CoordToRc>
/*
    companion object{
        val instance = Retrofit.Builder().baseUrl("http:").addConverterFactory(GsonConverterFactory.create()).client(
            OkHttpClient.Builder().build()
        ).build().create(ApiServiceCoord::class.java)
    }
    @GET()
    suspend fun getRefCatatral(): String{
        val response =
        return
    }

 */
}
