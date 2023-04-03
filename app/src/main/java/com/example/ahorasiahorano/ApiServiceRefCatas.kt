package com.example.ahorasiahorano

import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceRefCatas {
    @GET()
    fun getPuntos(): Response<List<RfXml>>
}