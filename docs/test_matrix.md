# Authentication Test Scenario Matrix

Matriks skenario uji modul autentikasi (Sprint 1). Jenis: **P** = positive, **N** = negative, **B** = boundary.
Status: **Diotomasi** = sudah ada test yang berjalan di CI; **Belum diotomasi** = belum ada test.

## A. Mobile - Validasi (`AuthValidatorTest`, 13 test)

| Fungsi | Skenario | Jenis | Status |
|---|---|---|---|
| validateEmail | kosong -> "wajib diisi" | N | Diotomasi |
| validateEmail | tanpa `@` -> format tidak valid | N | Diotomasi |
| validateEmail | tanpa suffix domain -> tidak valid | N | Diotomasi |
| validateEmail | valid dengan spasi di tepi (di-trim) | B | Diotomasi |
| validateName | kosong -> wajib diisi | N | Diotomasi |
| validateName | 1 karakter -> terlalu pendek | B | Diotomasi |
| validateName | nama valid | P | Diotomasi |
| validateLoginPassword | kosong -> wajib diisi | N | Diotomasi |
| validateLoginPassword | pendek tetap lolos (dicek backend) | B | Diotomasi |
| validateNewPassword | 7 karakter ditolak | B | Diotomasi |
| validateNewPassword | 8 karakter lolos | B | Diotomasi |
| validateConfirmPassword | tidak sama ditolak | N | Diotomasi |
| validateConfirmPassword | sama lolos | P | Diotomasi |

## B. Mobile - ViewModel (`AuthViewModelTest`, 13 test)

| Skenario | Jenis | Status |
|---|---|---|
| Login email format salah -> emailError | N | Diotomasi |
| Login password kosong -> passwordError | N | Diotomasi |
| Login sukses: Loading -> LoggedIn, token disimpan | P | Diotomasi |
| Login 401 -> pesan kredensial salah | N | Diotomasi |
| Login 401 tanpa body -> pesan default | N | Diotomasi |
| Login gagal jaringan -> pesan jaringan | N | Diotomasi |
| Mengetik setelah error -> status error dibersihkan | P | Diotomasi |
| consumeStatus: LoggedIn -> Idle | P | Diotomasi |
| Register password < 8 karakter ditolak | B | Diotomasi |
| Register konfirmasi tidak sama -> confirmError | N | Diotomasi |
| Register sukses -> Registered, email dipertahankan | P | Diotomasi |
| Register 409 -> email sudah terdaftar | N | Diotomasi |
| Logout -> token dihapus, form direset | P | Diotomasi |

## C. Mobile - Jaringan (`AuthInterceptorTest`, 4 test)

| Skenario | Jenis | Status |
|---|---|---|
| Header Bearer ditambahkan jika token ada | P | Diotomasi |
| Header tidak ditambahkan jika token null | N | Diotomasi |
| Header tidak ditambahkan jika token kosong | N | Diotomasi |
| Header Authorization yang sudah ada dipertahankan | B | Diotomasi |

## D. Backend - Jest (`server/tests/unit/`, 27 test)

Dijalankan oleh `server-ci.yml` melalui `npm test`. Database dan JWT di-mock, sehingga tidak membutuhkan PostgreSQL.

| Berkas | Skenario | Jumlah | Status |
|---|---|---|---|
| `authController.test.js` (register) | field kosong 400; format email salah 400; password < 8 karakter 400; sukses 201 dengan password ter-hash; email duplikat (23505) 409; error DB 500 | 6 | Diotomasi |
| `authController.test.js` (login) | field kosong 400; email tidak terdaftar 401; password salah 401 (pesan sama); sukses 200 dengan token tanpa field password; error DB 500 | 5 | Diotomasi |
| `password.test.js` (bcrypt) | hash berbeda dari password asli; format bcrypt cost 10; salt acak; comparePassword benar dan salah; menolak input non-string | 6 | Diotomasi |
| `token.test.js` (JWT) | format 3 bagian; payload id dan role; tanpa password; masa berlaku 24 jam; secret salah ditolak | 5 | Diotomasi |
| `verifyToken.test.js` | tanpa header 401; tanpa prefix Bearer 401; token tidak valid 401; token kedaluwarsa 401; token valid memanggil next() | 5 | Diotomasi |

Belum tercakup: pengujian integrasi endpoint ke database PostgreSQL sungguhan (rencana Sprint 2).

## Ringkasan

| Area | Test otomatis |
|---|---|
| A. AuthValidator | 13 |
| B. AuthViewModel | 13 |
| C. AuthInterceptor | 4 |
| Bawaan (`ExampleUnitTest`) | 1 |
| **Total mobile (`testDebugUnitTest`)** | **31** |
| D. Backend (Jest) | 27 |
| **Total keseluruhan** | **58** |

## Code Coverage

Belum diukur. Perlu JaCoCo (mobile) dan `npm run test:coverage` (server, sudah tersedia). Isi persentase setelah dijalankan.
