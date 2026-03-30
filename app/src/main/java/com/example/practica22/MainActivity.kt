package com.example.practica22

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRegistroMascota = findViewById<Button>(R.id.btnRegistroMascota)
        val btnRegistroDoctor = findViewById<Button>(R.id.btnRegistroDoctor)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnRegistroMascota.setOnClickListener {
            startActivity(Intent(this, RegistroMascotaActivity::class.java))
        }

        btnRegistroDoctor.setOnClickListener {
            startActivity(Intent(this, GestionDoctoresActivity::class.java))
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}