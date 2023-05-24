package com.example.ahorasiahorano

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiServiceCoord {
    @GET
    suspend fun getRefCatastral(@Url url: String): Response<CoordToRc>

}
