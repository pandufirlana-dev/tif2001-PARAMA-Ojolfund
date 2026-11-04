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

## D. Backend (`/api/v1/auth`) - target `server/tests/auth.test.js`

Skrip `test` belum ada di `server/package.json` (menunggu Mhs 2). Workflow `server-ci.yml` memakai `npm test --if-present`, jadi otomatis ikut berjalan begitu skrip ditambahkan.

| ID | Endpoint | Input | Respons diharapkan | Jenis | Status |
|---|---|---|---|---|---|
| D01 | POST /register | data valid | 201, `user` tanpa password | P | Belum diotomasi |
| D02 | POST /register | field wajib kosong | 400 | N | Belum diotomasi |
| D03 | POST /register | email `abc` | 400 format email | N | Belum diotomasi |
| D04 | POST /register | password 7 karakter | 400 | B | Belum diotomasi |
| D05 | POST /register | password 8 karakter | 201 | B | Belum diotomasi |
| D06 | POST /register | email sudah ada (DB `23505`) | 409 | N | Belum diotomasi |
| D07 | POST /register | error DB lain | 500 | N | Belum diotomasi |
| D08 | POST /login | kredensial benar | 200 + token JWT | P | Belum diotomasi |
| D09 | POST /login | field kosong | 400 | N | Belum diotomasi |
| D10 | POST /login | password salah / email tak terdaftar | 401 | N | Belum diotomasi |
| D11 | GET /me | token valid | 200 | P | Belum diotomasi |
| D12 | GET /me | tanpa header | 401 "Token tidak ditemukan" | N | Belum diotomasi |
| D13 | GET /me | token rusak | 401 "Token tidak valid" | N | Belum diotomasi |

## Ringkasan

| Area | Test otomatis |
|---|---|
| A. AuthValidator | 13 |
| B. AuthViewModel | 13 |
| C. AuthInterceptor | 4 |
| Bawaan (`ExampleUnitTest`) | 1 |
| **Total mobile (`testDebugUnitTest`)** | **31** |
| D. Backend | 0 dari 13 skenario |

## Code Coverage

Belum diukur. Perlu JaCoCo (mobile) dan Jest `--coverage` (server). Isi persentase setelah dikonfigurasi.
