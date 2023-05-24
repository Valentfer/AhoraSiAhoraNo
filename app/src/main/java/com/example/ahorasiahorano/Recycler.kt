package com.example.ahorasiahorano

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    private fun obtenerDAtos(parcela: Parcela) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val obtenerRefCat = ObtenerRefCat(parcela.Longitud, parcela.latitud)
                obtenerRefCat.getRefCatastral { ref, dir, codPostal, extension, muni ->
                    runOnUiThread {
                        if (pBar.isVisible) {
                            pBar.visibility = View.GONE
                        }
                        parcelas.add(DatosParcela(parcela, ref, dir, codPostal, extension, muni))
                        adapter.notifyItemInserted(parcelas.size)
                    }
                }
            } catch (e: Exception) {
                Log.e("parcelas", e.message.toString())
            }
        }
    }

    override fun onItemClick(datosParcela: DatosParcela) {
        val intent = Intent(this, DatoAcParcela::class.java)
        intent.putExtra("referencia", datosParcela.refeCat)
        intent.putExtra("direccion", datosParcela.dir)
        intent.putExtra("municipio", datosParcela.municipio)
        intent.putExtra("codpostal", datosParcela.codPostal)
        intent.putExtra("extension", datosParcela.extension)
        intent.putExtra("imagen", datosParcela.parcela.imagen)
        startActivity(intent)
    }
}