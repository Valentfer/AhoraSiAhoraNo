package com.example.ahorasiahorano.clases
/*
*  Clase para gestionar los datos que mostraremos en el activity de datos
* */
data class DatosParcela(
    val parcela: Parcela,
    val refeCat: String,
    val dir: String,
    val latitud: String,
    val longitud: String
)