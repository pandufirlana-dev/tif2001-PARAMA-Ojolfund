const jwt = require('jsonwebtoken');

const EXPIRES_IN = '24h';

const generateToken = (user) =>
  jwt.sign({ id: user.id, role: user.role }, process.env.JWT_SECRET, {
    expiresIn: EXPIRES_IN,
  });

module.exports = { generateToken, EXPIRES_IN };
