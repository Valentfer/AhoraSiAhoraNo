package com.example.ahorasiahorano.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ahorasiahorano.R

/*
* Clase menu donde se elige entre ver las parcelas guardadas, obtener los datos de una parcela proporcionandole
* la referencia catastral o realizarlo a partir de la nuestra ubicación
* */
class Menu : AppCompatActivity() {

    private lateinit var btnRefCat: Button
    private lateinit var btnPorCoor: Button
    private lateinit var btnMisParcelas: Button
    private lateinit var etRefCat: EditText
    private lateinit var usuario: String
    private var porCoordenadas: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)
        btnRefCat = findViewById(R.id.btnRefCata)
        btnPorCoor = findViewById(R.id.btnPorCoor)
        btnMisParcelas = findViewById(R.id.btnMisParcelas)
        etRefCat = findViewById(R.id.etRefCata)
        usuario = intent.extras!!.getString("usuario").toString()
        btnPorCoor.setOnClickListener {
            parcelaUbicacion()
        }
        btnRefCat.setOnClickListener {
            parcelaRefCata()
        }
        btnMisParcelas.setOnClickListener {
            irAMisParcela()
        }
    }

    //Función que nos lleva a las parcelas guardadas del usuario
    private fun irAMisParcela() {
        val intent = Intent(this, Recycler::class.java)
        intent.putExtra("usuario", usuario)
        startActivity(intent)
    }

    //Función para obtener los datos por referencia
    private fun parcelaRefCata() {
        porCoordenadas = false
        if (etRefCat.text.isNotEmpty()) {
            val intent = Intent(this, Mapa::class.java)
            intent.putExtra("referencia", etRefCat.text.toString())
            intent.putExtra("boolean", porCoordenadas)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Debes introducir la referencia catastral", Toast.LENGTH_LONG)
                .show()
        }

    }

    //Función para obtener los datos por ubicación
    private fun parcelaUbicacion() {
        porCoordenadas = true
        val intent = Intent(this, Mapa::class.java)
        intent.putExtra("boolean", porCoordenadas)
        intent.putExtra("usuario", usuario)
        startActivity(intent)
    }
}