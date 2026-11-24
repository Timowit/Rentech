create table pengguna (
    id                serial primary key,
    nama              text not null,
    email             text not null unique,
    no_hp             text not null,
    sandi_hash        text not null,
    foto              text,
    peran_aktif       text not null default 'penyewa' check (peran_aktif in ('penyewa', 'pemilik')),
    admin             boolean not null default false,
    status_verifikasi text not null default 'belum'
        check (status_verifikasi in ('belum', 'diperiksa', 'terverifikasi', 'ditolak')),
    dibuat            timestamptz not null default now()
);

create table verifikasi (
    id             serial primary key,
    pengguna_id    int not null references pengguna (id) on delete cascade,
    foto_ktp       text not null,
    foto_swafoto   text not null,
    nik            text not null,
    status         text not null default 'diperiksa' check (status in ('diperiksa', 'terverifikasi', 'ditolak')),
    alasan         text[] not null default '{}',
    diperiksa_oleh int references pengguna (id),
    diperiksa_pada timestamptz,
    dibuat         timestamptz not null default now()
);

create table kategori (
    id     serial primary key,
    nama   text not null unique,
    urutan int not null
);

create table barang (
    id             serial primary key,
    pemilik_id     int not null references pengguna (id),
    kategori_id    int not null references kategori (id),
    nama           text not null,
    harga_per_hari bigint not null check (harga_per_hari > 0),
    deposit        bigint not null check (deposit >= 0),
    kelengkapan    text not null default '',
    lokasi         text not null default 'Jakarta Selatan',
    jarak_demo     text not null default '',
    tersedia       boolean not null default true,
    dibuat         timestamptz not null default now()
);

create table foto_barang (
    id        serial primary key,
    barang_id int not null references barang (id) on delete cascade,
    path      text not null,
    urutan    int not null default 0
);

create table pesanan (
    id                    serial primary key,
    barang_id             int not null references barang (id),
    penyewa_id            int not null references pengguna (id),
    mulai                 date not null,
    selesai               date not null,
    diantar               boolean not null default false,
    slot                  int not null default 0,
    metode                text not null check (metode in ('transfer', 'tunai')),
    kode_unik             smallint check (kode_unik between 100 and 999),
    biaya_sewa            bigint not null,
    biaya_layanan         bigint not null,
    biaya_antar           bigint not null,
    deposit               bigint not null,
    total                 bigint not null,
    status                text not null default 'menunggu_pemilik' check (status in
        ('menunggu_pemilik', 'diterima_pemilik', 'ditolak_pemilik', 'berjalan', 'selesai', 'dibatalkan')),
    status_dana           text not null default 'belum' check (status_dana in
        ('belum', 'menunggu_cek', 'ditahan', 'diteruskan', 'tunai')),
    bukti_transfer        text,
    alasan_bukti          text,
    diterima_penyewa_pada timestamptz,
    dibuat                timestamptz not null default now(),
    check (selesai > mulai)
);
create index pesanan_barang_tanggal on pesanan (barang_id, mulai, selesai);

create table ulasan (
    id         serial primary key,
    pesanan_id int not null references pesanan (id),
    dari_id    int not null references pengguna (id),
    untuk_id   int not null references pengguna (id),
    bintang    smallint not null check (bintang between 1 and 5),
    isi        text not null default '',
    dibuat     timestamptz not null default now(),
    unique (pesanan_id, dari_id)
);

create table pesan (
    id          serial primary key,
    barang_id   int not null references barang (id),
    pesanan_id  int references pesanan (id),
    pengirim_id int not null references pengguna (id),
    penerima_id int not null references pengguna (id),
    teks        text not null,
    dibaca      boolean not null default false,
    dibuat      timestamptz not null default now()
);

create table tiket_bantuan (
    id          serial primary key,
    pengguna_id int not null references pengguna (id),
    topik       text not null,
    status      text not null default 'terbuka' check (status in ('terbuka', 'selesai')),
    dibuat      timestamptz not null default now()
);

create table pesan_bantuan (
    id         serial primary key,
    tiket_id   int not null references tiket_bantuan (id) on delete cascade,
    dari_admin boolean not null,
    teks       text not null,
    dibuat     timestamptz not null default now()
);

create table notifikasi (
    id          serial primary key,
    pengguna_id int not null references pengguna (id) on delete cascade,
    jenis       text not null check (jenis in
        ('diterima', 'dana', 'pesan', 'pengingat', 'ulasan', 'terverifikasi', 'ditolak')),
    judul       text not null,
    isi         text not null,
    tujuan      text not null default 'tidak' check (tujuan in ('sewa_saya', 'chat', 'ulasan', 'tidak')),
    dibaca      boolean not null default false,
    dibuat      timestamptz not null default now()
);
