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

## Server

Server API Rentech ada di folder `server/`, dibangun dengan Kotlin, Ktor, dan PostgreSQL, lalu di-host di Railway. Build-nya terpisah dari aplikasi Android.

**Menjalankan tes**

```
cd server
./gradlew test
```

Tes memakai PostgreSQL sementara (embedded), jadi tidak perlu Docker.

**Variabel environment**

| Nama | Isi |
|---|---|
| `DATABASE_URL` | URL PostgreSQL. Di Railway: `${{Postgres.DATABASE_URL}}` |
| `JWT_SECRET` | Kunci acak untuk token masuk |
| `OTP_DEV` | `true` selama pengembangan, kode OTP-nya `123456` |
| `FOTO_DIR` | Folder foto. Di Railway: `/data/foto` (volume) |
| `ADMIN_EMAIL`, `ADMIN_SANDI` | Akun admin yang dibuat seed. Hanya diisi di Railway |
| `PORT` | Diisi otomatis oleh Railway |

**Data contoh**

Saat server menyala, migrasi dan seed berjalan otomatis. Seed hanya mengisi data contoh kalau tabel barang masih kosong, jadi data yang sudah ada tidak ditimpa. Akun contoh memakai sandi `rentech123`:
- `penyewa@rentech.test` dan `pemilik@rentech.test`: sudah terverifikasi
- `diperiksa@rentech.test`: verifikasinya menunggu di antrean admin
- `ditolak@rentech.test`: verifikasinya ditolak

**Deploy**

```
cd server
railway up
```

