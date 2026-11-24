# Rentech

Rentech adalah aplikasi Android untuk menyewa barang elektronik dalam waktu singkat, langsung dari pemilik barang di sekitar kita. Satu akun bisa dipakai untuk dua hal: menyewa barang orang lain, atau menyewakan barang sendiri.

## Masalah

Banyak perangkat elektronik cuma dibutuhkan sebentar. Kamera untuk acara keluarga, proyektor untuk presentasi, drone untuk satu proyek video. Membeli barang seperti itu mahal, padahal setelahnya barang itu lebih sering tersimpan di lemari.

Di sisi lain, banyak orang punya perangkat yang jarang dipakai, tapi ragu meminjamkannya ke orang yang tidak dikenal. Penyewa juga punya keraguan sendiri. Keduanya takut barang rusak, uang hilang, atau tidak ada yang bisa dimintai tolong kalau ada masalah.

## Solusi

Rentech mempertemukan penyewa dan pemilik di satu tempat, dan seluruh alurnya disusun supaya kedua pihak merasa aman:

- Setiap pengguna memverifikasi identitas dengan KTP dan swafoto sebelum bisa bertransaksi.
- Pembayaran lewat transfer ditahan Rentech dan baru diteruskan ke pemilik setelah penyewa menerima barang.
- Deposit kembali penuh kalau barang dikembalikan dalam kondisi baik.
- Kondisi barang difoto saat serah terima dan saat pengembalian, jadi ada bukti kalau terjadi sengketa.
- Penyewa dan pemilik saling memberi ulasan setelah sewa selesai.
- Tim dukungan bisa dihubungi kalau ada kendala.

## Fitur

**Untuk penyewa**
- Daftar dan masuk dengan verifikasi kode OTP
- Verifikasi identitas dengan KTP dan swafoto
- Mencari barang berdasarkan kategori, harga, jarak, dan kondisi
- Melihat detail barang, harga per hari, deposit, dan profil pemilik
- Memilih tanggal sewa dari kalender ketersediaan
- Memilih cara serah terima: ambil sendiri atau diantar pemilik
- Membayar dengan transfer bank lalu mengunggah bukti, atau membayar tunai saat serah terima
- Memantau status sewa, mengonfirmasi barang diterima, dan memberi ulasan

**Untuk pemilik**
- Memasang barang dengan foto, kategori, dan kondisi
- Mengatur harga sewa, deposit, dan tanggal ketersediaan
- Menerima atau menolak permintaan sewa
- Melihat ringkasan barang dan permintaan dari beranda pemilik

**Umum**
- Berpindah peran antara penyewa dan pemilik dari halaman profil
- Pusat bantuan
- Daftar tunggu untuk masa pra-rilis

Fitur yang sedang direncanakan: chat antara penyewa dan pemilik, berbagi lokasi saat serah terima, laporan kerusakan, dan halaman pendapatan pemilik.

## Teknologi

- Kotlin untuk aplikasi Android
- Jetpack Compose dan Material 3 untuk antarmuka (rencana)
- OpenStreetMap untuk peta lokasi barang
- PostgreSQL di Railway untuk database
