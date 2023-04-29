package com.example.ahorasiahorano

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ObtenerRefCat(var longitud: Double, var latitud: Double) {

     lateinit var ref:String
    fun getRefCatastral(): String {
        val url = "reverseGeocode?lon=$longitud&lat=$latitud&type=refcatastral"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call= getRetrofitRef().create(ApiServiceCoord::class.java)
                    .getRefCatastral(url)
                val response = call.body()
                if (call.isSuccessful){
                    //runOnUiThread {
                       ref = response?.address.toString()
                        Log.i("RESPUESTA2", response?.address.toString())
                    //}
                }else{
                   // runOnUiThread {
                        Log.i("error", "ERROR EN LA RESPUESTA")
                 //   }
                }
            }catch (e:java.lang.Exception){
             //   runOnUiThread {
                    Log.i("error", e.message.toString())
               // }
            }
        }
        return ref
    }
    fun getRetrofitRef(): Retrofit {
        val urlBase = "http://www.cartociudad.es/geocoder/api/geocoder/"
        return Retrofit.Builder().baseUrl(urlBase).addConverterFactory(GsonConverterFactory.create()).build()
    }
}

