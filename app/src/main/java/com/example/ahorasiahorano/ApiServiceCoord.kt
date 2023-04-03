package com.example.ahorasiahorano

import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceCoord {
    @GET
    fun getRefCatastral(): Response<CoordToRc>
}