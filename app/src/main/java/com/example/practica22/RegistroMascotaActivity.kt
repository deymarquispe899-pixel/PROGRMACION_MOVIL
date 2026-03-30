package com.example.practica22

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class RegistroMascotaActivity : AppCompatActivity() {

    private lateinit var etNombreMascota: EditText
    private lateinit var etTipoMascota: EditText
    private lateinit var etPeso: EditText
    private lateinit var etMedidas: EditText
    private lateinit var etDescripcionDolencia: EditText
    private lateinit var spinnerDoctores: Spinner
    private lateinit var fabGuardar: FloatingActionButton
    private lateinit var btnCargarImagen: Button
    private lateinit var tvNombreImagen: TextView

    private lateinit var dbHelper: DatabaseHelper
    private var imagenSeleccionadaUri: String? = null
    private lateinit var listaDoctores: List<Doctor>

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_mascota)

        // Inicializar vistas
        etNombreMascota = findViewById(R.id.etNombreMascota)
        etTipoMascota = findViewById(R.id.etTipoMascota)
        etPeso = findViewById(R.id.etPeso)
        etMedidas = findViewById(R.id.etMedidas)
        etDescripcionDolencia = findViewById(R.id.etDescripcionDolencia)
        spinnerDoctores = findViewById(R.id.spinnerDoctores)
        fabGuardar = findViewById(R.id.fabGuardarHistorial)
        btnCargarImagen = findViewById(R.id.btnCargarImagen)
        tvNombreImagen = findViewById(R.id.tvNombreImagen)

        dbHelper = DatabaseHelper(this)

        // Cargar lista de doctores
        cargarDoctores()

        // Botón para cargar imagen desde galería
        btnCargarImagen.setOnClickListener {
            abrirGaleria()
        }

        // Floating Action Button para guardar
        fabGuardar.setOnClickListener {
            guardarHistorial()
        }
    }

    private fun cargarDoctores() {
        listaDoctores = dbHelper.obtenerTodosLosDoctores()

        if (listaDoctores.isEmpty()) {
            Toast.makeText(this, "⚠️ No hay doctores registrados. Primero registre un doctor.", Toast.LENGTH_LONG).show()
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("No hay doctores disponibles"))
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDoctores.adapter = adapter
        } else {
            val nombresDoctores = listaDoctores.map { "${it.nombre} - ${it.especialidad}" }.toTypedArray()
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresDoctores)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDoctores.adapter = adapter
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val imagenUri = data.data
            imagenSeleccionadaUri = imagenUri.toString()

            // Mostrar nombre del archivo seleccionado
            val nombreArchivo = obtenerNombreArchivo(imagenUri)
            tvNombreImagen.text = "✅ Imagen seleccionada: $nombreArchivo"
            tvNombreImagen.visibility = android.view.View.VISIBLE
        }
    }

    private fun obtenerNombreArchivo(uri: Uri?): String {
        var nombreArchivo: String? = null
        if (uri?.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nombreIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                    if (nombreIndex != -1) {
                        nombreArchivo = cursor.getString(nombreIndex)
                    }
                }
            }
        }
        if (nombreArchivo == null) {
            nombreArchivo = uri?.path
            val cut = nombreArchivo?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                nombreArchivo = nombreArchivo?.substring(cut + 1)
            }
        }
        return nombreArchivo ?: "imagen.jpg"
    }

    private fun guardarHistorial() {
        // Obtener datos del formulario
        val nombre = etNombreMascota.text.toString().trim()
        val tipo = etTipoMascota.text.toString().trim()
        val pesoStr = etPeso.text.toString().trim()
        val medidas = etMedidas.text.toString().trim()
        val descripcion = etDescripcionDolencia.text.toString().trim()

        // Validaciones
        when {
            nombre.isEmpty() -> {
                Toast.makeText(this, "❌ Ingrese el nombre de la mascota", Toast.LENGTH_SHORT).show()
                etNombreMascota.requestFocus()
            }
            tipo.isEmpty() -> {
                Toast.makeText(this, "❌ Ingrese el tipo de mascota", Toast.LENGTH_SHORT).show()
                etTipoMascota.requestFocus()
            }
            pesoStr.isEmpty() -> {
                Toast.makeText(this, "❌ Ingrese el peso de la mascota", Toast.LENGTH_SHORT).show()
                etPeso.requestFocus()
            }
            medidas.isEmpty() -> {
                Toast.makeText(this, "❌ Ingrese las medidas de la mascota", Toast.LENGTH_SHORT).show()
                etMedidas.requestFocus()
            }
            descripcion.isEmpty() -> {
                Toast.makeText(this, "❌ Ingrese la descripción de la dolencia", Toast.LENGTH_SHORT).show()
                etDescripcionDolencia.requestFocus()
            }
            listaDoctores.isEmpty() -> {
                Toast.makeText(this, "⚠️ No hay doctores registrados. Registre un doctor primero.", Toast.LENGTH_LONG).show()
            }
            else -> {
                val peso = pesoStr.toFloatOrNull()
                if (peso == null) {
                    Toast.makeText(this, "❌ Ingrese un peso válido (ej: 5.5)", Toast.LENGTH_SHORT).show()
                    etPeso.requestFocus()
                    return
                }

                if (peso <= 0) {
                    Toast.makeText(this, "❌ El peso debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                    etPeso.requestFocus()
                    return
                }

                // Obtener el doctor seleccionado
                val posicion = spinnerDoctores.selectedItemPosition
                if (posicion < 0 || posicion >= listaDoctores.size) {
                    Toast.makeText(this, "❌ Seleccione un doctor válido", Toast.LENGTH_SHORT).show()
                    return
                }

                val doctorSeleccionado = listaDoctores[posicion]

                // Crear objeto Mascota
                val mascota = Mascota().apply {
                    id = UUID.randomUUID().toString()
                    this.nombre = nombre
                    this.tipo = tipo
                    this.peso = peso
                    this.medidas = medidas
                    this.descripcionDolencia = descripcion
                    this.imagenUri = imagenSeleccionadaUri
                    this.idDoctor = doctorSeleccionado.id
                }

                // Guardar en base de datos
                val exito = dbHelper.insertarMascota(mascota)

                if (exito) {
                    Toast.makeText(this, "✅ Historial clínico guardado exitosamente", Toast.LENGTH_LONG).show()
                    finish() // Regresar a la pantalla principal
                } else {
                    Toast.makeText(this, "❌ Error al guardar el historial", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}