# API Specification: Modul Autentikasi (Ojolfund)

Base URL: `http://localhost:5000/api/v1`

## POST /auth/register

Request body:

```json
{ "name": "string", "email": "string", "password": "string (min 8 karakter)" }
```

Response 201:

```json
{
  "message": "Registrasi berhasil",
  "user": {
    "id": "uuid",
    "name": "...",
    "email": "...",
    "role": "user",
    "created_at": "..."
  }
}
```

Response 400: `{ "message": "Format email tidak valid" }` (atau field kosong / password < 8)
Response 409: `{ "message": "Email sudah terdaftar" }`

## POST /auth/login

Request body:

```json
{ "email": "string", "password": "string" }
```

Response 200:

```json
{
  "message": "Login berhasil",
  "token": "jwt (berlaku 24 jam)",
  "user": { "id": "...", "name": "...", "email": "...", "role": "user" }
}
```

Response 400: `{ "message": "Email dan password wajib diisi" }`
Response 401: `{ "message": "Email atau kata sandi salah" }`

## GET /auth/me

Header: `Authorization: Bearer <token>`
Response 200: `{ "message": "Token valid", "user": { "id": "...", "role": "..." } }`
Response 401: `{ "message": "Token tidak ditemukan" }` atau `"Token tidak valid atau kedaluwarsa"`

## HTTP Status Code Reference

| Code | Meaning                                           |
| ---- | ------------------------------------------------- |
| 200  | OK — request berhasil                             |
| 201  | Created — data baru berhasil dibuat               |
| 400  | Bad Request — permintaan tidak valid              |
| 401  | Unauthorized — token tidak ada / tidak valid      |
| 403  | Forbidden — tidak punya akses (role tidak sesuai) |
| 404  | Not Found — data tidak ditemukan                  |
| 422  | Unprocessable Entity — validasi input gagal       |
| 500  | Internal Server Error — kesalahan di sisi server  |
