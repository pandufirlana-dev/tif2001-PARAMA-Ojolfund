# CI Workflow Architecture

Dokumen ini menjelaskan pipeline otomasi GitHub Actions pada repositori ini.

## Gambaran Umum

| Workflow | Berkas | Trigger | Job (nama status check) | Isi |
|---|---|---|---|---|
| Android Mobile CI | `.github/workflows/mobile-ci.yml` | `pull_request` ke `main`, `workflow_dispatch` | `Lint & Unit Test Kotlin` | JDK 17, `./gradlew lintDebug`, `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`, unggah laporan dan APK |
| Server CI | `.github/workflows/server-ci.yml` | `pull_request` ke `main`, `workflow_dispatch` | `Lint & Test Server` | Node.js 22, `npm ci`, `npm run lint`, `npm test --if-present` |
| CI (placeholder) | `.github/workflows/ci.yml` | `push` dan `pull_request` ke `main` | `check` | Hanya `echo`; bukan pemeriksaan kualitas |
| CD (placeholder) | `.github/workflows/cd.yml` | tag `v*.*.*` | `deploy` | Hanya `echo`; belum ada deployment nyata |

## Alur pada Pull Request

1. Developer push feature branch lalu membuka PR ke `main`.
2. `Android Mobile CI` dan `Server CI` berjalan otomatis.
3. Reviewer memeriksa kode (lihat `.github/REVIEW_CHECKLIST.md`).
4. PR hanya dapat di-merge jika: minimal 1 approval dan seluruh status check wajib hijau.

## Detail Job

### Lint & Unit Test Kotlin (`mobile/`)
- Runner: `ubuntu-latest`, direktori kerja `mobile/`.
- Cache Gradle melalui `actions/setup-java` (`cache: gradle`).
- Langkah `lintDebug` gagal jika Android Lint menemukan error (warning tidak menggagalkan).
- Langkah `testDebugUnitTest` menjalankan unit test JVM lokal tanpa emulator.
- Langkah `assembleDebug` memverifikasi aplikasi dapat dikompilasi menjadi APK debug.
- Artifact: `lint-report` dan `unit-test-report` (HTML, selalu diunggah dengan `if: always()`), serta `app-debug-apk` (berkas `app-debug.apk`, disimpan 14 hari, hanya jika build berhasil).

### Lint & Test Server (`server/`)
- Runner: `ubuntu-latest`, direktori kerja `server/`.
- Cache npm berdasarkan `server/package-lock.json`.
- `npm run lint` menjalankan ESLint (konfigurasi di `server/`).
- `npm test --if-present` menjalankan Jest (27 tes unit pada `server/tests/unit/`).

## Aturan Status Check (Branch Protection)

Branch `main` dilindungi dengan aturan berikut:

- Require a pull request before merging (minimal 1 approval).
- Require status checks to pass before merging:
  - `Lint & Unit Test Kotlin`
  - `Lint & Test Server`
- Require branches to be up to date before merging.
- Larangan push langsung ke `main`.

Workflow wajib sengaja tidak memakai filter `paths`. Jika memakai filter `paths`, PR yang tidak menyentuh folder terkait tidak memicu workflow, sehingga status check wajib tetap "pending" dan PR tidak bisa di-merge.

## Pemecahan Masalah

| Gejala | Kemungkinan penyebab | Tindakan |
|---|---|---|
| `Run Android Lint Check` merah | Ada error lint pada kode `mobile/` | Unduh artifact `lint-report`, buka `lint-results-debug.html`, perbaiki error |
| `Run Local Unit Tests` dilewati | Langkah lint sebelumnya gagal | Perbaiki lint terlebih dahulu |
| `Permission denied` pada `./gradlew` | Berkas tidak executable | Sudah ditangani langkah `chmod +x gradlew` dan mode `100755` di Git |
| Check wajib "Expected - Waiting for status" | Workflow tidak terpicu | Pastikan workflow tidak memakai `paths` dan target branch adalah `main` |
