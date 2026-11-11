# Sprint 1 Review: Audit Kontribusi, Velocity, dan Rencana Sprint 2

Proyek: OjolFund (Android Kotlin + Node.js REST API) | Sprint 1: Fondasi Sistem & Modul Autentikasi
Periode data: 16 September 2026 - 5 November 2026 (branch `main`, 88 commit)

> Data di bawah dihitung dari riwayat git (`git log main`). Angka pada GitHub Insights dapat sedikit berbeda karena Insights mengelompokkan per akun GitHub dan hanya menghitung commit pada branch default. Gunakan dokumen ini bersama tangkapan layar Insights.

## 1. Audit Kontribusi Individu

Jumlah commit tidak termasuk commit merge (59 commit non-merge dan 29 commit merge: 27 dari PR dan 2 merge manual).

| Anggota | Peran | Commit | Baris ditambah | Baris dihapus | Area utama |
|---|---|---|---|---|---|
| M. Rafi Alfa Rizqi | Mhs 1, Mobile | 29 | 16.211 | 1.753 | `mobile/` (UI Compose, ViewModel, jaringan, tes, animasi) |
| Marhandhy Waringga Jaddi | Mhs 2, Backend | 10 | 8.548 | 1.225 | `server/` (API auth, JWT, skema DB, ESLint, Jest), `docs/` |
| Pandu Firlana Putra | Mhs 3, QA/DevOps | 20 | 479 | 33 | `.github/` (template PR/issue, CI/CD, review checklist), `docs/`, `.gitignore` |
| **Total** | | **59** | | | |

Catatan membaca angka:
- Jumlah baris tidak mencerminkan kualitas atau beban kerja. Berkas DevOps berupa YAML dan Markdown pendek, sedangkan commit mobile dan server memuat proyek awal Android, kode UI, dan `package-lock.json`.
- Commit awal repositori (`Initial commit`) tercatat atas nama Mhs 1 dan dihitung dalam 29 commit tersebut.
- Satu orang Mhs 2 muncul dengan tiga nama git (`marhandhy-dev`, `Marhandhy Waringga Jaddi`, `Handhy.up`) dan sudah digabung pada tabel.

## 2. Pull Request yang Di-merge

Total **27 PR** di-merge ke `main`, ditambah 2 commit merge manual (penyelesaian konflik).

| Periode merge | PR | Fokus |
|---|---|---|
| 16 - 25 Sep (setup repo) | #1, #2, #3, #5, #6, #11, #12, #13, #14 | README, folder awal, template issue dan PR, CI/CD placeholder, setup server, spesifikasi API |
| 13 - 14 Okt (Pertemuan 6) | #17, #18, #19, #20, #21, #22 | Compose UI dan AuthViewModel (#20), API auth JWT (#22), template PR, review checklist, `.gitignore` (#21) |
| 21 Okt (Pertemuan 7) | #23, #25, #27, #28 | Retrofit dan DataStore (#23), workflow `mobile-ci.yml` (#25), ESLint server (#27), perbaikan lint (#28) |
| 2 - 4 Nov (Pertemuan 8) | #29, #31, #33, #35 | Layar baru (#29), unit test mobile (#31), pipeline test CI dan dokumentasi (#33), unit test server (#35) |
| 5 Nov (Pertemuan 9) | #36, #37, #41, #42 | Animasi (#36, #37), logo (#41), dark mode dan polish UI (#42) |

