package com.example.ahorasiahorano

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView

class DatoAcParcela : AppCompatActivity() {

    private lateinit var tvRef: TextView
    private lateinit var tvDir: TextView
    private lateinit var tvMunicipio: TextView
    private lateinit var tvCodP: TextView
    private lateinit var tvExt: TextView
    private lateinit var ivImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dato_ac_parcela)

        tvRef = findViewById(R.id.tvRefDato)
        tvDir = findViewById(R.id.tvDirDato)
        tvMunicipio = findViewById(R.id.tvMunicipioDato)
        tvCodP = findViewById(R.id.tvcodPosDato)
        tvExt = findViewById(R.id.tvExtDato)
        ivImageView = findViewById(R.id.imvDato)
        mostrarDatos()
    }

    private fun mostrarDatos() {
//val objeto = intent.extras?.getString("referencia") as DatosParcela
val objeto = intent.extras?.getSerializable("referencia") as DatosParcela

        //tvRef.text = intent.extras?.getString("referencia")
        //tvRef.text = intent.extras?.getSerializable("referencia").toString()
        tvRef.text = objeto.refeCat
        //tvDir.text = intent.extras?.getString("direccion")
        //tvDir.text = intent.extras?.getSerializable("direccion").toString()
        tvDir.text = objeto.dir
        //tvMunicipio.text = intent.extras?.getString("municipio")
        //tvMunicipio.text = intent.extras?.getSerializable("municipio").toString()
        tvMunicipio.text = objeto.municipio
        //tvCodP.text = intent.extras?.getString("codpostal")
        //tvCodP.text = intent.extras?.getSerializable("codpostal").toString()
        tvCodP.text = objeto.codPostal
        //tvExt.text = intent.extras?.getString("extension")
        //tvExt.text = intent.extras?.getSerializable("extension").toString()
        tvExt.text = objeto.extension
        // ivImageView.setImageBitmap(intent.extras.getString("imagen"))

        //val bitmap = intent.extras?.getString("imagen")
        //val bitmap = intent.extras?.getSerializable("imagen").toString()
        val bitmap = objeto.parcela.imagen
        val bitbytearra = Base64.decode(bitmap, Base64.DEFAULT)
        val imagenbit = BitmapFactory.decodeByteArray(bitbytearra, 0, bitbytearra.size)
        ivImageView.findViewById<ImageView>(R.id.imvDato).setImageBitmap(imagenbit)
    }
}