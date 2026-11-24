# Server & Database Rentech: Rencana Implementasi (Bagian 1 dari 3)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Membangun server Ktor yang tersambung ke PostgreSQL di Railway. Server ini menyediakan semua API yang dibutuhkan aplikasi, ditambah fungsi layanan admin yang nanti dipakai dasbor, dan diisi data contoh lewat seed.

**Architecture:**
- Build Gradle mandiri di `server/`. Root Railway diarahkan ke folder ini, dan build Android tidak tersentuh.
- Ktor melayani JSON untuk aplikasi.
- Exposed dipakai untuk akses data dan Flyway untuk skema.
- Foto disimpan di volume, dengan pemisahan publik dan privat.
- Logika bisnis ditulis di kelas `…Layanan` supaya bisa dipakai ulang oleh dasbor admin (Bagian 2).

**Tech Stack:**
- Kotlin JVM (toolchain 21) dan Ktor 3 (server-netty, content-negotiation + kotlinx-json, auth-jwt, status-pages).
- Exposed (core, jdbc, java-time), Flyway (flyway-database-postgresql), HikariCP, dan driver postgresql.
- `at.favre.lib:bcrypt` untuk hash kata sandi.
- Untuk tes: `io.zonky.test:embedded-postgres` dan ktor-server-test-host.
- Pakai versi stabil terbaru di Maven Central saat implementasi. Versi Kotlin plugin = `2.2.10`, sama dengan `gradle/libs.versions.toml`.

**Spec:** `docs/superpowers/specs/2026-11-24-server-admin-database-design.md`

**Bagian berikutnya:**
- Bagian 2: dasbor admin, atas nama marifw11.
- Bagian 3: pemindahan layar Android ke data server, dicommit atas nama pemilik layarnya.

Masing-masing akan punya rencana sendiri setelah bagian ini selesai.

## Global Constraints
- Semua commit di rencana ini dibuat atas nama **Timowit**. Jalankan `git pakai-timo` lalu cek dengan `git siapa` sebelum commit.
- Identifier, pesan error, dan teks untuk pengguna ditulis dalam bahasa Indonesia, mengikuti gaya aplikasi (misalnya `pengguna`, `barang`, `pesanan`).
- Rahasia hanya dibaca dari environment: `DATABASE_URL`, `JWT_SECRET`, `ADMIN_EMAIL`, `ADMIN_SANDI`, `OTP_DEV`, `FOTO_DIR`, `PORT`. Rahasia tidak boleh ada di repo.
- Kode OTP selama `OTP_DEV=true` adalah `123456`. Kalau `OTP_DEV` bukan `true`, verifikasi OTP selalu gagal, karena belum ada pengirim SMS.
- Unggahan foto maksimal **5 MB**, dan hanya `image/jpeg` atau `image/png` yang diterima.
- Foto KTP, swafoto, dan bukti transfer **tidak pernah** dilayani lewat rute publik.
- Seed tidak boleh mengarang statistik, rating, jumlah pengguna, atau testimoni. Isinya hanya data contoh yang sudah ada di app/desain. Foto KTP contoh harus bertuliskan "CONTOH".
- `pengguna.admin` tidak bisa diubah lewat API apa pun. Nilainya hanya bisa diatur oleh seed dari `ADMIN_EMAIL`.

## Review Focus
1. **Pengguna biasa meminta foto privat atau memanggil layanan admin** → harus 403, dan 401 kalau tanpa token. Diuji di Task 5.
2. **Dua pesanan dengan rentang tanggal tumpang tindih untuk barang yang sama** → pesanan kedua mendapat 409. Pesanan yang `ditolak_pemilik` atau `dibatalkan` tidak memblokir tanggal. Diuji di Task 6.
3. **Unggahan lebih dari 5 MB atau bukan gambar** (misalnya PDF berekstensi .jpg, dicek dari *magic bytes*) → 413 atau 415, dan tidak ada file yang tertinggal di disk. Diuji di Task 4.
4. **`DATABASE_URL` dari Railway berformat `postgresql://user:pass@host:port/db`** → harus diubah menjadi JDBC URL beserta user dan sandinya. Diuji di Task 1.
5. **Seed dijalankan dua kali** → tidak ada data ganda dan data yang sudah ada tidak ditimpa. Diuji di Task 8.

