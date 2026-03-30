package com.example.practica22

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "Veterinaria.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE doctores (
                id TEXT PRIMARY KEY,
                nombre TEXT,
                especialidad TEXT,
                email TEXT,
                password TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE mascotas (
                id TEXT PRIMARY KEY,
                nombre TEXT,
                tipo TEXT,
                peso REAL,
                medidas TEXT,
                descripcion_dolencia TEXT,
                imagen_uri TEXT,
                id_doctor TEXT
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS doctores")
        db.execSQL("DROP TABLE IF EXISTS mascotas")
        onCreate(db)
    }

    // ==================== DOCTORES ====================

    fun insertarDoctor(doctor: Doctor): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", doctor.id)
            put("nombre", doctor.nombre)
            put("especialidad", doctor.especialidad)
            put("email", doctor.email)
            put("password", doctor.password)
        }
        return db.insert("doctores", null, values) != -1L
    }

    fun obtenerTodosLosDoctores(): MutableList<Doctor> {
        val lista = mutableListOf<Doctor>()
        val db = readableDatabase
        val cursor = db.query("doctores", null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val doctor = Doctor()
            doctor.id = cursor.getString(0)
            doctor.nombre = cursor.getString(1)
            doctor.especialidad = cursor.getString(2)
            doctor.email = cursor.getString(3)
            doctor.password = cursor.getString(4)
            lista.add(doctor)
        }
        cursor.close()
        return lista
    }

    fun actualizarDoctor(doctor: Doctor): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", doctor.nombre)
            put("especialidad", doctor.especialidad)
            put("email", doctor.email)
            put("password", doctor.password)
        }
        return db.update("doctores", values, "id = ?", arrayOf(doctor.id)) > 0
    }

    fun eliminarDoctor(id: String) {
        writableDatabase.delete("doctores", "id = ?", arrayOf(id))
    }

    fun validarDoctor(email: String, password: String): Doctor? {
        val db = readableDatabase
        val cursor = db.query(
            "doctores", null,
            "email = ? AND password = ?",
            arrayOf(email, password),
            null, null, null
        )

        var doctor: Doctor? = null
        if (cursor.moveToFirst()) {
            doctor = Doctor()
            doctor.id = cursor.getString(0)
            doctor.nombre = cursor.getString(1)
            doctor.especialidad = cursor.getString(2)
            doctor.email = cursor.getString(3)
            doctor.password = cursor.getString(4)
        }
        cursor.close()
        return doctor
    }

    fun obtenerDoctorPorId(id: String): Doctor? {
        val db = readableDatabase
        val cursor = db.query("doctores", null, "id = ?", arrayOf(id), null, null, null)

        var doctor: Doctor? = null
        if (cursor.moveToFirst()) {
            doctor = Doctor()
            doctor.id = cursor.getString(0)
            doctor.nombre = cursor.getString(1)
            doctor.especialidad = cursor.getString(2)
            doctor.email = cursor.getString(3)
            doctor.password = cursor.getString(4)
        }
        cursor.close()
        return doctor
    }

    // ==================== MASCOTAS ====================

    fun insertarMascota(mascota: Mascota): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", mascota.id)
            put("nombre", mascota.nombre)
            put("tipo", mascota.tipo)
            put("peso", mascota.peso)
            put("medidas", mascota.medidas)
            put("descripcion_dolencia", mascota.descripcionDolencia)
            put("imagen_uri", mascota.imagenUri)
            put("id_doctor", mascota.idDoctor)
        }
        return db.insert("mascotas", null, values) != -1L
    }

    fun obtenerTodasLasMascotas(): MutableList<Mascota> {
        val lista = mutableListOf<Mascota>()
        val db = readableDatabase
        val cursor = db.query("mascotas", null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val mascota = Mascota()
            mascota.id = cursor.getString(0)
            mascota.nombre = cursor.getString(1)
            mascota.tipo = cursor.getString(2)
            mascota.peso = cursor.getFloat(3)
            mascota.medidas = cursor.getString(4)
            mascota.descripcionDolencia = cursor.getString(5)
            mascota.imagenUri = cursor.getString(6)
            mascota.idDoctor = cursor.getString(7)
            lista.add(mascota)
        }
        cursor.close()
        return lista
    }
    // Agregar a DatabaseHelper.kt
    fun obtenerImagenMascota(id: String): String? {
        val db = readableDatabase
        val cursor = db.query("mascotas", arrayOf("imagen_uri"), "id = ?", arrayOf(id), null, null, null)
        var imagenUri: String? = null
        if (cursor.moveToFirst()) {
            imagenUri = cursor.getString(0)
        }
        cursor.close()
        return imagenUri
    }
}