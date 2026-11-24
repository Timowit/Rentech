# Rancangan: server, database, dan dasbor admin Rentech

Tanggal: 2026-11-24

## Kenapa

Semua layar Rentech masih memakai data contoh yang ditulis langsung di kode: barang, foto, pesanan, notifikasi, dan chat. Alur keamanan yang dijanjikan aplikasi butuh pihak ketiga yang benar-benar memeriksa, yaitu "tim Rentech". Tim inilah yang memvalidasi identitas, memeriksa bukti transfer, dan membalas Hubungi tim. Peran tim itu belum ada.

Tujuan rancangan ini:
- Data tersimpan di PostgreSQL di Railway.
- Aplikasi mengambil data dari server.
- Ada admin yang bisa memeriksa bahwa swafoto pengguna cocok dengan KTP-nya.

## Tujuan
- Data contoh tetap ada, tetapi dimasukkan ke database lewat *seed*, termasuk foto barang. Composable tidak lagi menyimpan daftar data atau memilih foto sendiri.
- Ada server API yang dipakai aplikasi Android.
- Ada dasbor web admin. Isinya, setelah admin masuk:
  1. **Verifikasi identitas**: foto KTP dan swafoto ditampilkan berdampingan bersama data diri. Admin memilih *Terima* atau *Tolak* dengan satu atau beberapa alasan.
  2. **Bukti transfer**: admin melihat nominal yang harus dibayar (termasuk kode unik) di samping foto bukti, lalu memilih *Sesuai* atau *Tidak sesuai*.
  3. **Hubungi tim**: daftar percakapan dari layar G04. Admin membalas dari dasbor.
- Semua layar aplikasi dipindah ke data server.

## Bukan tujuan (untuk putaran ini)
- SMS OTP sungguhan. Kodenya tetap `123456` selama mode pengembangan.
- Payment gateway. Pembayaran tetap transfer manual atau tunai, sesuai keputusan 2026-11-23.
- Peta, lokasi langsung, dan jarak sungguhan.
- Chat waktu nyata lewat websocket. Polling sudah cukup.
- Halaman pendapatan pemilik, pengembalian barang, dan laporan kerusakan.

## Anti-tujuan (gagal walau "jalan")
- Foto KTP atau swafoto bisa dibuka lewat tautan publik atau tanpa login admin.
- Kata sandi database atau kunci rahasia ada di APK atau di repo.
- Pengguna biasa bisa memanggil aksi admin, misalnya mengubah `admin=true` lewat API.
- Statistik, jumlah pengguna, rating, atau testimoni yang dikarang. Seed hanya berisi data contoh yang sudah ada di desain.

## Batasan
- Bahasa Kotlin, supaya kedua anggota tim bisa membaca semua bagian kode.
- Hosting di Railway, dengan akun yang login lewat GitHub Timowit.
- Repo yang sama. Server berada di folder `server/` sebagai modul Gradle terpisah.
- Teks antarmuka tetap berbahasa Indonesia, mengikuti gaya yang sudah ada.

## Pendekatan yang dipertimbangkan
- **A. Ktor + dasbor admin web di server yang sama. Dipilih.** Satu layanan, satu bahasa. Admin memeriksa foto di layar besar, dan kode admin tidak ikut di APK.
- B. Ktor + mode admin di aplikasi Android. Lebih cepat dibuat, tetapi membandingkan foto di layar HP rawan salah, dan layar admin ikut terpasang di APK publik.
- C. Node.js + React. Menambah dua bahasa baru ke proyek yang seluruhnya Kotlin.

## Bentuk sistem

```
Aplikasi Android ──HTTPS/JSON──▶ Server Ktor (Railway) ──▶ PostgreSQL (Railway)
Browser admin ────HTTPS/HTML──▶      └─ /admin                └─ Volume /data (foto)
```

