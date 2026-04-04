package com.example.keuangankita.ui.riwayat

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keuangankita.R
import com.example.keuangankita.repository.TransaksiRepository
import com.example.keuangankita.ui.home.TransaksiAdapter
import com.example.keuangankita.utils.SessionManager

class RiwayatActivity : AppCompatActivity() {

    private lateinit var rvRiwayat: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvBack: TextView

    private lateinit var transaksiRepository: TransaksiRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: TransaksiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        transaksiRepository = TransaksiRepository(this)
        sessionManager      = SessionManager(this)

        rvRiwayat = findViewById(R.id.rvRiwayat)
        tvEmpty   = findViewById(R.id.tvEmpty)
        tvBack    = findViewById(R.id.tvBack)

        tvBack.setOnClickListener { finish() }

        adapter = TransaksiAdapter(mutableListOf()) { transaksi ->
            AlertDialog.Builder(this)
                .setTitle("Hapus Transaksi")
                .setMessage("Yakin ingin menghapus transaksi ini?")
                .setPositiveButton("Hapus") { _, _ ->
                    transaksiRepository.hapusTransaksi(transaksi.id)
                    loadData()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        rvRiwayat.layoutManager = LinearLayoutManager(this)
        rvRiwayat.adapter = adapter

        loadData()
    }

    private fun loadData() {
        val userId = sessionManager.getUserId()
        val list   = transaksiRepository.getAllTransaksi(userId)

        if (list.isEmpty()) {
            tvEmpty.visibility   = View.VISIBLE
            rvRiwayat.visibility = View.GONE
        } else {
            tvEmpty.visibility   = View.GONE
            rvRiwayat.visibility = View.VISIBLE
            adapter.updateData(list)
        }
    }
}
