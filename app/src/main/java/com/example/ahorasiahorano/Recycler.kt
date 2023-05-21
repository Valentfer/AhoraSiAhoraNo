package com.example.ahorasiahorano

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Recycler : AppCompatActivity() {

    private val parcelas = mutableListOf<Parcela>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler)
        buscar()
        init()
    }
    private fun init(){
        val recycler = findViewById<RecyclerView>(R.id.rcRecycler)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = AdaptadorParcelas(parcelas)
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

                val user = fila.getString(0)
                val image = fila.getString(1)
                val latitud = fila.getString(2)
                val longitud = fila.getString(3)

                parcelas.add(Parcela(user, image, latitud.toDouble(), longitud.toDouble()))
                fila.close()
                baseDeDatos.close()
            } else {
                Toast.makeText(this, "No existen parcelas", Toast.LENGTH_SHORT).show()
            }

    }
}