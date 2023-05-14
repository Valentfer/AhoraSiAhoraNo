package com.example.ahorasiahorano

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity4 : AppCompatActivity() {

    lateinit var btnRegInsertar: Button
    lateinit var etRegUsuario: EditText
    lateinit var etRegPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        btnRegInsertar = findViewById(R.id.btnRegInsertar)
        etRegUsuario = findViewById(R.id.etRegUsuario)
        etRegPassword = findViewById(R.id.etRegPassword)

        btnRegInsertar.setOnClickListener {
            insertarReg()
        }
    }

    private fun insertarReg() {
        if (etRegUsuario.text.isNotEmpty() && etRegPassword.text.isNotEmpty()){
            val admin = BBDD(this, "login", null, 1)
            val BaseDeDatos = admin.writableDatabase
                val registro = ContentValues()

                registro.put("usuario", etRegUsuario.text.toString())
                registro.put("password", etRegPassword.text.toString())

                BaseDeDatos.insert("login", null, registro)

                BaseDeDatos.close()

                etRegUsuario.setText("")
                etRegPassword.setText("")

                Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Debes introducir todos los campos", Toast.LENGTH_SHORT).show()
            }
    }
}