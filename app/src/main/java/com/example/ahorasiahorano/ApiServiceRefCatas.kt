package com.example.ahorasiahorano

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiServiceRefCatas {
    @GET
   // suspend fun getPuntos(@Url url: String): Response<FeatureCollection>
    suspend fun getPuntos(@Url url: String): Response<GmlLinearRing>
    //suspend fun getPuntos(@Url url: String): Response<GmlposList>
}