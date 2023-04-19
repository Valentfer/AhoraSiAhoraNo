package com.example.ahorasiahorano

import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceRefCatas {
    //@GET
    @GET("wfsCP.aspx?service=wfs&amp;version=2&amp;request=GetFeature&amp;STOREDQUERIE_ID=GetParcel&amp;refcat=3870005VH4137S&amp;srsname=EPSG::25830")
   // fun getPuntos(): Response<List<RfXml>>
    //suspend fun getPuntos(@Url url: String): Response<GmlLinearRing>
    suspend fun getPuntos(gmlLinearRing: GmlLinearRing): Response<GmlLinearRing>
    //suspend fun getPuntos(@Url url: String): Response<GmlposList>
}