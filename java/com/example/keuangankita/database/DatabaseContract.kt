package com.example.keuangankita.database

object DatabaseContract {

    // Tabel Users
    object UserEntry {
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
    }

    // Tabel Transaksi
    object TransaksiEntry {
        const val TABLE_NAME = "transaksi"
        const val COLUMN_ID = "id"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_JENIS = "jenis"           // "pemasukan" atau "pengeluaran"
        const val COLUMN_JUMLAH = "jumlah"
        const val COLUMN_KETERANGAN = "keterangan"
        const val COLUMN_KATEGORI = "kategori"
        const val COLUMN_TANGGAL = "tanggal"       // format: yyyy-MM-dd
    }
}
