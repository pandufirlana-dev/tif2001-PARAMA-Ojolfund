const jwt = require('jsonwebtoken');
const verifyToken = require('../../src/middlewares/verifyToken');
const { generateToken } = require('../../src/utils/token');
const { createRes } = require('../helpers/mockRes');

describe('middleware verifyToken', () => {
  test('tanpa header Authorization → 401 Token tidak ditemukan', () => {
    const res = createRes();
    const next = jest.fn();
    verifyToken({ headers: {} }, res, next);
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({ message: 'Token tidak ditemukan' });
    expect(next).not.toHaveBeenCalled();
  });

  test('header tanpa prefix Bearer → 401', () => {
    const res = createRes();
    const next = jest.fn();
    verifyToken({ headers: { authorization: 'Token abc' } }, res, next);
    expect(res.status).toHaveBeenCalledWith(401);
    expect(next).not.toHaveBeenCalled();
  });

  test('token tidak valid → 401 Token tidak valid', () => {
    const res = createRes();
    const next = jest.fn();
    verifyToken({ headers: { authorization: 'Bearer abc.def.ghi' } }, res, next);
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({ message: 'Token tidak valid' });
    expect(next).not.toHaveBeenCalled();
  });

  test('token kedaluwarsa → 401', () => {
    const expired = jwt.sign({ id: 'x' }, process.env.JWT_SECRET, { expiresIn: -10 });
    const res = createRes();
    const next = jest.fn();
    verifyToken({ headers: { authorization: `Bearer ${expired}` } }, res, next);
    expect(res.status).toHaveBeenCalledWith(401);
    expect(next).not.toHaveBeenCalled();
  });

  test('token valid → next() dipanggil dan req.user terisi', () => {
    const token = generateToken({ id: 'uuid-123', role: 'user' });
    const req = { headers: { authorization: `Bearer ${token}` } };
    const res = createRes();
    const next = jest.fn();
    verifyToken(req, res, next);
    expect(next).toHaveBeenCalledTimes(1);
    expect(req.user.id).toBe('uuid-123');
    expect(req.user.role).toBe('user');
  });
});
