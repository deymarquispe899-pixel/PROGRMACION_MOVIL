package com.example.practica22

import android.app.AlertDialog
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class ListaMascotasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listViewMascotas: ListView
    private lateinit var tvDoctorNombre: TextView
    private lateinit var btnModoOscuro: Button
    private lateinit var btnCerrarSesion: Button
    private var doctorActual: Doctor? = null
    private lateinit var listaMascotas: MutableList<Mascota>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_mascotas)

        // Obtener doctor con compatibilidad
        doctorActual = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("doctor", Doctor::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("doctor") as? Doctor
        }

        dbHelper = DatabaseHelper(this)
        listViewMascotas = findViewById(R.id.listViewMascotas)
        tvDoctorNombre = findViewById(R.id.tvDoctorNombre)
        btnModoOscuro = findViewById(R.id.btnModoOscuro)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        tvDoctorNombre.text = "🐾 Bienvenido, ${doctorActual?.nombre}"

        cargarMascotas()

        btnModoOscuro.setOnClickListener {
            cambiarTema()
        }

        btnCerrarSesion.setOnClickListener {
            finish()
        }

        listViewMascotas.setOnItemClickListener { _, _, position, _ ->
            val mascota = listaMascotas[position]
            mostrarDetallesMascota(mascota)
        }
    }

    private fun cargarMascotas() {
        listaMascotas = dbHelper.obtenerTodasLasMascotas()

        if (listaMascotas.isEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("📭 No hay mascotas registradas"))
            listViewMascotas.adapter = adapter
        } else {
            // Adaptador personalizado para mostrar imagen + texto
            val adapter = object : BaseAdapter() {
                override fun getCount(): Int = listaMascotas.size
                override fun getItem(position: Int): Any = listaMascotas[position]
                override fun getItemId(position: Int): Long = position.toLong()

                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                    val view = convertView ?: LayoutInflater.from(this@ListaMascotasActivity)
                        .inflate(R.layout.item_mascota, parent, false)

                    val mascota = listaMascotas[position]

                    val imgFoto = view.findViewById<ImageView>(R.id.imgFotoMascota)
                    val tvNombre = view.findViewById<TextView>(R.id.tvNombreMascota)
                    val tvInfo = view.findViewById<TextView>(R.id.tvInfoMascota)

                    tvNombre.text = mascota.nombre
                    tvInfo.text = "${mascota.tipo} | ${mascota.peso} kg"

                    // CARGAR LA IMAGEN SELECCIONADA
                    if (!mascota.imagenUri.isNullOrEmpty()) {
                        try {
                            val imagenUri = Uri.parse(mascota.imagenUri)
                            // Verificar si la URI es válida
                            contentResolver.openInputStream(imagenUri)?.use {
                                imgFoto.setImageURI(imagenUri)
                            } ?: run {
                                imgFoto.setImageResource(android.R.drawable.ic_menu_gallery)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            imgFoto.setImageResource(android.R.drawable.ic_menu_gallery)
                        }
                    } else {
                        imgFoto.setImageResource(android.R.drawable.ic_menu_gallery)
                    }

                    return view
                }
            }
            listViewMascotas.adapter = adapter
        }
    }

    private fun mostrarDetallesMascota(mascota: Mascota) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_detalle_mascota, null)

        val imgFoto = dialogView.findViewById<ImageView>(R.id.imgDetalleFoto)
        val tvNombre = dialogView.findViewById<TextView>(R.id.tvDetalleNombre)
        val tvTipo = dialogView.findViewById<TextView>(R.id.tvDetalleTipo)
        val tvPeso = dialogView.findViewById<TextView>(R.id.tvDetallePeso)
        val tvMedidas = dialogView.findViewById<TextView>(R.id.tvDetalleMedidas)
        val tvDiagnostico = dialogView.findViewById<TextView>(R.id.tvDetalleDiagnostico)
        val tvDoctor = dialogView.findViewById<TextView>(R.id.tvDetalleDoctor)

        // CARGAR LA IMAGEN EN EL DETALLE
        if (!mascota.imagenUri.isNullOrEmpty()) {
            try {
                val imagenUri = Uri.parse(mascota.imagenUri)
                contentResolver.openInputStream(imagenUri)?.use {
                    imgFoto.setImageURI(imagenUri)
                } ?: run {
                    imgFoto.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                imgFoto.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            imgFoto.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        tvNombre.text = "🐕 ${mascota.nombre}"
        tvTipo.text = "📌 Tipo: ${mascota.tipo}"
        tvPeso.text = "⚖️ Peso: ${mascota.peso} kg"
        tvMedidas.text = "📏 Medidas: ${mascota.medidas}"
        tvDiagnostico.text = "🏥 Diagnóstico: ${mascota.descripcionDolencia}"

        val doctor = dbHelper.obtenerDoctorPorId(mascota.idDoctor)
        tvDoctor.text = "👨‍⚕️ Doctor: ${doctor?.nombre ?: "No asignado"} - ${doctor?.especialidad ?: ""}"

        AlertDialog.Builder(this)
            .setTitle("Detalles de ${mascota.nombre}")
            .setView(dialogView)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun cambiarTema() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Toast.makeText(this, "🌞 Modo Claro activado", Toast.LENGTH_SHORT).show()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Toast.makeText(this, "🌙 Modo Oscuro activado", Toast.LENGTH_SHORT).show()
        }
        recreate()
    }
}