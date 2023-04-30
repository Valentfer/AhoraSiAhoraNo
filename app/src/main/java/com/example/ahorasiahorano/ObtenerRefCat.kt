package com.example.ahorasiahorano

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ObtenerRefCat(var longitud: Double, var latitud: Double) {

    fun getRefCatastral(callback : (String) -> Unit) {
        val url = "reverseGeocode?lon=$longitud&lat=$latitud&type=refcatastral"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call= getRetrofitRef().create(ApiServiceCoord::class.java)
                    .getRefCatastral(url)
                val response = call.body()
                if (call.isSuccessful){
                    val ref = response?.address.toString()
                    Log.i("RESPUESTA2", response?.address.toString())
                    callback(ref)
                }else{
                        Log.i("error", "ERROR EN LA RESPUESTA")
                }
            }catch (e:java.lang.Exception){
                    Log.i("error", e.message.toString())
            }
        }
    }
    fun getRetrofitRef(): Retrofit {
        val urlBase = "http://www.cartociudad.es/geocoder/api/geocoder/"
        return Retrofit.Builder().baseUrl(urlBase).addConverterFactory(GsonConverterFactory.create()).build()
    }
}


