# Checklist Peer Code Review

Dipakai reviewer saat membuka tab **Files changed**. Pilih **Approve** jika semua poin relevan terpenuhi, atau **Request changes** jika ada pelanggaran keamanan / bug kritis.

## Keamanan (semua PR)
- [ ] Tidak ada password, token, atau API key hardcoded di kode
- [ ] Tidak ada file `.env`, `local.properties`, `*.jks`, `*.keystore`, `*.apk`, `*.aab` di daftar file yang berubah
- [ ] File `*.env.example` hanya berisi placeholder, bukan nilai asli
- [ ] Tidak ada folder `build/`, `.gradle/`, atau `.idea/` ikut ter-commit

## Mobile (Kotlin / Jetpack Compose) — `mobile/`
- [ ] Arsitektur MVVM dipatuhi: validasi dan logika bisnis di ViewModel (StateFlow), bukan di Composable
- [ ] State UI lengkap: error, loading, sukses, dan navigasi
- [ ] Base URL / konfigurasi API tidak ditulis hardcoded; lewat BuildConfig atau konfigurasi build (bukan variabel `VITE_*`)
- [ ] Token JWT (jika ada) disimpan via DataStore / EncryptedSharedPreferences, bukan SharedPreferences biasa
- [ ] Lolos `./gradlew lintDebug` dan format ktlint

## Backend (Node.js) — `server/`
- [ ] Password di-hash dengan bcrypt (salt rounds 10), tidak disimpan plain text
- [ ] `JWT_SECRET` dan `DATABASE_URL` dibaca dari environment, bukan ditulis di kode
- [ ] Query database memakai parameter (bukan string concatenation)
- [ ] Respons error konsisten (400 / 401 / 409) dan sesuai `docs/api_specifications.md`
- [ ] Lolos ESLint

## Kualitas Kode
- [ ] Penamaan fungsi/variabel/kelas konsisten
- [ ] Tidak ada potensi infinite loop, memory leak, atau coroutine yang bocor
- [ ] Tidak ada kode mati / komentar debug yang tertinggal

## Proses
- [ ] Pesan commit mengikuti Conventional Commits
- [ ] PR template terisi lengkap dan evidence terlampir
- [ ] Minimal 1 Approval dan status CI hijau sebelum merge