---

### Task 1: Kerangka server, konfigurasi, dan health check

**Files:**
- Create: `server/settings.gradle.kts`, `server/build.gradle.kts`, `server/gradle/wrapper/*`, `server/gradlew` (salin wrapper dari root), `server/.gitignore` (`build/`, `.gradle/`)
- Create: `server/src/main/kotlin/com/rentech/server/Aplikasi.kt`
- Create: `server/src/main/kotlin/com/rentech/server/Konfigurasi.kt`
- Test: `server/src/test/kotlin/com/rentech/server/KonfigurasiTest.kt`, `AplikasiTest.kt`

**Interfaces:**
- Produces:
  - `data class Konfigurasi(val jdbcUrl: String, val dbUser: String, val dbSandi: String, val jwtSecret: String, val adminEmail: String?, val adminSandi: String?, val otpDev: Boolean, val fotoDir: java.nio.file.Path)`
  - `fun Konfigurasi.Companion.dariEnv(env: Map<String, String> = System.getenv()): Konfigurasi`
  - `fun ubahDatabaseUrl(url: String): Triple<String, String, String>`, yang mengembalikan (jdbcUrl, user, sandi)
  - `fun Application.modul(k: Konfigurasi, db: javax.sql.DataSource)`, yang menjadi titik pasang semua rute di task berikutnya
  - `fun main()`, yang membaca `PORT` (default 8080)

- [ ] **Step 1: Tulis tes yang gagal**
  - `ubahDatabaseUrl("postgresql://u:p@h:5432/railway")` harus bernilai `Triple("jdbc:postgresql://h:5432/railway", "u", "p")`. Format `postgres://` juga harus diterima.
  - `dariEnv(mapOf(... "OTP_DEV" to "true"))` menghasilkan `otpDev == true`. Kalau `JWT_SECRET` tidak ada, hasilnya `IllegalStateException` dengan pesan yang menyebut `JWT_SECRET`.
  - Dengan `testApplication`, `GET /sehat` harus mengembalikan 200 dan body `{"status":"ok"}`.
- [ ] **Step 2:** `cd server && JAVA_HOME=/opt/android-studio/jbr ./gradlew test` harus **FAIL** (simbol belum ada).
- [ ] **Step 3:** Implementasikan.
  - Pasang `ContentNegotiation(json)` dan `StatusPages` di `modul`.
  - StatusPages memetakan `Gagal(status, pesan)` ke JSON `{"pesan": "..."}`. `class Gagal(val status: HttpStatusCode, pesan: String) : RuntimeException(pesan)` ditaruh di `Aplikasi.kt`, dan task lain memakainya.
  - Tambahkan plugin Ktor `io.ktor.plugin` supaya ada `buildFatJar`.
- [ ] **Step 4:** Jalankan `./gradlew test`, hasilnya harus **PASS**.
- [ ] **Step 5:** Commit dengan pesan `feat(server): kerangka server ktor dan konfigurasi`.

### Task 2: Skema database dan harness tes

**Files:**
- Create: `server/src/main/resources/db/migration/V1__skema_awal.sql`
- Create: `server/src/main/kotlin/com/rentech/server/db/Database.kt`, `db/Tabel.kt`
- Test: `server/src/test/kotlin/com/rentech/server/UjiDb.kt` (helper), `db/MigrasiTest.kt`

**Interfaces:**
- Produces:
  - `fun buatDataSource(k: Konfigurasi): HikariDataSource`
  - `fun migrasi(ds: DataSource)`, yang menjalankan Flyway
  - Objek Exposed: `Pengguna`, `Verifikasi`, `Kategori`, `Barang`, `FotoBarang`, `Pesanan`, `Ulasan`, `Pesan`, `TiketBantuan`, `PesanBantuan`, `Notifikasi`. Kolom dan enum-nya persis sama dengan tabel skema di spec (nama snake_case, enum disimpan sebagai `text` dengan `CHECK`).
  - Helper tes `object UjiDb { val ds: DataSource; fun bersihkan() }`. Helper ini menyalakan satu embedded Postgres per JVM, menjalankan `migrasi`, lalu `bersihkan()` men-TRUNCATE semua tabel. Ada juga `fun ujiAplikasi(blok: suspend ApplicationTestBuilder.() -> Unit)`, yang memasang `modul` dengan `Konfigurasi` tes (`otpDev=true`, `fotoDir` = folder sementara).

