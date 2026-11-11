# Spesifikasi API: Modul Autentikasi (Sprint 1)

Base URL (development): `http://localhost:5000/api/v1`

| Lingkungan                                         | Base URL                        |
| -------------------------------------------------- | ------------------------------- |
| Emulator Android                                   | `http://10.0.2.2:5000/api/v1/`  |
| HP fisik via USB (`adb reverse tcp:5000 tcp:5000`) | `http://localhost:5000/api/v1/` |

Semua request dan respons berformat JSON (`Content-Type: application/json`).

## 1. Register

`POST /auth/register`

**Request body**

```json
{
  "name": "Tester",
  "email": "tester@example.com",
  "password": "Password123"
}
```

| Field    | Tipe   | Wajib | Aturan                         |
| -------- | ------ | ----- | ------------------------------ |
| name     | string | Ya    | Tidak boleh kosong             |
| email    | string | Ya    | Format email valid, harus unik |
| password | string | Ya    | Minimal 8 karakter             |

**Respons sukses: 201 Created**

```json
{
  "message": "Registrasi berhasil",
  "user": {
    "id": "uuid",
    "name": "Tester",
    "email": "tester@example.com",
    "role": "user",
    "created_at": "2026-11-04T08:00:00.000Z"
  }
}
```

**Respons galat**

| Status | Kondisi                         | Body                                                     |
| ------ | ------------------------------- | -------------------------------------------------------- |
| 400    | Ada field kosong                | `{ "message": "Name, email, dan password wajib diisi" }` |
| 400    | Format email tidak valid        | `{ "message": "Format email tidak valid" }`              |
| 400    | Password kurang dari 8 karakter | `{ "message": "Password minimal 8 karakter" }`           |
| 409    | Email sudah terdaftar           | `{ "message": "Email sudah terdaftar" }`                 |
| 500    | Kesalahan server                | `{ "message": "Terjadi kesalahan pada server" }`         |

## 2. Login

`POST /auth/login`

**Request body**

```json
{
  "email": "tester@example.com",
  "password": "Password123"
}
```

**Respons sukses: 200 OK**

```json
{
  "message": "Login berhasil",
  "token": "<JWT>",
  "user": {
    "id": "uuid",
    "name": "Tester",
    "email": "tester@example.com",
    "role": "user"
  }
}
```

**Respons galat**

| Status | Kondisi                                   | Body                                              |
| ------ | ----------------------------------------- | ------------------------------------------------- |
| 400    | Email atau password kosong                | `{ "message": "Email dan password wajib diisi" }` |
| 401    | Email tidak terdaftar atau password salah | `{ "message": "Email atau kata sandi salah" }`    |
| 500    | Kesalahan server                          | `{ "message": "Terjadi kesalahan pada server" }`  |

Pesan 401 sengaja dibuat sama untuk email tidak terdaftar dan password salah, agar tidak membocorkan email mana yang terdaftar.

## 3. Health check

`GET /health`

| Status | Body                                                |
| ------ | --------------------------------------------------- |
| 200    | `{ "status": "ok", "database": "connected" }`       |
| 500    | `{ "status": "error", "database": "disconnected" }` |

## 4. Token JWT

- Algoritma bawaan `jsonwebtoken` (HS256), ditandatangani dengan `JWT_SECRET`.
- Masa berlaku: **24 jam**.
- Payload: `{ "id": "<uuid user>", "role": "<role>" }`.
- Dikirim klien pada endpoint terproteksi lewat header:

```
Authorization: Bearer <token>
```

Respons middleware `verifyToken` (untuk endpoint terproteksi pada sprint berikutnya):

| Status | Kondisi                                                       | Body                                     |
| ------ | ------------------------------------------------------------- | ---------------------------------------- |
| 401    | Header Authorization tidak ada atau tidak berawalan `Bearer ` | `{ "message": "Token tidak ditemukan" }` |
| 401    | Token tidak valid atau kedaluwarsa                            | `{ "message": "Token tidak valid" }`     |

## 5. Catatan untuk klien mobile

- `baseUrl` Retrofit harus diakhiri `/`, dan path endpoint tidak diawali `/` (misalnya `auth/login`).
- Nama field request dan respons harus sama persis dengan dokumen ini (huruf besar-kecil dihitung).
- Simpan token di DataStore atau EncryptedSharedPreferences, bukan teks polos.
- Pada development memakai `http`, Android butuh `usesCleartextTraffic` atau network security config.
