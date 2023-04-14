package com.example.ahorasiahorano

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiServiceRefCatas {
    @GET
   // fun getPuntos(): Response<List<RfXml>>
    //suspend fun getPuntos(@Url url: String): Response<GmlposList>
    suspend fun getPuntos(@Url url: String): Response<Puntos>
}