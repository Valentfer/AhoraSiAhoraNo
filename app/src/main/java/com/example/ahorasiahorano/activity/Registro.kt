package com.example.ahorasiahorano.activity

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ahorasiahorano.baseDatos.BBDD
import com.example.ahorasiahorano.R
/*
* Activity para realizar el registro de los ususarios, en ella se realiza una comprobación
* para que los datos de usuario no esten vacios y se realiza la inserción en la base de datos
* */
class Registro : AppCompatActivity() {

    private lateinit var btnRegInsertar: Button
    private lateinit var etRegUsuario: EditText
    private lateinit var etRegPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro)
        btnRegInsertar = findViewById(R.id.btnRegInsertar)
        etRegUsuario = findViewById(R.id.etRegUsuario)
        etRegPassword = findViewById(R.id.etRegPassword)

        btnRegInsertar.setOnClickListener {
            insertarReg()
        }
    }

    private fun insertarReg() {
        if (etRegUsuario.text.isNotEmpty() && etRegPassword.text.isNotEmpty()) {
            val admin = BBDD(this, "login", null, 1)
            val baseDeDatos = admin.writableDatabase
            val registro = ContentValues()

            registro.put("usuario", etRegUsuario.text.toString())
            registro.put("password", etRegPassword.text.toString())

            baseDeDatos.insert("login", null, registro)

            baseDeDatos.close()

            etRegUsuario.setText("")
            etRegPassword.setText("")

            Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Debes introducir todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
}