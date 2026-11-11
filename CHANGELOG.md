# Changelog

Seluruh perubahan penting pada proyek OjolFund dicatat di berkas ini.
Format mengacu pada [Keep a Changelog](https://keepachangelog.com/id/1.1.0/) dan penomoran versi mengikuti [Semantic Versioning](https://semver.org/lang/id/).
Angka `#NN` adalah nomor Pull Request di GitHub.

## [Unreleased]

Rencana Sprint 2 ada di `docs/sprint1_review.md`.

## [0.1.0] - 2026-11-11

Rilis akhir Sprint 1 (Fondasi Sistem & Modul Autentikasi).

### Added

**Mobile (Android, Kotlin, Jetpack Compose)**
- Proyek Android dan komponen UI dasar: design tokens, komponen Compose yang dapat dipakai ulang, navigasi dan bottom bar (#19, #20)
- Layar welcome, login, OTP, home, transaksi, shift, kendaraan, profil, dan tambah pengeluaran (#20)
- `AuthViewModel` berbasis StateFlow dengan validasi form sisi klien (`AuthValidator`) (#20, #23)
- Layar login dan register email, klien Retrofit dengan `AuthInterceptor`, dan penyimpanan token JWT memakai DataStore (#23)
- Layar order, bahan bakar, ringkasan, laporan, target, dan komunitas beserta ViewModel-nya (#29)
- Splash screen, animasi transisi, pulsing dot, count-up, dan press scale (#36, #37)
- Logo launcher OjolFund (#41)
- Mode gelap dengan saklar tema, skeleton loading, haptic feedback, pull-to-refresh, dan onboarding pager (#42)
- Dokumentasi UI mobile (`docs/mobile_ui.md`) (#45)

**Server (Node.js, Express, PostgreSQL)**
- Server Express, koneksi pool PostgreSQL, dan skema tabel `users` (#11, #22)
- Endpoint `POST /api/v1/auth/register` dan `POST /api/v1/auth/login` dengan password bcrypt dan token JWT 24 jam (#22)
- Middleware `verifyToken` dan endpoint `GET /api/v1/auth/me` (#22)
- Spesifikasi API register dan login (`docs/api_specifications.md`) (#14)
- Konfigurasi ESLint dan Prettier (#27)

**DevOps dan QA**
- Template issue (bug, fitur) dan template Pull Request (#6)
- Template PR dengan bukti per area (mobile, backend, DevOps) dan `.github/REVIEW_CHECKLIST.md` untuk peer review (#21)
- Aturan `.gitignore` untuk Android (keystore, APK/AAB, `.idea`) (#21)
- Workflow `mobile-ci.yml`: Android Lint dan unit test pada setiap PR (#25)
- Workflow `server-ci.yml`: ESLint dan Jest pada setiap PR, serta artifact laporan tes (#33)
- Langkah `assembleDebug` dan artifact `app-debug-apk` pada `mobile-ci.yml` untuk verifikasi build APK debug
- Dokumentasi `docs/ci_architecture.md`, `docs/secrets_reference.md`, dan `docs/test_matrix.md` (#33)
- Branch protection pada `main`: wajib PR dengan approval dan status check hijau

**Pengujian**
- 31 unit test mobile: `AuthValidatorTest` (13), `AuthViewModelTest` (13), `AuthInterceptorTest` (4), dan `ExampleUnitTest` (1) dengan JUnit 4, MockK, dan kotlinx-coroutines-test (#31)
- 27 unit test server dengan Jest: controller auth, hashing bcrypt, token JWT, dan middleware `verifyToken` (#35)

### Changed
- Folder `client/` diganti nama menjadi `mobile/` (#20)
- Kode Kotlin diformat dengan ktlint (#23)
- Controller dan routes server disesuaikan dengan aturan ESLint; helper password dan token dipisah ke `utils/` (#27, #35)
- Workflow `mobile-ci.yml` tidak lagi memakai filter `paths` agar status check wajib selalu dilaporkan (#33)

### Fixed
- Isu Android Lint pada kode mobile (ContextCastToActivity dan ViewModelConstructorInComposable) (#28)
- `gradlew` dijadikan executable agar dapat dijalankan di runner CI (#25)
- Duplikasi blok Android dan judul yang rusak pada `.gitignore` dan `REVIEW_CHECKLIST.md` (#21)

### Known Issues
- Belum ada pengujian integrasi dengan database PostgreSQL sungguhan
- Code coverage belum diukur (JaCoCo belum dikonfigurasi)
- `ci.yml` dan `cd.yml` masih berupa placeholder (hanya `echo`)
- `ktlintCheck` belum dijalankan di CI
- `mobile/.env.example` masih memuat variabel sisa web (`VITE_API_BASE_URL`)

[Unreleased]: https://github.com/pandufirlana-dev/tif2001-PARAMA-Ojolfund/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/pandufirlana-dev/tif2001-PARAMA-Ojolfund/releases/tag/v0.1.0
