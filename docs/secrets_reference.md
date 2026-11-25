# Secrets Reference

Dokumen ini mendaftar variabel rahasia yang dipakai proyek. **Jangan menuliskan nilai asli di berkas ini.**

## Variabel Lingkungan Server (`server/.env`, tidak di-commit)

Template: `server/.env.example`.

| Variabel | Dipakai oleh | Keterangan |
|---|---|---|
| `PORT` | `server/src/index.js` | Port HTTP server (contoh: 5000) |
| `DATABASE_URL` | `server/src/config/db.js` | Connection string PostgreSQL (Neon/Supabase), wajib `sslmode=require` |
| `JWT_SECRET` | `authController.js`, `verifyToken.js` | Kunci penandatangan JWT (masa berlaku token 24 jam) |
| `NODE_ENV` | server | `development` atau `production` |
| `SUPABASE_URL` | (rencana) modul upload media | URL proyek Supabase untuk Storage |
| `SUPABASE_SERVICE_ROLE_KEY` | (rencana) modul upload media | Kunci server untuk menulis ke bucket. **Hanya di server**, tidak boleh ada di aplikasi mobile |
| `SUPABASE_BUCKET` | (rencana) modul upload media | Nama bucket media (`ojolfund-media`) |
| `SENTRY_DSN` | (rencana) `server/src/index.js` | DSN proyek Sentry untuk server |

Aturan:
- `.env` dan `.env.*` diabaikan oleh `.gitignore`; hanya `.env.example` yang boleh di-commit dan hanya berisi placeholder.
- Jika sebuah secret pernah ter-commit, anggap bocor: ganti nilainya (rotate), lalu bersihkan dari riwayat.

## Konfigurasi Mobile

| Berkas | Catatan |
|---|---|
| `mobile/local.properties` | Path SDK lokal, diabaikan Git, tidak boleh di-commit |
| `*.jks`, `*.keystore` | Kunci penandatanganan aplikasi, diabaikan Git, tidak boleh di-commit |
| `mobile/.env.example` | Berisi `VITE_API_BASE_URL` (sisa template web); aplikasi Android tidak membaca variabel ini. Base URL API dikonfigurasi melalui konfigurasi build |

## GitHub Secrets (Settings > Secrets and variables > Actions)

Saat ini workflow `mobile-ci.yml` dan `server-ci.yml` **tidak memerlukan** GitHub Secrets. Jika nanti ditambahkan (misalnya integration test dengan database atau CD), daftarkan di tabel ini:

| Nama secret | Dipakai oleh workflow | Keterangan | Status |
|---|---|---|---|
| `DATABASE_URL` | (rencana) server integration test | Database khusus pengujian, bukan database produksi | Belum dibuat |
| `JWT_SECRET` | (rencana) server integration test | Nilai acak khusus pengujian | Belum dibuat |
| `SENTRY_DSN` | (rencana) build dan deploy | DSN Sentry untuk server | Belum dibuat |
| `SENTRY_DSN_ANDROID` | (rencana) build mobile | DSN Sentry untuk aplikasi Android | Belum dibuat |
| `RENDER_DEPLOY_HOOK` | (rencana) `cd.yml` | URL deploy hook layanan hosting backend | Belum dibuat |

Detail pembuatan akun dan kunci ada di `docs/cloud_setup.md`.

Aturan: nilai secret hanya diisi melalui halaman Settings GitHub, tidak pernah ditulis di berkas workflow atau dokumen.
