package com.example.keuangankita.model

data class Transaksi(
    val id: Int = 0,
    val userId: Int,
    val jenis: String,          // "pemasukan" atau "pengeluaran"
    val jumlah: Double,
    val keterangan: String = "",
    val kategori: String,
    val tanggal: String         // format: yyyy-MM-dd
) {
    companion object {
        const val JENIS_PEMASUKAN = "pemasukan"
        const val JENIS_PENGELUARAN = "pengeluaran"
    }
}
