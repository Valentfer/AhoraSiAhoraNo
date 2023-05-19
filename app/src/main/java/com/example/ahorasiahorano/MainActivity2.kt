package com.example.ahorasiahorano

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity2 : AppCompatActivity() {

    lateinit var btnRefCat: Button
    lateinit var btnPorCoor: Button
    lateinit var etRefCat: EditText
    private lateinit var usuario: String
    var porCoordenadas: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        btnRefCat = findViewById(R.id.btnRefCata)
        btnPorCoor = findViewById(R.id.btnPorCoor)
        etRefCat = findViewById(R.id.etRefCata)
        usuario = intent.extras!!.getString("usuario").toString()
        btnPorCoor.setOnClickListener {
            parcelaUbicacion()
        }
        btnRefCat.setOnClickListener {
            parcelaRefCata()
        }
    }

    private fun parcelaRefCata() {
        porCoordenadas = false
        if (etRefCat.text.isNotEmpty()){
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("referencia", etRefCat.text.toString())
            intent.putExtra("boolean", porCoordenadas)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }else{
            Toast.makeText(this, "Debes introducir la referencia catastral", Toast.LENGTH_LONG).show()
        }

    }

    private fun parcelaUbicacion() {
        porCoordenadas = true
        val intent= Intent(this, MainActivity::class.java)
        intent.putExtra("boolean", porCoordenadas)
        intent.putExtra("usuario", usuario)
        startActivity(intent)
    }
}