- [ ] **Step 1:** Tulis tes yang gagal. `MigrasiTest`: setelah `migrasi(UjiDb.ds)`, query `information_schema.tables` memuat ke-11 tabel. Menyisipkan `pengguna` dengan `status_verifikasi='asal'` harus melempar `SQLException` karena CHECK.
- [ ] **Step 2:** Jalankan `./gradlew test --tests '*MigrasiTest*'`, hasilnya harus **FAIL**.
- [ ] **Step 3:** Tulis `V1__skema_awal.sql` dan `Tabel.kt`.
  - `pesanan.kode_unik` memakai `smallint` dengan nilai 100–999.
  - `verifikasi.alasan` memakai `text[]`.
  - Semua `dibuat` memakai `timestamptz default now()`.
- [ ] **Step 4:** Tes harus **PASS**.
- [ ] **Step 5:** Commit dengan pesan `feat(server): skema database dan migrasi`.

### Task 3: Akun, OTP pengembangan, dan JWT

**Files:**
- Create: `server/src/main/kotlin/com/rentech/server/akun/AkunRute.kt`, `akun/Token.kt`
- Test: `server/src/test/kotlin/com/rentech/server/akun/AkunTest.kt`

**Interfaces:**
- Produces:
  - Rute:
    - `POST /akun/daftar {nama,email,noHp,sandi}`. Hasil sukses 201 `{id}`. Email yang sudah dipakai menghasilkan 409.
    - `POST /akun/otp {email,kode}` → 200 `{token}`.
    - `POST /akun/masuk {email,sandi}` → 200 `{token}`. Kredensial salah menghasilkan 401.
    - `GET /akun/saya` → `PenggunaDto`.
    - `PATCH /akun/saya {peranAktif}`.
  - `@Serializable data class PenggunaDto(val id: Int, val nama: String, val email: String, val noHp: String, val foto: String?, val peranAktif: String, val statusVerifikasi: String, val alasanDitolak: List<String>)`
  - `fun ApplicationCall.penggunaId(): Int`, yang membaca id dari JWT (`authenticate("app")`)
  - `fun buatToken(k: Konfigurasi, penggunaId: Int): String`, dengan masa berlaku 30 hari

- [ ] **Step 1:** Tulis tes yang gagal.
  - Alur daftar → otp `123456` → `/akun/saya` mengembalikan `statusVerifikasi == "belum"`.
  - OTP `000000` menghasilkan 401.
  - `/akun/saya` tanpa token menghasilkan 401.
  - Daftar dengan email yang sama dua kali menghasilkan 409.
  - `PATCH /akun/saya` dengan body `{"admin":true}` tidak mengubah kolom `admin`, dan nilainya tetap `false` di DB.
- [ ] **Step 2:** Tes harus **FAIL**.
- [ ] **Step 3:** Implementasikan.
  - Sandi di-hash dengan bcrypt cost 12.
  - `PATCH` hanya membaca `peranAktif` (nilai `penyewa` atau `pemilik`). Nilai lain menghasilkan 400.
- [ ] **Step 4:** Tes harus **PASS**.
- [ ] **Step 5:** Commit dengan pesan `feat(server): daftar, masuk, otp pengembangan, dan jwt`.

### Task 4: Penyimpanan foto

**Files:**
- Create: `server/src/main/kotlin/com/rentech/server/foto/Penyimpanan.kt`, `foto/FotoRute.kt`
- Test: `server/src/test/kotlin/com/rentech/server/foto/PenyimpananTest.kt`

