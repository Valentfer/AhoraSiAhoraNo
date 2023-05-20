package com.example.ahorasiahorano

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Login : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegistro: Button
    private lateinit var btnInicio: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        etUsuario = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)
        btnRegistro = findViewById(R.id.btnRegistro)
        btnInicio = findViewById(R.id.btnInicio)

        btnRegistro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
        btnInicio.setOnClickListener {
            if (etUsuario.text.isNotEmpty() && etPassword.text.isNotEmpty()){
                if (iniciarApp(etUsuario.text.toString(), etPassword.text.toString())){
                    val intent = Intent(this, Menu::class.java)
                    intent.putExtra("usuario", etUsuario.text.toString())
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"Datos incorrectos",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Introduce el usuario y contraseña",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun iniciarApp(usuario: String, password: String): Boolean {

        val admin = BBDD(this, "login", null, 1)
            val db = admin.readableDatabase
            val cursor = db.rawQuery("select * from login where usuario ='$usuario' and password = '$password'", null)
            val count = cursor.count
            cursor.close()
            db.close()
            return count > 0
    }
}