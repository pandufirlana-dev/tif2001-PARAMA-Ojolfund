const bcrypt = require('bcryptjs');
const { hashPassword, comparePassword, SALT_ROUNDS } = require('../../src/utils/password');

describe('password utils (bcrypt)', () => {
  test('hash tidak sama dengan password asli', async () => {
    const hash = await hashPassword('Password123');
    expect(hash).not.toBe('Password123');
  });

  test('hash berformat bcrypt dengan cost 10', async () => {
    const hash = await hashPassword('Password123');
    expect(hash).toMatch(/^\$2[aby]\$10\$/);
    expect(bcrypt.getRounds(hash)).toBe(SALT_ROUNDS);
  });

  test('password sama menghasilkan hash berbeda (salt acak)', async () => {
    const h1 = await hashPassword('Password123');
    const h2 = await hashPassword('Password123');
    expect(h1).not.toBe(h2);
  });

  test('comparePassword true untuk password yang cocok', async () => {
    const hash = await hashPassword('Password123');
    await expect(comparePassword('Password123', hash)).resolves.toBe(true);
  });

  test('comparePassword false untuk password yang salah', async () => {
    const hash = await hashPassword('Password123');
    await expect(comparePassword('SalahBanget', hash)).resolves.toBe(false);
  });

  test('hashPassword menolak input bukan string', async () => {
    await expect(hashPassword(12345678)).rejects.toThrow();
  });
});
