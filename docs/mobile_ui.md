# Dokumentasi UI Mobile (OjolFund)

Dokumen ini merangkum antarmuka aplikasi Android (`mobile/`): layar, alur navigasi, komponen reusable, state management, tema, dan animasi.
Untuk lapisan jaringan dan autentikasi lihat `docs/mobile_network_module.md`; untuk skenario pengujian lihat `docs/test_matrix.md`.

## 1. Teknologi

| Aspek | Pilihan |
|---|---|
| UI | Jetpack Compose + Material3 (Compose BOM 2024.09.00) |
| Arsitektur | MVVM, `StateFlow`, DI manual (`di/AppContainer.kt`) |
| Navigasi | Navigation Compose (satu `NavHost` di `ui/OjolFundApp.kt`) |
| Penyimpanan lokal | Jetpack DataStore Preferences (token JWT dan pilihan tema) |
| Jaringan | Retrofit + OkHttp (lihat `mobile_network_module.md`) |
| SDK | minSdk 24, targetSdk 36, compileSdk 37.1; package `com.ojolfund.app` |
| Kualitas kode | ktlint (`./gradlew ktlintCheck`), Android Lint (`./gradlew lintDebug`), unit test (`./gradlew testDebugUnitTest`) |

## 2. Struktur Folder UI

```
mobile/app/src/main/java/com/ojolfund/app/
├── MainActivity.kt            titik masuk; memilih tema terang/gelap
├── OjolFundApplication.kt     membuat AppContainer
├── di/AppContainer.kt         Retrofit, OkHttp, TokenStore, ThemeStore, repository
├── data/                      model tampilan, format Rupiah, data contoh, local/remote/repository
├── validation/AuthValidator.kt aturan validasi form sisi klien
├── viewmodel/                 AuthViewModel, OtpAuthViewModel, LedgerViewModel,
│                              PlanViewModel, CommunityViewModel, ThemeViewModel
└── ui/
    ├── OjolFundApp.kt         NavHost, rute, bottom bar, transisi, overlay splash
    ├── theme/                 OjolColors (terang/gelap), OjolText, OjolfundTheme
    ├── components/            komponen reusable (bagian 5)
    └── screens/               19 layar (bagian 3)
```

## 3. Layar

### 3.1 Autentikasi dan onboarding

| Rute | Berkas | Fungsi |
|---|---|---|
| (overlay) | `SplashScreen.kt` | Splash animasi saat aplikasi dibuka; selalu hijau di kedua tema |
| `welcome` | `WelcomeScreen.kt` | Halaman awal: 3 slide fitur yang bisa digeser, tombol "Mulai Sekarang" dan "Masuk" |
| `login` | `LoginScreen.kt` | Masuk dengan email dan kata sandi (terhubung ke backend) |
| `register` | `RegisterScreen.kt` | Daftar akun: nama, email, kata sandi, konfirmasi |
| `phone_login` | `PhoneLoginScreen.kt` | Masuk dengan nomor telepon (tampilan; belum terhubung backend) |
| `otp` | `OtpScreen.kt` | Input kode OTP (tampilan; belum terhubung backend) |

### 3.2 Tab utama (bottom bar)

| Rute | Berkas | Fungsi |
|---|---|---|
| `home` | `HomeScreen.kt` | Beranda: saldo bersih hari ini, ringkasan pemasukan/potongan/pengeluaran, aktivitas terbaru |
| `transactions` | `TransactionsScreen.kt` | Daftar transaksi per hari dengan filter |
| `shift` | `ShiftScreen.kt` | Status shift dan target harian |
| `vehicle` | `VehicleScreen.kt` | Kendaraan, konsumsi BBM, servis |
| `profile` | `ProfileScreen.kt` | Profil, sinkronisasi, tema (Sistem/Terang/Gelap), keluar |

### 3.3 Layar detail

| Rute | Berkas | Fungsi |
|---|---|---|
| `add_order` | `AddOrderScreen.kt` | Catat order: platform, tarif, komisi, metode bayar |
| `add_expense` | `AddExpenseScreen.kt` | Catat pengeluaran per kategori |
| `fuel` | `FuelScreen.kt` | Catat pengisian BBM dan hitung km/liter |
| `summary` | `SummaryScreen.kt` | Ringkasan harian per platform |
| `report` | `ReportScreen.kt` | Laporan dengan grafik batang dan kontribusi platform |
| `target` | `TargetScreen.kt` | Target pendapatan dan progres |
| `community` | `CommunityScreen.kt` | Laporan kondisi jalan dari komunitas |
| `community_chat` | `CommunityChatScreen.kt` | Obrolan komunitas |

## 4. Alur Navigasi

```
(buka aplikasi) ─► Splash ─► welcome ─┬─► login ──────┐
                                      ├─► register ───┤ (sukses)
                                      └─► phone_login ─► otp ─┤
                                                              ▼
                                                            home ◄─► transactions / shift / vehicle / profile
                                                              │
        add_order · add_expense · fuel · summary · report · target · community ─► community_chat
                                                              │
                                              profile ─ Keluar ─► welcome (back stack dibersihkan)
```

- Bottom bar tampil di `home`, `transactions`, `shift`, `vehicle`, `profile`, `summary`, `report`, `target` (`Routes.withBottomBar`).
- Perpindahan antar tab memakai fade; layar detail bergeser halus dari kanan.
- Setelah daftar berhasil, pengguna diarahkan ke login dengan banner sukses; setelah login berhasil token JWT disimpan di DataStore.

## 5. Komponen Reusable (`ui/components`)

