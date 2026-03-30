package com.example.practica22

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvError: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmailLogin)
        etPassword = findViewById(R.id.etPasswordLogin)
        btnLogin = findViewById(R.id.btnLogin)
        tvError = findViewById(R.id.tvError)
        dbHelper = DatabaseHelper(this)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            when {
                email.isEmpty() || password.isEmpty() -> {
                    mostrarError("Por favor ingresa email y contraseña")
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    mostrarError("Formato de email inválido")
                }
                else -> {
                    val doctor = dbHelper.validarDoctor(email, password)
                    if (doctor != null) {
                        Toast.makeText(this, "Bienvenido ${doctor.nombre}", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, ListaMascotasActivity::class.java)
                        intent.putExtra("doctor", doctor)
                        startActivity(intent)
                        finish()
                    } else {
                        mostrarError("Credenciales incorrectas")
                    }
                }
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        tvError.text = mensaje
        tvError.visibility = android.view.View.VISIBLE
    }
}