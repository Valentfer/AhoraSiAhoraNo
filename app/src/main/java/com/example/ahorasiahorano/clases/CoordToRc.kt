package com.example.ahorasiahorano.clases
/*
* Clase con las variables necesarias que para gestionar la respuesta de la API de cartociciudad por coordenadas
* */
data class CoordToRc(
    val address: String,
    val comunidadAutonoma: Any,
    val countryCode: String,
    val extension: Any,
    val geom: String,
    val id: Any,
    val lat: Double,
    val lng: Double,
    val muni: Any,
    val poblacion: Any,
    val portalNumber: Int,
    val postalCode: Any,
    val priority: Int,
    val province: Any,
    val refCatastral: String,
    val state: Int,
    val stateMsg: String,
    val tip_via: Any,
    val type: Any
)