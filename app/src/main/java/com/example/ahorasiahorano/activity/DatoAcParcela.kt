package com.example.ahorasiahorano.activity

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import com.example.ahorasiahorano.clases.DatosParcela
import com.example.ahorasiahorano.R
import com.google.gson.Gson

class DatoAcParcela : AppCompatActivity() {

    private lateinit var tvRef: TextView
    private lateinit var tvDir: TextView
    private lateinit var tvLongDato: TextView
    private lateinit var tvLatDato: TextView
    private lateinit var ivImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dato_ac_parcela)

        tvRef = findViewById(R.id.tvRefDato)
        tvDir = findViewById(R.id.tvDirDato)
        tvLongDato = findViewById(R.id.tvLongDato)
        tvLatDato = findViewById(R.id.tvLatDato)
        ivImageView = findViewById(R.id.imvDato)
        mostrarDatos()
    }

    private fun mostrarDatos() {
        val jsonString = intent.extras?.getString("referencia", "").toString()
        val objeto = Gson().fromJson(jsonString, DatosParcela::class.java)

        tvRef.text = tvRef.text.toString() + "\n" + objeto.refeCat
        tvDir.text = tvDir.text.toString() + "\n" + objeto.dir
        tvLongDato.text = tvLongDato.text.toString() + "\n" + objeto.longitud
        tvLatDato.text = tvLatDato.text.toString() + "\n" + objeto.latitud
        val bitmap = objeto.parcela.imagen
        val bitbytearra = Base64.decode(bitmap, Base64.DEFAULT)
        val imagenbit = BitmapFactory.decodeByteArray(bitbytearra, 0, bitbytearra.size)
        ivImageView.findViewById<ImageView>(R.id.imvDato).setImageBitmap(imagenbit)
    }
}