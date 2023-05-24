package com.example.ahorasiahorano

import java.io.Serializable

data class DatosParcela(
    val parcela: Parcela,
    val refeCat: String,
    val municipio: String,
    val dir: String,
    val extension: String,
    val codPostal: String
) : Serializable