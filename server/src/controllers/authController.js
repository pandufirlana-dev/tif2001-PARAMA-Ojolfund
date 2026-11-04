const pool = require('../config/db');
const { hashPassword, comparePassword } = require('../utils/password');
const { generateToken } = require('../utils/token');
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const register = async (req, res) => {
  const { name, email, password } = req.body;

  if (!name || !email || !password) {
    return res.status(400).json({ message: 'Name, email, dan password wajib diisi' });
  }
  if (!EMAIL_REGEX.test(email)) {
    return res.status(400).json({ message: 'Format email tidak valid' });
  }
  if (password.length < 8) {
    return res.status(400).json({ message: 'Password minimal 8 karakter' });
  }

  try {
    const hashed = await hashPassword(password);
    const result = await pool.query(
      `INSERT INTO users (name, email, password)
       VALUES ($1, $2, $3)
       RETURNING id, name, email, role, created_at`,
      [name, email, hashed],
    );
    return res.status(201).json({ message: 'Registrasi berhasil', user: result.rows[0] });
  } catch (err) {
    if (err.code === '23505') {
      return res.status(409).json({ message: 'Email sudah terdaftar' });
    }
    console.error(err);
    return res.status(500).json({ message: 'Terjadi kesalahan pada server' });
  }
};

const login = async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({ message: 'Email dan password wajib diisi' });
  }

  try {
    const result = await pool.query('SELECT * FROM users WHERE email = $1', [email]);
    const user = result.rows[0];

    if (!user || !(await comparePassword(password, user.password))) {
      return res.status(401).json({ message: 'Email atau kata sandi salah' });
    }

    const token = generateToken(user);

    return res.status(200).json({
      message: 'Login berhasil',
      token,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        role: user.role,
      },
    });
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Terjadi kesalahan pada server' });
  }
};

module.exports = { register, login };
