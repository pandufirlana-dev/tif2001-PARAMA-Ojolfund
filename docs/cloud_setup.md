# Cloud Storage dan Sentry: Panduan Setup (Sprint 2)

Dokumen ini mencatat cara menyiapkan penyimpanan media cloud (free-tier) dan pemantauan error Sentry untuk proyek OjolFund. **Jangan menuliskan kunci, token, atau DSN asli di berkas ini.** Nilai asli hanya disimpan di `server/.env`, Render/hosting, dan GitHub Secrets.

## 1. Arsitektur Unggah Media

```
Aplikasi Android (Multipart)
   -> Server Express + Multer (MemoryStorage, file hanya di RAM)
      -> Supabase Storage (bucket ojolfund-media, file permanen)
         -> URL publik
            -> PostgreSQL (hanya menyimpan teks URL)
```

Alasan: hosting gratis memakai sistem berkas sementara, sehingga file di folder `uploads/` hilang saat server restart. Karena itu file tidak disimpan di disk server.

Aturan validasi di server (diimplementasikan oleh Mhs 2):
- Ukuran maksimal 2 MB (2 x 1024 x 1024 byte).
- Tipe MIME yang diizinkan hanya `image/jpeg`, `image/png`, dan `image/webp`.

## 2. Setup Supabase Storage

1. Buka supabase.com, daftar atau masuk, lalu buat proyek baru (paket Free). Jika tim sudah memakai Supabase untuk PostgreSQL, gunakan proyek yang sama.
2. Di menu **Storage**, klik **New bucket**.
   - Name: `ojolfund-media`
   - Public bucket: aktif (agar URL gambar dapat dibuka langsung oleh aplikasi)
3. Di **Project Settings > API**, catat:
   - **Project URL** menjadi `SUPABASE_URL`
   - **service_role key** menjadi `SUPABASE_SERVICE_ROLE_KEY`
4. Simpan nilai di `server/.env` dan di environment variables hosting backend. Perhatikan bahwa `service_role` melewati seluruh aturan akses, sehingga hanya boleh berada di server. Jangan masukkan ke aplikasi Android, repositori, atau screenshot.
5. Uji: unggah satu gambar lewat dashboard Supabase, salin URL publiknya, lalu buka di browser. Gambar harus tampil.

Catatan: proyek Free dapat dijeda otomatis jika lama tidak aktif. Buka dashboard sebelum presentasi dan pastikan proyek berstatus aktif.

Alternatif jika tim memilih Cloudinary: ganti tiga variabel Supabase dengan `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, dan `CLOUDINARY_API_SECRET`.

## 3. Setup Sentry

1. Buka sentry.io, daftar atau masuk, lalu buat organisasi (paket Developer/gratis).
2. Buat **dua proyek** (**Projects > Create Project**):
   - Platform **Node.js** (atau Express), nama `ojolfund-server`
   - Platform **Android**, nama `ojolfund-android`
3. Pada tiap proyek, buka **Settings > Client Keys (DSN)** dan salin DSN. Bentuknya `https://<kunci>@<org>.ingest.sentry.io/<id>`.
4. Simpan DSN:
   - Server: `SENTRY_DSN` di `server/.env`, hosting backend, dan GitHub Secrets
   - Android: `SENTRY_DSN_ANDROID` di GitHub Secrets dan `local.properties` (tidak di-commit)
5. Pemasangan SDK dikerjakan pada Pertemuan 12:
   - Server: paket `@sentry/node`, `Sentry.init({ dsn: process.env.SENTRY_DSN })`, dan error handler Express
   - Android: dependensi `io.sentry:sentry-android` dan `io.sentry.dsn` pada `AndroidManifest.xml`

## 4. Ringkasan Variabel dan Lokasi Penyimpanan

| Variabel | `server/.env` | Hosting backend | GitHub Secrets | Aplikasi mobile |
|---|---|---|---|---|
| `SUPABASE_URL` | ya | ya | tidak | tidak |
| `SUPABASE_SERVICE_ROLE_KEY` | ya | ya | tidak | **tidak boleh** |
| `SUPABASE_BUCKET` | ya | ya | tidak | tidak |
| `SENTRY_DSN` | ya | ya | ya | tidak |
| `SENTRY_DSN_ANDROID` | tidak | tidak | ya | lewat konfigurasi build (bukan di-commit) |

## 5. Checklist Verifikasi

- [ ] Bucket `ojolfund-media` dibuat dan berstatus public
- [ ] Satu gambar uji dapat diunggah dan URL publiknya terbuka di browser
- [ ] Proyek Sentry `ojolfund-server` dan `ojolfund-android` dibuat
- [ ] DSN tersimpan di tempat yang benar (tabel bagian 4), tidak ada di Git
- [ ] `git status` bersih dari `.env` dan berkas rahasia sebelum commit
- [ ] Anggota tim yang membutuhkan (Mhs 2) sudah menerima nilai lewat jalur privat, bukan grup publik

## 6. Keamanan

- `.env` dan `local.properties` diabaikan oleh `.gitignore`. Hanya `.env.example` (berisi placeholder) yang boleh di-commit.
- Jika sebuah kunci pernah ter-commit atau terkirim ke grup, anggap bocor: segera buat ulang (rotate) di dashboard terkait.
- Kirim nilai rahasia ke rekan lewat pesan pribadi, bukan grup kelas atau komentar PR.
