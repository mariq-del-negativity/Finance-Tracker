package com.example.keuangankita.repository

import android.content.ContentValues
import android.content.Context
import com.example.keuangankita.database.DatabaseContract.TransaksiEntry
import com.example.keuangankita.database.DatabaseHelper
import com.example.keuangankita.model.Transaksi

class TransaksiRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Tambah transaksi baru
    fun tambahTransaksi(transaksi: Transaksi): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TransaksiEntry.COLUMN_USER_ID, transaksi.userId)
            put(TransaksiEntry.COLUMN_JENIS, transaksi.jenis)
            put(TransaksiEntry.COLUMN_JUMLAH, transaksi.jumlah)
            put(TransaksiEntry.COLUMN_KETERANGAN, transaksi.keterangan)
            put(TransaksiEntry.COLUMN_KATEGORI, transaksi.kategori)
            put(TransaksiEntry.COLUMN_TANGGAL, transaksi.tanggal)
        }
        val result = db.insert(TransaksiEntry.TABLE_NAME, null, values)
        db.close()
        return result
    }

    // Hapus transaksi berdasarkan id
    fun hapusTransaksi(id: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            TransaksiEntry.TABLE_NAME,
            "${TransaksiEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        return result
    }

    // Ambil semua transaksi user (terbaru dulu)
    fun getAllTransaksi(userId: Int): List<Transaksi> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Transaksi>()
        val cursor = db.query(
            TransaksiEntry.TABLE_NAME,
            null,
            "${TransaksiEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString()),
            null, null,
            "${TransaksiEntry.COLUMN_TANGGAL} DESC, ${TransaksiEntry.COLUMN_ID} DESC"
        )
        while (cursor.moveToNext()) {
            list.add(cursorToTransaksi(cursor))
        }
        cursor.close()
        db.close()
        return list
    }

    // Ambil 5 transaksi terbaru untuk home
    fun getRecentTransaksi(userId: Int, limit: Int = 5): List<Transaksi> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Transaksi>()
        val cursor = db.query(
            TransaksiEntry.TABLE_NAME,
            null,
            "${TransaksiEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString()),
            null, null,
            "${TransaksiEntry.COLUMN_TANGGAL} DESC, ${TransaksiEntry.COLUMN_ID} DESC",
            limit.toString()
        )
        while (cursor.moveToNext()) {
            list.add(cursorToTransaksi(cursor))
        }
        cursor.close()
        db.close()
        return list
    }

    // Hitung total saldo user (pemasukan - pengeluaran)
    fun getTotalSaldo(userId: Int): Double {
        val db = dbHelper.readableDatabase
        var saldo = 0.0

        val cursorPemasukan = db.rawQuery(
            "SELECT SUM(${TransaksiEntry.COLUMN_JUMLAH}) FROM ${TransaksiEntry.TABLE_NAME} " +
                    "WHERE ${TransaksiEntry.COLUMN_USER_ID} = ? AND ${TransaksiEntry.COLUMN_JENIS} = ?",
            arrayOf(userId.toString(), Transaksi.JENIS_PEMASUKAN)
        )
        if (cursorPemasukan.moveToFirst()) {
            saldo += cursorPemasukan.getDouble(0)
        }
        cursorPemasukan.close()

        val cursorPengeluaran = db.rawQuery(
            "SELECT SUM(${TransaksiEntry.COLUMN_JUMLAH}) FROM ${TransaksiEntry.TABLE_NAME} " +
                    "WHERE ${TransaksiEntry.COLUMN_USER_ID} = ? AND ${TransaksiEntry.COLUMN_JENIS} = ?",
            arrayOf(userId.toString(), Transaksi.JENIS_PENGELUARAN)
        )
        if (cursorPengeluaran.moveToFirst()) {
            saldo -= cursorPengeluaran.getDouble(0)
        }
        cursorPengeluaran.close()
        db.close()
        return saldo
    }

    // Hitung total pemasukan bulan ini
    fun getTotalPemasukanBulanIni(userId: Int, yearMonth: String): Double {
        val db = dbHelper.readableDatabase
        var total = 0.0
        val cursor = db.rawQuery(
            "SELECT SUM(${TransaksiEntry.COLUMN_JUMLAH}) FROM ${TransaksiEntry.TABLE_NAME} " +
                    "WHERE ${TransaksiEntry.COLUMN_USER_ID} = ? AND ${TransaksiEntry.COLUMN_JENIS} = ? " +
                    "AND ${TransaksiEntry.COLUMN_TANGGAL} LIKE ?",
            arrayOf(userId.toString(), Transaksi.JENIS_PEMASUKAN, "$yearMonth%")
        )
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }

    // Hitung total pengeluaran bulan ini
    fun getTotalPengeluaranBulanIni(userId: Int, yearMonth: String): Double {
        val db = dbHelper.readableDatabase
        var total = 0.0
        val cursor = db.rawQuery(
            "SELECT SUM(${TransaksiEntry.COLUMN_JUMLAH}) FROM ${TransaksiEntry.TABLE_NAME} " +
                    "WHERE ${TransaksiEntry.COLUMN_USER_ID} = ? AND ${TransaksiEntry.COLUMN_JENIS} = ? " +
                    "AND ${TransaksiEntry.COLUMN_TANGGAL} LIKE ?",
            arrayOf(userId.toString(), Transaksi.JENIS_PENGELUARAN, "$yearMonth%")
        )
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }

    // Ambil total pengeluaran per kategori bulan ini (untuk grafik)
    fun getPengeluaranPerKategori(userId: Int, yearMonth: String): Map<String, Double> {
        val db = dbHelper.readableDatabase
        val result = mutableMapOf<String, Double>()
        val cursor = db.rawQuery(
            "SELECT ${TransaksiEntry.COLUMN_KATEGORI}, SUM(${TransaksiEntry.COLUMN_JUMLAH}) " +
                    "FROM ${TransaksiEntry.TABLE_NAME} " +
                    "WHERE ${TransaksiEntry.COLUMN_USER_ID} = ? AND ${TransaksiEntry.COLUMN_JENIS} = ? " +
                    "AND ${TransaksiEntry.COLUMN_TANGGAL} LIKE ? " +
                    "GROUP BY ${TransaksiEntry.COLUMN_KATEGORI}",
            arrayOf(userId.toString(), Transaksi.JENIS_PENGELUARAN, "$yearMonth%")
        )
        while (cursor.moveToNext()) {
            val kategori = cursor.getString(0)
            val jumlah = cursor.getDouble(1)
            result[kategori] = jumlah
        }
        cursor.close()
        db.close()
        return result
    }

    // Hitung total transaksi user
    fun getTotalCountTransaksi(userId: Int): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${TransaksiEntry.TABLE_NAME} WHERE ${TransaksiEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        var count = 0
        if (cursor.moveToFirst()) count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count
    }

    private fun cursorToTransaksi(cursor: android.database.Cursor): Transaksi {
        return Transaksi(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(TransaksiEntry.COLUMN_ID)),
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(TransaksiEntry.COLUMN_USER_ID)),
            jenis = cursor.getString(cursor.getColumnIndexOrThrow(TransaksiEntry.COLUMN_JENIS)),
            jumlah = cursor.getDouble(cursor.getColumnIndexOrThrow(TransaksiEntry.COLUMN_JUMLAH)),
            keterangan = cursor.getString(cursor.getColumnIndexOrThrow(TransaksiEntry.COLUMN_KETERANGAN)) ?: "",
            kategori = cursor.getString(cursor.getColumnIndexOrThrow(TransaksiEntry.COLUMN_KATEGORI)),
            tanggal = cursor.getString(cursor.getColumnIndexOrThrow(TransaksiEntry.COLUMN_TANGGAL))
        )
    }
}
