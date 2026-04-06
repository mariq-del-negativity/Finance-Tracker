# Finance Tracker (KeuanganKita)

Aplikasi Android Finance Tracker sederhana namun lengkap untuk mencatat, mengelola, dan memantau keuangan pribadi yang berjalan secara lokal di perangkat anda.

## Tentang Aplikasi

**Finance Tracker** (nama internal: **KeuanganKita**) adalah aplikasi mobile berbasis Android yang membantu pengguna mengelola keuangan pribadi dengan lebih disiplin. Aplikasi ini memungkinkan pencatatan transaksi pemasukan dan pengeluaran, pengelompokan berdasarkan kategori, visualisasi data melalui grafik, serta pembuatan laporan bulanan.

## Fitur Utama
- Pencatatan transaksi pemasukan dan pengeluaran secara real-time
- Pengelompokan transaksi berdasarkan kategori
- Visualisasi data keuangan dalam bentuk grafik dan laporan
- Pengaturan anggaran bulanan
- Ringkasan saldo dan progres tabungan
- Antarmuka yang sederhana dan user-friendly

## Teknologi yang Digunakan
- Kotlin
- Android Studio  
- SQLite (SQLiteOpenHelper)
- RecyclerView
- Material FloatingActionButton
- Custom Canvas Chart (tanpa library)
- Repository Pattern
- SharedPreferences (session)

## Struktur Folder Proyek

Repository ini berisi isi folder `app/src/main` dari proyek Android Studio asli. Berikut struktur foldernya:

<img width="841" height="432" alt="Screenshot_20260404_231622" src="https://github.com/user-attachments/assets/f602205e-6643-43c4-a8af-134b6c1b0655" />


> Catatan: Repository ini berisi isi folder `app/src/main` dari proyek Android Studio asli. Untuk menjalankan proyek secara lengkap, disarankan membuka di Android Studio setelah di-clone.


## Langka-langkah
- Buka Android Studio dan buat projek baru dan pilih [Empty views activity], namai projeknya sebagai "KeuanganKita"
- Buka terminal di Android Studio dan jalankan
```bash 
git clone https://github.com/mariq-del-negativity/Finance-Tracker.git
```
- Buka file manager lalu cari foldernya
- Ganti/timpa semua isi folder `~/AndroidStudioProjects/KeuanganKita/app/src/main/` dengan isi folder  Finance Tracker yang sudah di clone dari repository ini
- Kembali ke Android Studio ~~> sync gradle ~~> run app
