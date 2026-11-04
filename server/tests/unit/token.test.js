const jwt = require('jsonwebtoken');
const { generateToken } = require('../../src/utils/token');

const user = { id: 'uuid-123', role: 'user' };

describe('token generator (JWT)', () => {
  test('menghasilkan token berformat 3 bagian', () => {
    expect(generateToken(user).split('.')).toHaveLength(3);
  });

  test('payload memuat id dan role', () => {
    const decoded = jwt.verify(generateToken(user), process.env.JWT_SECRET);
    expect(decoded.id).toBe('uuid-123');
    expect(decoded.role).toBe('user');
  });

  test('payload tidak memuat password', () => {
    const decoded = jwt.verify(
      generateToken({ ...user, password: 'rahasia' }),
      process.env.JWT_SECRET,
    );
    expect(decoded.password).toBeUndefined();
  });

  test('masa berlaku token 24 jam', () => {
    const decoded = jwt.verify(generateToken(user), process.env.JWT_SECRET);
    expect(decoded.exp - decoded.iat).toBe(24 * 60 * 60);
  });

  test('token tidak bisa diverifikasi dengan secret yang salah', () => {
    expect(() => jwt.verify(generateToken(user), 'secret-lain')).toThrow();
  });
});
