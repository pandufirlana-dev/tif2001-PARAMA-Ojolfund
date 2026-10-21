# Modul Jaringan Mobile (OjolFund)

Dokumen ini menjelaskan lapisan jaringan dan autentikasi pada aplikasi Android (`mobile/`).
Kontrak endpoint mengacu pada `docs/api_specifications.md`.

## Ringkasan Arsitektur

```
LoginScreen / RegisterScreen        (ui/screens)
        │  state & event
AuthViewModel  ── AuthValidator     (viewmodel, validation)
        │
AuthRepository ── TokenStore        (data/repository, data/local)
        │                │
AuthApiService        DataStore Preferences (jwt_token)
        │
Retrofit + OkHttp (AuthInterceptor, HttpLoggingInterceptor)   (di/AppContainer)
        │
Backend Express  http://<host>:5000/api/v1/
```

| Berkas | Tanggung jawab |
|---|---|
| `data/remote/AuthApiService.kt` | Interface Retrofit: `@POST auth/register`, `@POST auth/login`, `@GET auth/me` |
| `data/remote/dto/AuthDto.kt` | `LoginRequest`, `RegisterRequest`, `LoginResponse`, `RegisterResponse`, `MeResponse`, `UserDto`, `ErrorResponse` |
| `data/remote/AuthInterceptor.kt` | Menyematkan `Authorization: Bearer <token>` pada setiap request jika token ada |
| `data/local/TokenStore.kt` | Antarmuka + implementasi `DataStoreTokenStore` (Jetpack DataStore, bukan SharedPreferences biasa) |
| `data/repository/AuthRepository.kt` | Memanggil API, menyimpan token saat login sukses, memetakan galat ke pesan Indonesia (`AuthResult`) |
| `di/AppContainer.kt` | DI manual: merakit OkHttp, Retrofit, TokenStore, repository |
| `OjolFundApplication.kt` | Membuat `AppContainer` sekali per proses |
| `validation/AuthValidator.kt` | Validasi form sisi klien (Kotlin murni, siap diuji unit) |
| `viewmodel/AuthViewModel.kt` | State form, validasi, login/register/logout, factory |

## Konfigurasi

- **Base URL**: `BuildConfig.BASE_URL`, diatur di `app/build.gradle.kts`
  (default `http://10.0.2.2:5000/api/v1/` = `localhost` komputer dari emulator Android).
  Untuk HP fisik ganti dengan IP LAN komputer, mis. `http://192.168.1.10:5000/api/v1/`
  (HP dan komputer harus satu jaringan). Base URL wajib berakhir dengan `/`.
- **Cleartext HTTP**: hanya diizinkan pada build **debug** (`app/src/debug/...network_security_config.xml`).
  Build release tetap HTTPS-only.
- **Izin**: `android.permission.INTERNET` di `AndroidManifest.xml`.
- **Logging**: `HttpLoggingInterceptor` level `BASIC` dan hanya di debug, sehingga header/body
  (token dan kata sandi) tidak pernah dicatat.

## Auth Interceptor

`AuthInterceptor(tokenProvider)` mengambil token dari `TokenStore` dan menambahkan
`Authorization: Bearer <token>`. Request tanpa token (login/register) lewat tanpa header.
Jika request sudah membawa header `Authorization` sendiri, header itu tidak ditimpa.
`tokenProvider` berupa lambda sehingga dapat diuji tanpa Android.

## Penyimpanan Token

Token JWT disimpan di **Jetpack DataStore Preferences** (`session`, key `jwt_token`).
Dihapus saat logout (`AuthViewModel.logout()` → `AuthRepository.logout()` → `TokenStore.clear()`).

## Kontrak API yang Dipakai

| Aksi | Endpoint | Request | Sukses | Galat |
|---|---|---|---|---|
| Daftar | `POST /auth/register` | `{name, email, password}` | 201 `{message, user}` | 400 data tidak valid, 409 `Email sudah terdaftar` |
| Masuk | `POST /auth/login` | `{email, password}` | 200 `{message, token, user}` | 400, 401 `Email atau kata sandi salah` |
| Profil | `GET /auth/me` | header Bearer | 200 `{message, user}` | 401 |

Penanganan galat di `AuthRepository`: pesan `message` dari body galat server dipakai apa adanya;
jika tidak ada, dipakai pesan cadangan berdasarkan kode HTTP. `IOException` (tanpa koneksi/timeout)
menjadi *"Tidak dapat terhubung ke server..."*.

## Aturan Validasi Klien

| Field | Aturan | Pesan |
|---|---|---|
| Nama | wajib, minimal 2 karakter | "Nama lengkap wajib diisi" / "Nama minimal 2 karakter" |
| Email | wajib, format valid | "Email wajib diisi" / "Format email tidak valid" |
| Kata sandi (daftar) | wajib, minimal 8 karakter | "Kata sandi minimal 8 karakter" |
| Kata sandi (masuk) | tidak kosong | "Kata sandi wajib diisi" |
| Konfirmasi | sama dengan kata sandi | "Konfirmasi kata sandi tidak sama" |

Galat tampil per field saat submit dan hilang saat field diedit. Tombol menampilkan indikator
loading dan tidak bisa ditekan ganda saat request berjalan.

## Alur Layar

`Welcome` → `Daftar` → (sukses) `Masuk` (email terisi + banner "Akun berhasil dibuat") → `Home`.
Dari `Masuk` tersedia tautan ke `Daftar` dan tombol "Masuk dengan Nomor HP (Demo)" (alur OTP lama,
tanpa backend). `Profil → Keluar` menghapus token lalu kembali ke `Welcome`.

## ktlint

- Konfigurasi: `mobile/.editorconfig` (`ktlint_code_style = android_studio`, panjang baris maks 120).
- Plugin Gradle: `org.jlleitschuh.gradle.ktlint` (di `libs.versions.toml`).
- Jalankan dari folder `mobile/`:
  - `./gradlew ktlintCheck` — audit
  - `./gradlew ktlintFormat` — perbaiki otomatis
  - `./gradlew lintDebug` — Android Lint

## Titik Uji (Pertemuan 8)

`AuthViewModel` menerima `AuthRepository` lewat konstruktor, dan `AuthRepository` menerima
`AuthApiService` + `TokenStore` (antarmuka), sehingga dapat di-mock. `OjolTextField` dan tombol
memiliki `testTag`: `login_email`, `login_password`, `login_submit`, `login_error_banner`,
`register_name`, `register_email`, `register_password`, `register_confirm`, `register_submit`,
`register_error_banner` (pesan galat field: `<tag>_error`).
