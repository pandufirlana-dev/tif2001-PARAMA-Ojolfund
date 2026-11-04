jest.mock('../../src/config/db', () => ({ query: jest.fn() }));

const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const pool = require('../../src/config/db');
const { register, login } = require('../../src/controllers/authController');
const { createRes } = require('../helpers/mockRes');

beforeEach(() => {
  pool.query.mockReset();
  jest.spyOn(console, 'error').mockImplementation(() => {});
});

afterEach(() => {
  jest.restoreAllMocks();
});

describe('register', () => {
  const body = { name: 'Tester', email: 'tester@example.com', password: 'Password123' };

  test('field kosong → 400', async () => {
    const res = createRes();
    await register({ body: { email: 'a@b.com' } }, res);
    expect(res.status).toHaveBeenCalledWith(400);
    expect(pool.query).not.toHaveBeenCalled();
  });

  test('format email tidak valid → 400', async () => {
    const res = createRes();
    await register({ body: { ...body, email: 'bukan-email' } }, res);
    expect(res.status).toHaveBeenCalledWith(400);
  });

  test('password kurang dari 8 karakter → 400', async () => {
    const res = createRes();
    await register({ body: { ...body, password: 'short' } }, res);
    expect(res.status).toHaveBeenCalledWith(400);
  });

  test('berhasil → 201, password disimpan sebagai hash', async () => {
    pool.query.mockResolvedValue({
      rows: [{ id: 'uuid-1', name: 'Tester', email: body.email, role: 'user' }],
    });
    const res = createRes();
    await register({ body }, res);

    expect(res.status).toHaveBeenCalledWith(201);
    const savedPassword = pool.query.mock.calls[0][1][2];
    expect(savedPassword).not.toBe('Password123');
    expect(savedPassword).toMatch(/^\$2[aby]\$10\$/);
    expect(res.json.mock.calls[0][0].user.password).toBeUndefined();
  });

  test('email duplikat (kode 23505) → 409', async () => {
    pool.query.mockRejectedValue({ code: '23505' });
    const res = createRes();
    await register({ body }, res);
    expect(res.status).toHaveBeenCalledWith(409);
  });

  test('error database lain → 500', async () => {
    pool.query.mockRejectedValue(new Error('db down'));
    const res = createRes();
    await register({ body }, res);
    expect(res.status).toHaveBeenCalledWith(500);
  });
});

describe('login', () => {
  const credentials = { email: 'tester@example.com', password: 'Password123' };
  let storedUser;

  beforeAll(async () => {
    storedUser = {
      id: 'uuid-1',
      name: 'Tester',
      email: credentials.email,
      role: 'user',
      password: await bcrypt.hash('Password123', 4), // cost rendah agar test cepat
    };
  });

  test('field kosong → 400', async () => {
    const res = createRes();
    await login({ body: { email: 'a@b.com' } }, res);
    expect(res.status).toHaveBeenCalledWith(400);
  });

  test('email tidak terdaftar → 401', async () => {
    pool.query.mockResolvedValue({ rows: [] });
    const res = createRes();
    await login({ body: credentials }, res);
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({ message: 'Email atau kata sandi salah' });
  });

  test('password salah → 401 dengan pesan yang sama', async () => {
    pool.query.mockResolvedValue({ rows: [storedUser] });
    const res = createRes();
    await login({ body: { ...credentials, password: 'SalahBanget' } }, res);
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({ message: 'Email atau kata sandi salah' });
  });

  test('berhasil → 200, token valid, tanpa field password', async () => {
    pool.query.mockResolvedValue({ rows: [storedUser] });
    const res = createRes();
    await login({ body: credentials }, res);

    expect(res.status).toHaveBeenCalledWith(200);
    const payload = res.json.mock.calls[0][0];
    expect(jwt.verify(payload.token, process.env.JWT_SECRET).id).toBe('uuid-1');
    expect(payload.user.password).toBeUndefined();
  });

  test('error database → 500', async () => {
    pool.query.mockRejectedValue(new Error('db down'));
    const res = createRes();
    await login({ body: credentials }, res);
    expect(res.status).toHaveBeenCalledWith(500);
  });
});
