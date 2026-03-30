package com.example.practica22

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class GestionDoctoresActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listaDoctores: MutableList<Doctor>
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_doctores)

        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.listViewDoctores)
        val btnAgregar = findViewById<Button>(R.id.btnAgregarDoctor)

        cargarDoctores()

        btnAgregar.setOnClickListener {
            mostrarDialogoDoctor(null)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            mostrarDialogoDoctor(listaDoctores[position])
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val doctor = listaDoctores[position]
            AlertDialog.Builder(this)
                .setTitle("Eliminar Doctor")
                .setMessage("¿Eliminar a ${doctor.nombre}?")
                .setPositiveButton("Sí") { _, _ ->
                    dbHelper.eliminarDoctor(doctor.id)
                    cargarDoctores()
                    Toast.makeText(this, "Doctor eliminado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .show()
            true
        }
    }

    private fun cargarDoctores() {
        listaDoctores = dbHelper.obtenerTodosLosDoctores()
        val nombres = listaDoctores.map { "${it.nombre} - ${it.especialidad} | ${it.email}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)
        listView.adapter = adapter
    }

    private fun mostrarDialogoDoctor(doctor: Doctor?) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 30, 50, 30)

        layout.addView(TextView(this).apply {
            text = "Nombre completo:"
            textSize = 14f
            setPadding(0, 10, 0, 5)
        })
        val etNombre = EditText(this).apply {
            hint = "Ej: Dr. Juan Pérez"
            setPadding(20, 10, 20, 10)
            background = android.graphics.drawable.ColorDrawable(0xFFEEEEEE.toInt())
        }
        layout.addView(etNombre)

        layout.addView(TextView(this).apply {
            text = "Especialidad:"
            textSize = 14f
            setPadding(0, 15, 0, 5)
        })
        val etEspecialidad = EditText(this).apply {
            hint = "Ej: Cardiología, Dermatología"
            setPadding(20, 10, 20, 10)
            background = android.graphics.drawable.ColorDrawable(0xFFEEEEEE.toInt())
        }
        layout.addView(etEspecialidad)

        layout.addView(TextView(this).apply {
            text = "Email:"
            textSize = 14f
            setPadding(0, 15, 0, 5)
        })
        val etEmail = EditText(this).apply {
            hint = "doctor@veterinaria.com"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setPadding(20, 10, 20, 10)
            background = android.graphics.drawable.ColorDrawable(0xFFEEEEEE.toInt())
        }
        layout.addView(etEmail)

        layout.addView(TextView(this).apply {
            text = "Contraseña:"
            textSize = 14f
            setPadding(0, 15, 0, 5)
        })
        val etPassword = EditText(this).apply {
            hint = "Mínimo 4 caracteres"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            setPadding(20, 10, 20, 10)
            background = android.graphics.drawable.ColorDrawable(0xFFEEEEEE.toInt())
        }
        layout.addView(etPassword)

        doctor?.let {
            etNombre.setText(it.nombre)
            etEspecialidad.setText(it.especialidad)
            etEmail.setText(it.email)
            etPassword.setText(it.password)
        }

        AlertDialog.Builder(this)
            .setTitle(if (doctor == null) "Nuevo Doctor" else "Editar Doctor")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString()
                val especialidad = etEspecialidad.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                when {
                    nombre.isEmpty() -> Toast.makeText(this, "Ingrese el nombre", Toast.LENGTH_SHORT).show()
                    especialidad.isEmpty() -> Toast.makeText(this, "Ingrese la especialidad", Toast.LENGTH_SHORT).show()
                    email.isEmpty() -> Toast.makeText(this, "Ingrese el email", Toast.LENGTH_SHORT).show()
                    password.isEmpty() -> Toast.makeText(this, "Ingrese la contraseña", Toast.LENGTH_SHORT).show()
                    password.length < 4 -> Toast.makeText(this, "Mínimo 4 caracteres", Toast.LENGTH_SHORT).show()
                    else -> {
                        if (doctor == null) {
                            val nuevoDoctor = Doctor().apply {
                                id = UUID.randomUUID().toString()
                                this.nombre = nombre
                                this.especialidad = especialidad
                                this.email = email
                                this.password = password
                            }
                            if (dbHelper.insertarDoctor(nuevoDoctor)) {
                                Toast.makeText(this, "✅ Doctor registrado", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "❌ Error", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            doctor.nombre = nombre
                            doctor.especialidad = especialidad
                            doctor.email = email
                            doctor.password = password
                            if (dbHelper.actualizarDoctor(doctor)) {
                                Toast.makeText(this, "✅ Doctor actualizado", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "❌ Error", Toast.LENGTH_SHORT).show()
                            }
                        }
                        cargarDoctores()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}