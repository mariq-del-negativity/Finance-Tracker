package com.example.keuangankita.ui.transaksi

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.keuangankita.R
import com.example.keuangankita.model.Transaksi
import com.example.keuangankita.repository.TransaksiRepository
import com.example.keuangankita.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TambahPemasukanActivity : AppCompatActivity() {

    private lateinit var etJumlah: EditText
    private lateinit var etKeterangan: EditText
    private lateinit var spinnerKategori: Spinner
    private lateinit var tvLabelKategoriCustom: TextView
    private lateinit var etKategoriCustom: EditText
    private lateinit var btnBatal: AppCompatButton
    private lateinit var btnSimpan: AppCompatButton

    private lateinit var transaksiRepository: TransaksiRepository
    private lateinit var sessionManager: SessionManager

    private val kategoriList = listOf("Gaji", "Bonus", "Investasi", "Hadiah", "Lainnya")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_pemasukan)

        transaksiRepository = TransaksiRepository(this)
        sessionManager      = SessionManager(this)

        etJumlah              = findViewById(R.id.etJumlah)
        etKeterangan          = findViewById(R.id.etKeterangan)
        spinnerKategori       = findViewById(R.id.spinnerKategori)
        tvLabelKategoriCustom = findViewById(R.id.tvLabelKategoriCustom)
        etKategoriCustom      = findViewById(R.id.etKategoriCustom)
        btnBatal              = findViewById(R.id.btnBatal)
        btnSimpan             = findViewById(R.id.btnSimpan)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kategoriList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKategori.adapter = adapter

        // Tampilkan/sembunyikan field kategori custom
        spinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val isLainnya = kategoriList[pos] == "Lainnya"
                tvLabelKategoriCustom.visibility = if (isLainnya) View.VISIBLE else View.GONE
                etKategoriCustom.visibility      = if (isLainnya) View.VISIBLE else View.GONE
                if (!isLainnya) etKategoriCustom.text.clear()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnBatal.setOnClickListener { finish() }
        btnSimpan.setOnClickListener { simpanPemasukan() }
    }

    private fun simpanPemasukan() {
        val jumlahStr  = etJumlah.text.toString().trim()
        val keterangan = etKeterangan.text.toString().trim()
        val pilihanKategori = spinnerKategori.selectedItem.toString()

        // Tentukan kategori final
        val kategori = if (pilihanKategori == "Lainnya") {
            val custom = etKategoriCustom.text.toString().trim()
            if (custom.isEmpty()) {
                Toast.makeText(this, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            custom
        } else {
            pilihanKategori
        }

        if (jumlahStr.isEmpty()) {
            Toast.makeText(this, "Jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        val jumlah = jumlahStr.toDoubleOrNull()
        if (jumlah == null || jumlah <= 0) {
            Toast.makeText(this, "Masukkan jumlah yang valid", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf     = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val tanggal = sdf.format(Date())

        val transaksi = Transaksi(
            userId     = sessionManager.getUserId(),
            jenis      = Transaksi.JENIS_PEMASUKAN,
            jumlah     = jumlah,
            keterangan = keterangan,
            kategori   = kategori,
            tanggal    = tanggal
        )

        val result = transaksiRepository.tambahTransaksi(transaksi)
        if (result > 0) {
            Toast.makeText(this, "Pemasukan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Gagal menyimpan, coba lagi", Toast.LENGTH_SHORT).show()
        }
    }
}