Velocity Sprint 1 (PR di-merge per periode pertemuan): 6, 4, 4, 4 PR, atau 18 PR selama Pertemuan 6 - 9.
Nomor yang tidak muncul pada tabel (misalnya #30, #32, #34) adalah issue atau PR yang tidak di-merge.

## 3. Verifikasi Build APK Debug

CI kini memverifikasi build APK pada setiap PR:

| Item | Nilai |
|---|---|
| Perintah | `./gradlew assembleDebug` (di `mobile/`, JDK 17) |
| Workflow | `.github/workflows/mobile-ci.yml`, job `Lint & Unit Test Kotlin` |
| Artifact | `app-debug-apk` (`app-debug.apk`, disimpan 14 hari) |
| Konfigurasi | `applicationId` `com.ojolfund.app`, `minSdk` 24, `targetSdk` 36, versi 1.0 (kode 1) |
| Base URL API | `http://10.0.2.2:5000/api/v1/` (alamat host untuk emulator Android) |

Langkah verifikasi alur autentikasi di emulator atau perangkat (untuk video/GIF bukti):
1. Jalankan server: salin `server/.env.example` ke `server/.env`, isi `DATABASE_URL` dan `JWT_SECRET`, lalu `cd server && npm ci && npm start`.
2. Unduh artifact `app-debug-apk` dari halaman run GitHub Actions, atau jalankan dari Android Studio.
3. Pasang APK ke emulator (`adb install app-debug.apk`).
4. Rekam alur: Register, Login, masuk Home, Logout, lalu Login dengan password salah (muncul pesan galat).
5. Perangkat fisik memakai alamat IP laptop, bukan `10.0.2.2`, sehingga `BASE_URL` perlu disesuaikan.

## 4. Checklist Keluaran Sprint 1

| Item | Status | Bukti |
|---|---|---|
| Register dengan password terenkripsi | Terpenuhi di kode | `server/src/controllers/authController.js` memakai bcrypt; tes "berhasil 201, password disimpan sebagai hash" |
| Data tersimpan di PostgreSQL cloud | Perlu verifikasi manual | Tidak dapat dibuktikan dari repositori; cek koneksi `DATABASE_URL` yang dipakai tim |
| Login menerbitkan JWT valid | Terpenuhi | Tes `token.test.js` (masa berlaku 24 jam) dan tes login 200 |
| Halaman Login dan Register dengan validasi klien | Terpenuhi | `LoginScreen.kt`, `RegisterScreen.kt`, `AuthValidator.kt` |
| Token tersimpan aman di sisi klien | Terpenuhi | `DataStoreTokenStore` (Jetpack DataStore) |
| Workflow CI memvalidasi setiap PR | Terpenuhi | `mobile-ci.yml` dan `server-ci.yml`; lihat tab Checks pada PR |
| Matriks unit test lolos | Terpenuhi | 31 tes mobile + 27 tes server = 58 (`docs/test_matrix.md`) |
| Branch protection `main` aktif | Perlu verifikasi manual | Settings > Rules > Rulesets |
| Seluruh task Sprint 1 di kolom Done | Perlu verifikasi manual | GitHub Projects board |
| Presensi WA seluruh pertemuan | Perlu verifikasi manual | Rekap grup kelas |

## 5. Evaluasi Sprint

Yang berjalan baik:
- Alur PR, review, dan CI berjalan sejak Pertemuan 7 dan menangkap error lint nyata pada percobaan pertama.
- Tes otomatis mencakup validator, ViewModel, interceptor, controller, bcrypt, JWT, dan middleware.

Kendala dan perbaikan:
- CI hanya menjalankan Android Lint dan unit test; `ktlintCheck` terpasang di proyek tetapi belum di CI.
- Coverage belum diukur.
- `ci.yml` dan `cd.yml` masih placeholder, dan `mobile/.env.example` masih memuat variabel sisa web.
- Sebagian PR berisi banyak commit sekaligus; sebaiknya PR dibuat lebih kecil agar review lebih cepat.

## 6. Rencana Sprint 2 (Usulan)

Usulan awal untuk dibahas tim dan disesuaikan dengan backlog GitHub Projects.

| Area | Usulan task | Penanggung jawab |
|---|---|---|
| Backend | Tabel dan endpoint CRUD transaksi/ledger (order, bahan bakar) yang dilindungi JWT, beserta pembaruan `docs/api_specifications.md` | Mhs 2 |
| Mobile | Hubungkan `LedgerViewModel` dan layar order/bahan bakar ke API melalui Retrofit; tangani state loading, error, dan sesi kedaluwarsa | Mhs 1 |
| QA | Tes integrasi endpoint dengan Supertest dan database uji; tes ViewModel baru; matriks pengujian Sprint 2 | Mhs 3 |
| DevOps | Tambah `ktlintCheck` dan coverage (JaCoCo, Jest `--coverage`) ke CI; ganti `ci.yml` placeholder; tag rilis `v0.1.0` | Mhs 3 |
| Semua | Perkecil ukuran PR, kaitkan setiap PR dengan issue (`Closes #N`), dan perbarui `CHANGELOG.md` pada tiap rilis | Tim |