**Interfaces:**
- Produces:
  - `class Penyimpanan(dir: Path) { fun simpan(bytes: ByteArray, privat: Boolean): String; fun baca(path: String): Path? }`
    - `simpan` mengembalikan path relatif `publik/<uuid>.jpg` atau `privat/<uuid>.jpg`.
    - `simpan` melempar `Gagal(413)` kalau ukurannya lebih dari 5 MB, dan `Gagal(415)` kalau *magic bytes* bukan JPEG (`FF D8 FF`) atau PNG (`89 50 4E 47`).
  - `suspend fun ApplicationCall.terimaFoto(namaField: String): ByteArray`, yang membaca multipart dan berhenti membaca setelah 5 MB + 1 byte
  - Rute `GET /foto/{path...}` hanya melayani awalan `publik/`. `privat/` atau `..` menghasilkan 404.

- [ ] **Step 1:** Tulis tes yang gagal.
  - JPEG kecil tersimpan di `publik/`, lalu bisa diambil lewat `GET /foto/...` dengan status 200.
  - Byte PDF (`%PDF`) menghasilkan 415, dan folder tetap kosong.
  - Data 5 MB + 1 byte menghasilkan 413.
  - `GET /foto/privat/x.jpg` dan `GET /foto/publik/../privat/x.jpg` menghasilkan 404.
- [ ] **Step 2:** Tes harus **FAIL**.
- [ ] **Step 3:** Implementasikan. File baru ditulis dulu ke file sementara, lalu dipindah dengan `Files.move` atomik.
- [ ] **Step 4:** Tes harus **PASS**.
- [ ] **Step 5:** Commit dengan pesan `feat(server): penyimpanan foto publik dan privat`.

### Task 5: Verifikasi identitas dan layanan admin verifikasi

**Files:**
- Create: `server/src/main/kotlin/com/rentech/server/verifikasi/VerifikasiRute.kt`, `verifikasi/VerifikasiLayanan.kt`
- Create: `server/src/main/kotlin/com/rentech/server/notifikasi/NotifikasiLayanan.kt`
- Test: `server/src/test/kotlin/com/rentech/server/verifikasi/VerifikasiTest.kt`

**Interfaces:**
- Consumes: `Penyimpanan`, `penggunaId()`
- Produces:
  - Rute `POST /verifikasi` (multipart `ktp`, `swafoto`, field `nik`) → 202. Setelah itu `statusVerifikasi` pengguna menjadi `diperiksa`.
  - `class VerifikasiLayanan(penyimpanan, notifikasi)`, berisi:
    - `fun antrean(): List<VerifikasiAntreanDto>`
    - `fun terima(verifikasiId: Int, adminId: Int)`
    - `fun tolak(verifikasiId: Int, adminId: Int, alasan: List<String>)`, yang melempar `Gagal(400)` kalau alasan kosong
    - `fun pastikanAdmin(penggunaId: Int)`, yang melempar `Gagal(403)` kalau pengguna bukan admin. Bagian 2 memanggil ini di setiap rute `/admin`.
  - `class NotifikasiLayanan { fun kirim(penggunaId: Int, jenis: String, judul: String, isi: String, tujuan: String) }`. Nilai `jenis` sama dengan nama enum `Jenis` di `NotifikasiScreen.kt` (`diterima`, `dana`, `pesan`, `pengingat`, `ulasan`, `terverifikasi`), ditambah `ditolak`.
  - `val ALASAN_BAKU = listOf("Foto KTP buram, nomor NIK tidak terbaca", "Wajah pada swafoto tertutup bayangan", "Wajah pada swafoto tidak sama dengan foto KTP", "Data diri tidak sesuai dengan KTP")`

- [ ] **Step 1:** Tulis tes yang gagal.
  - Unggah verifikasi → `antrean()` berisi 1 entri.
  - `terima` → status `terverifikasi`, dan ada notifikasi dengan judul `"Identitas kamu terverifikasi"`.
  - `tolak` dengan 2 alasan → `/akun/saya` mengembalikan `alasanDitolak` yang sama.
  - `tolak` dengan daftar kosong menghasilkan 400.
  - `pastikanAdmin(idPenggunaBiasa)` menghasilkan 403.
  - Unggah tanpa token menghasilkan 401.
  - Path file KTP berawalan `privat/`.
