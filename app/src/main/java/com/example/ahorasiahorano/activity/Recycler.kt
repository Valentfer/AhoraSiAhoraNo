package com.example.ahorasiahorano.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ahorasiahorano.recycler.AdaptadorParcelas
import com.example.ahorasiahorano.baseDatos.BBDD
import com.example.ahorasiahorano.clases.DatosParcela
import com.example.ahorasiahorano.clases.ObtenerRefCat
import com.example.ahorasiahorano.clases.Parcela
import com.example.ahorasiahorano.R
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
/*
* Activity RecyclerView donde se realiza la búsqueda de
* */
class Recycler : AppCompatActivity(), AdaptadorParcelas.OnItemClickListener {

    private val parcelas = mutableListOf<DatosParcela>()
    private lateinit var adapter: AdaptadorParcelas
    private lateinit var pBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler)
        pBar = findViewById(R.id.pBar)
        buscar()
        init()
    }

    private fun init() {
        val recycler = findViewById<RecyclerView>(R.id.rcRecycler)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = AdaptadorParcelas(parcelas, this)
        recycler.adapter = adapter
    }
    /*
    * Busca las parcelas en la base de datos donde coincide el usuario que accede a la aplicación, si no aún no dispone de parcelas le muestra un mensaje
    * */
    private fun buscar() {
        val admin = BBDD(this, "parcelas", null, 1)
        val baseDeDatos = admin.writableDatabase

        val usuario = intent.extras!!.getString("usuario")

        val fila = baseDeDatos.rawQuery(
            "select usuario, imagen, latitud, longitud from parcelas where usuario ='$usuario'",
            null
        )
        if (fila.moveToFirst()) {
            do {

                val user = fila.getString(0)
                val image = fila.getString(1)
                val latitud = fila.getString(2)
                val longitud = fila.getString(3)

                obtenerDAtos(Parcela(user, image, latitud.toDouble(), longitud.toDouble()))

            } while (fila.moveToNext())
            fila.close()
            baseDeDatos.close()
        } else {
            Toast.makeText(this, "No existen parcelas", Toast.LENGTH_SHORT).show()
        }
    }
/*
* Función que busca los datos que se mostrarán en el cardview o pasará al activity correspondiente al hacer click en el cardview
* */
    private fun obtenerDAtos(parcela: Parcela) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val obtenerRefCat = ObtenerRefCat(parcela.Longitud, parcela.latitud)
                obtenerRefCat.getRefCatastral { ref, dir, lati, longi ->
                    runOnUiThread {
                        if (pBar.isVisible) {
                            pBar.visibility = View.GONE
                        }
                        parcelas.add(DatosParcela(parcela, ref, dir, lati, longi))
                        adapter.notifyItemInserted(parcelas.size - 1)
                    }
                }
            } catch (e: Exception) {
                Log.e("parcelas", e.message.toString())
            }
        }
    }

    override fun onItemClick(datosParcela: DatosParcela) {
        val intent = Intent(this, DatoAcParcela::class.java)
        intent.putExtra("referencia", Gson().toJson(datosParcela))
        startActivity(intent)
    }
}