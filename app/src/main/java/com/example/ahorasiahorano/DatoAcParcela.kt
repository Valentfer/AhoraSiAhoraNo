package com.example.ahorasiahorano

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson

class DatoAcParcela : AppCompatActivity() {

    private lateinit var tvRef: TextView
    private lateinit var tvDir: TextView
    private lateinit var tvLongDato: TextView
    private lateinit var tvLatDato: TextView
    private lateinit var tvExt: TextView
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

        tvRef.text = objeto.refeCat
        tvDir.text = objeto.dir
        tvLongDato.text = objeto.longitud
        tvLatDato.text = objeto.latitud
        val bitmap = objeto.parcela.imagen
        val bitbytearra = Base64.decode(bitmap, Base64.DEFAULT)
        val imagenbit = BitmapFactory.decodeByteArray(bitbytearra, 0, bitbytearra.size)
        ivImageView.findViewById<ImageView>(R.id.imvDato).setImageBitmap(imagenbit)
    }
}