- [ ] **Step 2:** Tes harus **FAIL**.
- [ ] **Step 3:** Implementasikan. Setiap `terima` dan `tolak` berjalan dalam satu transaksi: update verifikasi, update pengguna, lalu sisipkan notifikasi.
- [ ] **Step 4:** Tes harus **PASS**.
- [ ] **Step 5:** Commit dengan pesan `feat(server): verifikasi identitas dan layanan admin`.

### Task 6: Barang, kategori, dan pesanan

**Files:**
- Create: `server/src/main/kotlin/com/rentech/server/barang/BarangRute.kt`
- Create: `server/src/main/kotlin/com/rentech/server/pesanan/PesananRute.kt`, `pesanan/PesananLayanan.kt`
- Test: `server/src/test/kotlin/com/rentech/server/barang/BarangTest.kt`, `pesanan/PesananTest.kt`

**Interfaces:**
- Produces:
  - Rute barang:
    - `GET /kategori` → `List<String>`, urut menurut `urutan`.
    - `GET /barang?q=&kategori=&hargaMaks=` → `List<BarangDto>`.
    - `GET /barang/{id}` → `BarangDto`, dengan 404 kalau tidak ada.
    - `GET /barang/{id}/tanggal-terpakai` → `List<String>` (ISO date).
    - `POST /barang` (pemilik terverifikasi, multipart).
    - `GET /pemilik/{id}` → profil beserta barangnya.
  - `@Serializable data class BarangDto(val id: Int, val nama: String, val kategori: String, val foto: List<String>, val hargaPerHari: Long, val deposit: Long, val jarak: String, val kelengkapan: String, val lokasi: String, val pemilik: PemilikRingkasDto)`. `foto` berisi URL relatif `/foto/publik/...`.
  - Rute pesanan:
    - `POST /pesanan {barangId, mulai, selesai, diantar, slot, metode}` → `PesananDto`.
    - `GET /pesanan/saya`.
    - `GET /pesanan/masuk` (pemilik).
    - `POST /pesanan/{id}/terima` dan `/tolak` (pemilik barang saja).
    - `POST /pesanan/{id}/bukti` (multipart `bukti`) → `statusDana = menunggu_cek`.
    - `POST /pesanan/{id}/diterima` (penyewa) → status `berjalan`, dan kalau sebelumnya `ditahan`, `statusDana` menjadi `diteruskan`.
  - `PesananLayanan.periksaBukti(pesananId: Int, adminId: Int, sesuai: Boolean, alasan: String?)`, dipakai di Bagian 2.
  - Biaya dihitung di server, nilainya sama dengan `DraftSewa.kt`: `BIAYA_LAYANAN = 25_000`, `BIAYA_ANTAR = 25_000`, `total = hari*harga + layanan + antar + deposit`. Untuk metode `transfer`, total ditambah `kode_unik` (100–999).

- [ ] **Step 1:** Tulis tes yang gagal.
  - Pencarian `q=kamera` hanya mengembalikan barang yang namanya mengandung "kamera" (tidak peka huruf besar/kecil).
  - Pengguna dengan status `belum` yang membuat pesanan mendapat 403.
  - Pesanan 12–15 Des lalu 14–16 Des untuk barang yang sama: pesanan kedua mendapat 409.
  - Setelah pesanan pertama `ditolak_pemilik`, pesanan 14–16 Des boleh dibuat (201).
  - `selesai <= mulai` menghasilkan 400.
  - Total 3 hari kamera (350.000, deposit 2.000.000), ambil sendiri, tunai = `3_075_000`.
  - Bukan pemilik yang memanggil `/terima` mendapat 403.
  - `periksaBukti(sesuai=true)` → `statusDana = ditahan`, dan ada notifikasi berjudul `"Bukti transfer sudah diperiksa"`.
- [ ] **Step 2:** Tes harus **FAIL**.
- [ ] **Step 3:** Implementasikan.
  - Cek bentrok tanggal dalam transaksi `SERIALIZABLE`. Hanya pesanan berstatus `menunggu_pemilik`, `diterima_pemilik`, atau `berjalan` yang memblokir tanggal.
  - Notifikasi "Pemilik menerima pesananmu" dikirim saat pemilik menerima pesanan.
- [ ] **Step 4:** Tes harus **PASS**.
- [ ] **Step 5:** Commit dengan pesan `feat(server): barang, kategori, dan pesanan`.

