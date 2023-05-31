package com.example.ahorasiahorano.clases

import android.util.Log
import com.example.ahorasiahorano.api.ApiServiceCoord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/*
* Clase que realiza la llamada a la API cartociudad con las coordenadas,
* esta clase realiza la llamada en una corrutina para liberar el hilo principal para que la aplicación no se bloquee
* */
class ObtenerRefCat(private var longitud: Double, private var latitud: Double) {

    fun getRefCatastral(callback: (String, String, String, String) -> Unit) {
        val url = "reverseGeocode?lon=$longitud&lat=$latitud&type=refcatastral"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = getRetrofitRef().create(ApiServiceCoord::class.java)
                    .getRefCatastral(url)
                val response = call.body()
                if (call.isSuccessful) {
                    val ref = response?.address.toString()
                    val dir = response?.refCatastral.toString()
                    val lati = response?.lat.toString()
                    val longi = response?.lng.toString()
                    Log.i("RESPUESTA2", response?.address.toString())
                    callback(ref, dir, lati, longi)
                } else {
                    Log.i("error", "ERROR EN LA RESPUESTA")
                }
            } catch (e: java.lang.Exception) {
                Log.i("error", e.message.toString())
            }
        }
    }
// Uso de la clase retrofit
    private fun getRetrofitRef(): Retrofit {
        val urlBase = "http://www.cartociudad.es/geocoder/api/geocoder/"
        return Retrofit.Builder().baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}


