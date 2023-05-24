package com.example.ahorasiahorano

import java.io.Serializable


data class Parcela(
    val usuario: String,
    val imagen: String,
    val latitud: Double,
    val Longitud: Double
) : Serializable