### Task 7: Ulasan, chat, notifikasi, dan Hubungi tim

**Files:**
- Create: `server/src/main/kotlin/com/rentech/server/sosial/UlasanRute.kt`, `sosial/PesanRute.kt`, `notifikasi/NotifikasiRute.kt`, `bantuan/BantuanRute.kt`, `bantuan/BantuanLayanan.kt`
- Test: `server/src/test/kotlin/com/rentech/server/sosial/SosialTest.kt`, `bantuan/BantuanTest.kt`

**Interfaces:**
- Produces:
  - Ulasan: `POST /pesanan/{id}/ulasan {bintang 1..5, isi}`. Hanya penyewa dari pesanan `berjalan`/`selesai` yang boleh, dan hanya satu kali per pesanan (409 kalau kedua kali).
  - Chat:
    - `GET /percakapan/{barangId}?setelah=<id>` → `List<PesanDto(id, teks, waktu "HH.mm", keluar)>`. `keluar` dihitung relatif ke pengguna yang meminta, dan bentuknya sama dengan `Pesan` di `ChatPemilikScreen.kt`.
    - `POST /percakapan/{barangId} {teks}`. Teks kosong atau hanya spasi menghasilkan 400.
  - Notifikasi:
    - `GET /notifikasi` → `List<NotifikasiDto(id, jenis, judul, isi, waktu, hariIni, tujuan, dibaca)>`, terbaru lebih dulu.
    - `POST /notifikasi/{id}/dibaca`.
    - `POST /notifikasi/dibaca-semua`.
  - Bantuan:
    - `POST /bantuan {topik, teks?}` → membuat tiket, lalu langsung menyisipkan balasan otomatis dari `TopikBantuan.balasan`. Teksnya disalin persis dari `HubungiTimScreen.kt`.
    - `GET /bantuan/{tiketId}?setelah=`.
    - `POST /bantuan/{tiketId} {teks}`.
  - `BantuanLayanan.balas(tiketId: Int, adminId: Int, teks: String)` dan `BantuanLayanan.daftarTerbuka()`, dipakai di Bagian 2.

- [ ] **Step 1:** Tulis tes yang gagal.
  - Ulasan kedua untuk pesanan yang sama mendapat 409.
  - `bintang = 6` menghasilkan 400.
  - Pengguna A mengirim pesan, lalu B meminta percakapan: pesan itu `keluar == false`.
  - `setelah=` hanya mengembalikan pesan yang lebih baru.
  - Notifikasi milik pengguna lain tidak ikut muncul.
  - `dibaca-semua` membuat semua notifikasi `dibaca == true`.
  - Tiket topik `Verifikasi` langsung memuat 2 pesan (pesan pengguna dan balasan baku).
  - `balas` dari admin muncul di `GET /bantuan/{id}` sebagai pesan dengan `keluar == false`.
- [ ] **Step 2:** Tes harus **FAIL**.
- [ ] **Step 3:** Implementasikan. Ada pemeriksaan kepemilikan di setiap rute: tiket dan notifikasi orang lain menghasilkan 404, bukan 403, supaya keberadaannya tidak bocor.
- [ ] **Step 4:** Tes harus **PASS**.
- [ ] **Step 5:** Commit dengan pesan `feat(server): ulasan, chat, notifikasi, dan hubungi tim`.

### Task 8: Seed data contoh

**Files:**
- Create: `server/src/main/kotlin/com/rentech/server/seed/Seed.kt`
- Create: `server/seed/foto/`, berisi salinan `app/src/main/res/drawable-nodpi/{foto_*,avatar_*}.webp`, ditambah `ktp_contoh.png` dan `swafoto_contoh.png`. Gambar KTP dan swafoto dibuat sendiri dengan tulisan besar "CONTOH" dan tanpa wajah atau data orang sungguhan.
- Modify: `server/build.gradle.kts`, tambahkan task `seed` (JavaExec dengan main `com.rentech.server.seed.SeedKt`)
- Test: `server/src/test/kotlin/com/rentech/server/seed/SeedTest.kt`