| Berkas | Isi |
|---|---|
| `Components.kt` | `OjolCard`, `PrimaryButton` (loading, konfirmasi centang), `SoftButton`, `Pill` / `SyncPill`, `ProgressLine`, `IconBox`, header halaman, `OjolBottomBar`, `Context.toast` |
| `DetailComponents.kt` | `DetailHeader`, `ChoiceChip`, `SelectTile`, `AmountField`, `AmountDialog`, `CheckRow`, `VGap` |
| `OjolTextField.kt` | Kolom input dengan label, galat, tampilkan/sembunyikan sandi, animasi fokus |
| `AuthComponents.kt` | Komponen form autentikasi, banner pesan (sukses/galat) |
| `TagStyle.kt` | Gaya label platform dan kategori |
| `Motion.kt` | Animasi bersama: `PulsingDot`, `rememberPressScale`, `CountUpText`, `StaggerIn`, `rememberGrow`, `AnimatedCheckBadge`, `ShimmerBox`, `OjolRefreshBox`, `AnimatedEmptyState`, haptic |

## 6. State Management

| ViewModel | Sumber data | Catatan |
|---|---|---|
| `AuthViewModel` | `AuthRepository` (API nyata) | Status `Idle / Loading / LoggedIn / Registered / Error`; validasi via `AuthValidator` |
| `OtpAuthViewModel` | Simulasi | Alur OTP tampilan |
| `LedgerViewModel` | Memori lokal | Transaksi, pemasukan, pengeluaran, BBM |
| `PlanViewModel` | Memori lokal | Target dan rencana |
| `CommunityViewModel` | Memori lokal | Laporan dan obrolan komunitas |
| `ThemeViewModel` | `ThemeStore` (DataStore) | Pilihan tema Sistem/Terang/Gelap |

Data transaksi, target, dan komunitas masih lokal (belum dari server). Hanya autentikasi yang terhubung ke backend pada Sprint 1.

## 7. Validasi Form (`validation/AuthValidator.kt`)

| Aturan | Pesan |
|---|---|
| Email kosong | "Email wajib diisi" |
| Format email salah | "Format email tidak valid" |
| Kata sandi kosong (login dan daftar) | "Kata sandi wajib diisi" |
| Kata sandi baru < 8 karakter | "Kata sandi minimal 8 karakter" |
| Konfirmasi kosong | "Konfirmasi kata sandi wajib diisi" |
| Konfirmasi tidak sama | "Konfirmasi kata sandi tidak sama" |
| Nama kosong / < 2 karakter | "Nama lengkap wajib diisi" / "Nama minimal 2 karakter" |

Galat server ditampilkan sebagai pesan Indonesia: 401 "Email atau kata sandi salah", 409 "Email sudah terdaftar", dan pesan jaringan saat server tidak terjangkau.

## 8. Tema dan Gaya

- Token warna ada di `ui/theme/Theme.kt` (`OjolColors`), masing-masing punya varian terang dan gelap. Warna dibaca lewat getter sehingga layar otomatis tergambar ulang saat tema berganti.
- Mode tema (Sistem/Terang/Gelap) dipilih di Profil dan disimpan di DataStore; ikon status bar mengikuti tema pilihan.
- Tipografi: `OjolText` (Display, Headline, Title, Body, Label).
- Splash tetap hijau di kedua tema agar sama dengan splash sistem Android 12+.

## 9. Animasi dan Umpan Balik

| Efek | Lokasi |
|---|---|
| Splash: logo memantul, cincin melebar, nama dan tagline menyusul | `SplashScreen.kt` |
| Titik status berdenyut ("Tersinkron", halaman awal) | `PulsingDot` |
| Angka saldo menghitung naik | `CountUpText` (Beranda) |
| Daftar muncul bertahap | `StaggerIn` (Beranda, Transaksi, Komunitas) |
| Grafik tumbuh, bar progres terisi | `rememberGrow`, `ProgressLine` (Laporan, Ringkasan, Target) |
| Centang sukses tergambar | `AnimatedCheckBadge` (dialog BBM, banner sukses, tombol simpan) |
| Banner galat bergetar | `MessageBanner` |
| Efek tekan tombol dan kartu | `rememberPressScale` |
| Skeleton shimmer saat layar pertama dibuka | `ShimmerBox` (Beranda, Transaksi) |
| Tarik untuk menyegarkan | `OjolRefreshBox` (Beranda, Transaksi) |
| Getaran halus | `rememberTapHaptic` |
| Transisi antar layar | `NavHost` di `OjolFundApp.kt` |

Catatan: skeleton dan tarik-untuk-menyegarkan masih simulasi karena data lokal; hubungkan ke pemuatan nyata saat API data tersedia.

## 10. Ikon Aplikasi

Adaptive icon (`ic_launcher_background` gradien hijau, `ic_launcher_foreground` kartu putih dengan dompet hijau), ikon bulat, ikon monokrom untuk themed icon Android 13+, dan fallback WebP untuk Android 7.x.

## 11. Pengujian

| Berkas uji | Jumlah | Cakupan |
|---|---|---|
| `AuthViewModelTest` | 13 | Validasi, login sukses (Loading ke LoggedIn dan token tersimpan), 401, 409, jaringan, logout |
| `AuthValidatorTest` | 13 | Semua aturan validasi |
| `AuthInterceptorTest` | 4 | Header `Authorization: Bearer` |

Jalankan dari folder `mobile/`:

```
./gradlew ktlintCheck
./gradlew lintDebug
./gradlew testDebugUnitTest
```

## 12. Cara Menjalankan

1. Buka folder `mobile/` di Android Studio dan jalankan di emulator (API 24 ke atas).
2. Untuk login ke backend lokal, jalankan server (`server/`, port 5000). Emulator mengakses host lewat `http://10.0.2.2:5000/api/v1/` (`BuildConfig.BASE_URL`).
3. Jalankan Gradle dari terminal Android Studio di dalam folder `mobile/`.
