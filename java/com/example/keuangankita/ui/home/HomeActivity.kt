package com.example.keuangankita.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keuangankita.R
import com.example.keuangankita.repository.TransaksiRepository
import com.example.keuangankita.ui.login.LoginActivity
import com.example.keuangankita.ui.riwayat.RiwayatActivity
import com.example.keuangankita.ui.transaksi.TambahPemasukanActivity
import com.example.keuangankita.ui.transaksi.TambahPengeluaranActivity
import com.example.keuangankita.utils.CurrencyFormatter
import com.example.keuangankita.utils.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var tvSaldoTotal: TextView
    private lateinit var tvPemasukan: TextView
    private lateinit var tvPengeluaran: TextView
    private lateinit var tvUsername: TextView
    private lateinit var rvTransaksi: RecyclerView
    private lateinit var btnLihatSelengkapnya: AppCompatButton
    private lateinit var barChart: BarChartView
    private lateinit var tvEmptyChart: TextView
    private lateinit var llEmptyRiwayat: LinearLayout
    private lateinit var llLogout: LinearLayout
    private lateinit var fabTambah: FloatingActionButton
    private lateinit var fabKurang: FloatingActionButton

    private lateinit var sessionManager: SessionManager
    private lateinit var transaksiRepository: TransaksiRepository
    private lateinit var adapter: TransaksiAdapter

    private val LIMIT_RIWAYAT = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager      = SessionManager(this)
        transaksiRepository = TransaksiRepository(this)

        initViews()
        setupRecyclerView()
        setupClickListeners()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun initViews() {
        tvSaldoTotal         = findViewById(R.id.tvSaldoTotal)
        tvPemasukan          = findViewById(R.id.tvPemasukan)
        tvPengeluaran        = findViewById(R.id.tvPengeluaran)
        tvUsername           = findViewById(R.id.tvUsername)
        rvTransaksi          = findViewById(R.id.rvTransaksi)
        btnLihatSelengkapnya = findViewById(R.id.btnLihatSelengkapnya)
        barChart             = findViewById(R.id.barChart)
        tvEmptyChart         = findViewById(R.id.tvEmptyChart)
        llEmptyRiwayat       = findViewById(R.id.tvEmptyRiwayat)
        llLogout             = findViewById(R.id.tvLogout)
        fabTambah            = findViewById(R.id.fabTambah)
        fabKurang            = findViewById(R.id.fabKurang)

        tvUsername.text = "Halo, ${sessionManager.getUsername()}!"
    }

    private fun setupRecyclerView() {
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
        rvTransaksi.layoutManager = LinearLayoutManager(this)
        rvTransaksi.adapter = adapter
        rvTransaksi.isNestedScrollingEnabled = false
    }

    private fun setupClickListeners() {
        fabTambah.setOnClickListener {
            startActivity(Intent(this, TambahPemasukanActivity::class.java))
        }
        fabKurang.setOnClickListener {
            startActivity(Intent(this, TambahPengeluaranActivity::class.java))
        }
        btnLihatSelengkapnya.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }
        llLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    sessionManager.logout()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }

    private fun loadData() {
        val userId    = sessionManager.getUserId()
        val yearMonth = getCurrentYearMonth()

        // Saldo total
        val saldo = transaksiRepository.getTotalSaldo(userId)
        tvSaldoTotal.text = CurrencyFormatter.format(saldo)

        // Pemasukan & pengeluaran bulan ini
        val pemasukan  = transaksiRepository.getTotalPemasukanBulanIni(userId, yearMonth)
        val pengeluaran = transaksiRepository.getTotalPengeluaranBulanIni(userId, yearMonth)
        tvPemasukan.text  = CurrencyFormatter.format(pemasukan)
        tvPengeluaran.text = CurrencyFormatter.format(pengeluaran)

        // Grafik
        val kategoriData = transaksiRepository.getPengeluaranPerKategori(userId, yearMonth)
        if (kategoriData.isEmpty()) {
            barChart.visibility    = View.GONE
            tvEmptyChart.visibility = View.VISIBLE
        } else {
            barChart.visibility    = View.VISIBLE
            tvEmptyChart.visibility = View.GONE
            barChart.setData(kategoriData)
        }

        // Riwayat
        val recentList = transaksiRepository.getRecentTransaksi(userId, LIMIT_RIWAYAT)
        val totalCount = transaksiRepository.getTotalCountTransaksi(userId)

        if (recentList.isEmpty()) {
            llEmptyRiwayat.visibility = View.VISIBLE
            rvTransaksi.visibility    = View.GONE
        } else {
            llEmptyRiwayat.visibility = View.GONE
            rvTransaksi.visibility    = View.VISIBLE
            adapter.updateData(recentList)
        }

        btnLihatSelengkapnya.visibility =
            if (totalCount > LIMIT_RIWAYAT) View.VISIBLE else View.GONE
    }

    private fun getCurrentYearMonth(): String {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return sdf.format(Date())
    }
}
