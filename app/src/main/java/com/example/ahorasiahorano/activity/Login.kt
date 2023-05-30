package com.example.ahorasiahorano.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ahorasiahorano.baseDatos.BBDD
import com.example.ahorasiahorano.R

/*
* Con esta clase se inicia la aplicación y en ella se pide un usuario y contraseña, si este está registrado y coincide con los datos guardados se da paso al menú,
* si no, y es por que no está registrado, se le da la oportunidad de registrarse o no podrá seguir usando la aplicación
* */
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
        // se define el click del botón registro para iniciar el registro
        btnRegistro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
        /*
        *se define el click del botón inicio, que comprueba que los edittext esten rellenos y llama a la función
        * iniciarApp que comprueba si tanto el usuario como la clave son correctos, y si lo son pasa al siguiente activity
        * pasando como dato el usuario para después guardar o recuperar las parcelas según proceda, en caso de error se muestra
        *  un mensaje con una instrucción
        **/
        btnInicio.setOnClickListener {
            if (etUsuario.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                if (iniciarApp(etUsuario.text.toString(), etPassword.text.toString())) {
                    val intent = Intent(this, Menu::class.java)
                    intent.putExtra("usuario", etUsuario.text.toString())
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Introduce el usuario y contraseña", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Función que realiza una solicitud a la base de datos y comprueba si el usuario con la contraseña son correctos
    private fun iniciarApp(usuario: String, password: String): Boolean {

        val admin = BBDD(this, "login", null, 1)
        val db = admin.readableDatabase
        val cursor = db.rawQuery(
            "select * from login where usuario ='$usuario' and password = '$password'",
            null
        )
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }
}