**Interfaces:**
- Produces:
  - `fun jalankanSeed(ds: DataSource, k: Konfigurasi, penyimpanan: Penyimpanan)`. Fungsi ini dipanggil otomatis di `main()` setelah `migrasi`.
  - Admin di-upsert dari `ADMIN_EMAIL`/`ADMIN_SANDI` kalau keduanya ada.
  - Data contoh hanya dimasukkan kalau tabel `barang` masih kosong.
  - Isi seed:
    - 8 kategori `kategoriBarang` dan 7 barang `contohBarang`, dengan nama, harga, deposit, jarak, dan kelengkapan yang persis sama.
    - Pengguna demo: `penyewa@rentech.test` (terverifikasi), `pemilik@rentech.test` (terverifikasi, pemilik ke-7 barang), `diperiksa@rentech.test` (verifikasi menunggu dengan foto contoh), dan `ditolak@rentech.test` (dengan 2 alasan dari `VerifikasiDitolak` di `RentechNavHost.kt`). Sandi semua akun demo adalah `rentech123`.
    - 2 permintaan dari `contohPermintaan`, 1 riwayat `contohRiwayat`, percakapan `percakapanSewa`, dan 6 notifikasi `contohNotifikasi`, semuanya dengan teks yang persis sama.

- [ ] **Step 1:** Tulis tes yang gagal.
  - Setelah `jalankanSeed`, `GET /barang` mengembalikan 7 barang, dan barang 1 bernama "Kamera mirrorless full-frame" dengan harga 350000.
  - Seed yang dijalankan dua kali tetap menghasilkan 7 barang dan 1 admin.
  - Barang yang diubah setelah seed pertama tetap berisi data yang diubah setelah seed kedua.
  - Kalau `ADMIN_EMAIL` kosong, tidak ada pengguna dengan `admin = true`.
- [ ] **Step 2:** Tes harus **FAIL**.
- [ ] **Step 3:** Implementasikan.
- [ ] **Step 4:** Tes harus **PASS**.
- [ ] **Step 5:** Commit dengan pesan `feat(server): seed data contoh dan akun admin`.

### Task 9: Deploy ke Railway

**Files:**
- Create: `server/Dockerfile`. Build multi-tahap: tahap pertama `gradle:jdk21` menjalankan `./gradlew buildFatJar`, lalu hasilnya dijalankan di `eclipse-temurin:21-jre` dengan `java -jar`.
- Create: `server/railway.json` (builder `DOCKERFILE`, healthcheck `/sehat`)
- Modify: `README.md`, tambahkan bagian "Server" berisi cara menjalankan lokal, nama-nama env var, dan perintah seed. Nilai rahasia tidak ditulis.

- [ ] **Step 1:** Pastikan `railway whoami` menampilkan akun Timowit. Kalau belum, minta pengguna menjalankan `! railway login`.
- [ ] **Step 2:** Siapkan proyek Railway.
  - `railway init` dengan nama proyek `rentech`.
  - `railway add --database postgres`.
  - Buat layanan `server` yang tersambung ke repo GitHub `Timowit/Rentech` dengan root directory `server`.
  - Pasang volume di `/data`.
- [ ] **Step 3:** Isi variabel layanan `server`:
  - `DATABASE_URL=${{Postgres.DATABASE_URL}}`
  - `JWT_SECRET` acak 64 karakter, dibuat dengan `openssl rand -hex 32`
  - `OTP_DEV=true`
  - `FOTO_DIR=/data/foto`
  - `ADMIN_EMAIL` dan `ADMIN_SANDI`, diminta ke pengguna dan tidak ditulis di mana pun selain Railway
- [ ] **Step 4:** Deploy dan buat domain publik dengan `railway domain`.
- [ ] **Step 5:** Verifikasi:
  - `curl https://<domain>/sehat` → `{"status":"ok"}`
  - `curl https://<domain>/barang | jq length` → `7`
  - `curl -o /dev/null -w '%{http_code}' https://<domain>/foto/privat/x.jpg` → `404`
- [ ] **Step 6:** Commit dengan pesan `chore(server): deploy railway`, lalu push dengan `git push origin main`. Remote `github-timowit` dipakai karena `pakai-timo` aktif.
