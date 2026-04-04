package com.example.keuangankita.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.keuangankita.database.DatabaseContract.TransaksiEntry
import com.example.keuangankita.database.DatabaseContract.UserEntry

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "keuangankita.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Buat tabel users
        val createUserTable = """
            CREATE TABLE ${UserEntry.TABLE_NAME} (
                ${UserEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${UserEntry.COLUMN_USERNAME} TEXT NOT NULL UNIQUE,
                ${UserEntry.COLUMN_PASSWORD} TEXT NOT NULL
            )
        """.trimIndent()

        // Buat tabel transaksi
        val createTransaksiTable = """
            CREATE TABLE ${TransaksiEntry.TABLE_NAME} (
                ${TransaksiEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${TransaksiEntry.COLUMN_USER_ID} INTEGER NOT NULL,
                ${TransaksiEntry.COLUMN_JENIS} TEXT NOT NULL,
                ${TransaksiEntry.COLUMN_JUMLAH} REAL NOT NULL,
                ${TransaksiEntry.COLUMN_KETERANGAN} TEXT,
                ${TransaksiEntry.COLUMN_KATEGORI} TEXT NOT NULL,
                ${TransaksiEntry.COLUMN_TANGGAL} TEXT NOT NULL,
                FOREIGN KEY (${TransaksiEntry.COLUMN_USER_ID}) REFERENCES ${UserEntry.TABLE_NAME}(${UserEntry.COLUMN_ID})
            )
        """.trimIndent()

        db.execSQL(createUserTable)
        db.execSQL(createTransaksiTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${TransaksiEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${UserEntry.TABLE_NAME}")
        onCreate(db)
    }
}