### Server (`server/`)
- Ktor 3, kotlinx.serialization, HikariCP, dan Flyway untuk migrasi skema. Akses database memakai Exposed. *(Keputusanku: Exposed cukup ringan, dan pengguna Kotlin bisa membacanya tanpa belajar SQL ORM lain.)*
- Autentikasi aplikasi memakai JWT, dikirim di header `Authorization`. Kata sandi disimpan sebagai hash bcrypt.
- Autentikasi dasbor memakai sesi cookie (`HttpOnly`, `Secure`) dan formulir masuk di `/admin/masuk`. Setiap rute `/admin/*` memeriksa `admin = true` di server.
- Halaman admin dirender server dengan kotlinx.html, tanpa framework frontend. Tampilannya memakai warna dan huruf Rentech (cobalt #2346F5, Inter dan Roboto).
- Konfigurasi dibaca dari environment Railway: `DATABASE_URL`, `JWT_SECRET`, `ADMIN_EMAIL`, `ADMIN_SANDI`, `OTP_DEV=true`, dan `FOTO_DIR=/data/foto`.

### Foto
- Disimpan di Volume Railway yang di-mount ke `/data`. Database hanya menyimpan path relatifnya.
- Foto barang dan avatar bersifat publik di `GET /foto/barang/{nama}`.
- Foto KTP, swafoto, dan bukti transfer bersifat privat di `GET /admin/foto/{id}`. Rute ini hanya bisa dibuka lewat sesi admin. Pemiliknya boleh mengunggah foto, tetapi tidak bisa membukanya lagi lewat URL publik.
- Ukuran unggahan dibatasi 5 MB, dan hanya JPEG/PNG yang diterima.

### Skema database (inti)

| Tabel | Isi penting |
|---|---|
| `pengguna` | nama, email unik, no_hp, sandi_hash, foto, `peran_aktif` (penyewa/pemilik), `admin` (bool), `status_verifikasi` (belum/diperiksa/terverifikasi/ditolak) |
| `verifikasi` | pengguna_id, foto_ktp, foto_swafoto, nik, status, `alasan` (teks[]), diperiksa_oleh, diperiksa_pada |
| `kategori` | nama, urutan |
| `barang` | pemilik_id, kategori_id, nama, harga_per_hari, deposit, kelengkapan, lokasi, jarak_demo, tersedia |
| `foto_barang` | barang_id, path, urutan |
| `pesanan` | barang_id, penyewa_id, mulai, selesai, diantar, slot, metode (transfer/tunai), kode_unik, biaya_sewa, biaya_layanan, biaya_antar, deposit, total, `status` (menunggu_pemilik/diterima_pemilik/ditolak_pemilik/berjalan/selesai/dibatalkan), `status_dana` (belum/menunggu_cek/ditahan/diteruskan/tunai), bukti_transfer, alasan_bukti, diterima_penyewa_pada |
| `ulasan` | pesanan_id, dari_id, untuk_id, bintang, isi |
| `pesan` | pesanan_id atau barang_id, pengirim_id, penerima_id, teks, dibaca, dibuat |
| `tiket_bantuan` | pengguna_id, topik, status (terbuka/selesai) |
| `pesan_bantuan` | tiket_id, dari_admin (bool), teks, dibuat |
| `notifikasi` | pengguna_id, jenis, judul, isi, tujuan, dibaca, dibuat |

Tanggal yang tidak tersedia dihitung dari pesanan yang aktif, bukan dari daftar tetap.

### Alur verifikasi
1. Pengguna mengunggah KTP dan swafoto di A10. Status berubah menjadi `diperiksa`, lalu aplikasi menampilkan A11.
2. Admin membuka antrean di dasbor dan melihat KTP serta swafoto berdampingan.
3. *Terima*: status menjadi `terverifikasi`, dan notifikasi "Identitas kamu terverifikasi" dikirim.
4. *Tolak*: admin mencentang alasan (pilihan baku seperti "Foto KTP buram, nomor NIK tidak terbaca" dan "Wajah pada swafoto tertutup bayangan", ditambah kolom teks bebas). Status menjadi `ditolak`, aplikasi menampilkan A12 dengan alasan itu, lalu notifikasi dikirim.
5. Pengguna yang belum `terverifikasi` tidak bisa membuat pesanan atau memasang barang. Server menolaknya dengan 403.

### Alur bukti transfer
1. Pengguna mengunggah bukti di C05. `status_dana` menjadi `menunggu_cek`.
2. Admin memilih *Sesuai*: `status_dana` menjadi `ditahan`, lalu notifikasi "Bukti transfer sudah diperiksa" dikirim. Admin memilih *Tidak sesuai*: alasan dikirim ke pengguna, dan pengguna bisa mengunggah ulang.
3. Setelah penyewa mengonfirmasi barang diterima, `status_dana` menjadi `diteruskan`.

### Seed
- Dijalankan satu kali lewat perintah `./gradlew :server:seed`, atau otomatis kalau tabel `barang` masih kosong. Data yang sudah ada tidak ditimpa.
- Isinya:
  - 8 kategori dan 7 barang dari `ContohBarang.kt`. File fotonya disalin dari `app/src/main/res` ke `server/seed/foto/`, lalu ke volume.
  - Akun demo: satu penyewa terverifikasi, satu pemilik terverifikasi, satu pengguna `diperiksa` dengan foto contoh untuk antrean admin, dan satu pengguna `ditolak`.
  - Pesanan, permintaan masuk, riwayat untuk ulasan, percakapan, dan notifikasi, sesuai isi layar di Figma.
- Akun admin diambil dari `ADMIN_EMAIL` dan `ADMIN_SANDI`, tidak pernah ditulis di repo.
- Foto KTP contoh harus jelas palsu, misalnya bertanda "CONTOH". KTP orang sungguhan tidak boleh dipakai.

### Aplikasi Android
- Ditambah lapisan `data/`: klien HTTP (Retrofit atau Ktor client dengan kotlinx.serialization), repository per fitur, dan ViewModel per layar. Gambar dimuat dengan Coil dari URL.
- Composable menerima data lewat parameter, jadi tidak ada lagi `contoh…` di dalam layar. Preview tetap boleh memakai data contoh yang ditaruh di file khusus preview.
- Alamat server diatur lewat `BuildConfig.API_URL`: debug ke Railway (atau `10.0.2.2` saat lokal), release ke Railway.
- Token JWT disimpan di DataStore.
- Setiap layar punya keadaan memuat, gagal (dengan tombol Coba lagi), dan kosong, memakai gaya komponen yang sudah ada.
- Chat dan Hubungi tim mengambil pesan baru setiap 5 detik selama layar terbuka.
- Teks tetap seperti onboarding, FAQ Bantuan, dan pilar Perlindungan tetap di kode, karena itu tulisan antarmuka, bukan data.

### Railway
- Satu proyek berisi PostgreSQL, layanan `server` (root `server/`, dibangun dengan Gradle atau Dockerfile), dan Volume di `/data`.
- Domain publik bawaan Railway dipakai untuk API dan `/admin`.

## Keputusan yang sudah diambil
- Pakai A: Ktor dengan dasbor web admin (dipilih pengguna).
- OTP memakai kode tetap `123456` selama mode pengembangan (disetujui pengguna).
- Foto disimpan privat di volume Railway (disetujui pengguna).
- Data contoh boleh ada, tetapi harus lewat database (permintaan pengguna).
- Semua layar dipindah ke data server (keputusanku, mengikuti "jangan dummy lagi").
- Atribusi commit (pilihan pengguna, dibagi dua):
  - Server, database, seed, dan lapisan data Android dicommit atas nama Timowit.
  - Dasbor admin dicommit atas nama marifw11.
  - Perubahan di layar dicommit atas nama pemilik layarnya sesuai pembagian di Figma.
- Dasbor berisi verifikasi, bukti transfer, dan Hubungi tim. Verifikasi diminta pengguna. Dua lainnya keputusanku, karena tanpa keduanya layar C05, D02, dan G04 tidak bisa jalan dengan data sungguhan.

## Diserahkan ke pembangun
- Struktur paket server, nama endpoint, dan bentuk JSON.
- Cara menguji: Ktor `testApplication` dengan Postgres sungguhan (Testcontainers kalau Docker tersedia, kalau tidak embedded Postgres).
- Urutan migrasi Flyway.
- Detail tampilan dasbor, asalkan mengikuti warna dan huruf Rentech dan menampilkan KTP serta swafoto berdampingan.
