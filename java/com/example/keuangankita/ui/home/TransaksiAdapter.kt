package com.example.keuangankita.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.keuangankita.R
import com.example.keuangankita.model.Transaksi
import com.example.keuangankita.utils.CurrencyFormatter

class TransaksiAdapter(
    private val list: MutableList<Transaksi>,
    private val onLongClick: ((Transaksi) -> Unit)? = null
) : RecyclerView.Adapter<TransaksiAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvKategori: TextView  = view.findViewById(R.id.tvKategori)
        val tvKeterangan: TextView = view.findViewById(R.id.tvKeterangan)
        val tvTanggal: TextView   = view.findViewById(R.id.tvTanggal)
        val tvJumlah: TextView    = view.findViewById(R.id.tvJumlah)
        val tvIcon: TextView      = view.findViewById(R.id.tvIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaksi = list[position]

        holder.tvKategori.text   = transaksi.kategori
        holder.tvKeterangan.text = transaksi.keterangan.ifEmpty { "-" }
        holder.tvTanggal.text    = formatTanggal(transaksi.tanggal)

        if (transaksi.jenis == Transaksi.JENIS_PEMASUKAN) {
            holder.tvJumlah.text = "+ ${CurrencyFormatter.format(transaksi.jumlah)}"
            holder.tvJumlah.setTextColor(Color.parseColor("#4CAF50"))
            holder.tvIcon.text = "+"
            holder.tvIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
        } else {
            holder.tvJumlah.text = "- ${CurrencyFormatter.format(transaksi.jumlah)}"
            holder.tvJumlah.setTextColor(Color.parseColor("#F44336"))
            holder.tvIcon.text = "-"
            holder.tvIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F44336"))
        }

        holder.itemView.setOnLongClickListener {
            onLongClick?.invoke(transaksi)
            true
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Transaksi>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    private fun formatTanggal(tanggal: String): String {
        return try {
            val parts = tanggal.split("-")
            val day   = parts[2].toInt()
            val monthNames = listOf(
                "Jan","Feb","Mar","Apr","Mei","Jun",
                "Jul","Agu","Sep","Okt","Nov","Des"
            )
            val month = monthNames[parts[1].toInt() - 1]
            val year  = parts[0]
            "$day $month $year"
        } catch (e: Exception) {
            tanggal
        }
    }
}
