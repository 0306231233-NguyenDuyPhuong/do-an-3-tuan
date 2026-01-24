import db from "../models/index";
import { Op, where } from "sequelize";
import crypto, { verify } from "crypto";
import argon2 from "argon2";
import jwt from "jsonwebtoken";
import createTransporter from "../config/mailer.js";
import dotenv from "dotenv";
dotenv.config();
import { redisClient } from "../config/redis.js";

//0: inactive, 1: active, 2: banned
const login = async (req, res) => {
  try {
    const { username, password } = req.body;
    if (!username || !password)
      return res.status(400).json({
        message: "Username and password are required",
      });

    //find user in database
    const user = await db.User.findOne({
      where: {
        [Op.or]: [{ email: username }, { phone: username }],
      },
    });

    // if not found user
    if (!user || !(await argon2.verify(user.password, password))) {
      return res.status(401).json({
        message: "Invalid credentials",
      });
    }
    if (user.status === 2)
      return res.status(403).json({
        message: "Account being locked",
      });
    if (!user.is_verified)
      return res.status(403).json({
        message: "Account not verified",
      });

    //create access token
    const accessToken = jwt.sign(
      {
        userId: user.id,
        role: user.role,
      },
      process.env.ACCESS_TOKEN_SECRET,
      {
        expiresIn: "1h",
      }
    );
    //create refresh token
    const refreshToken = jwt.sign(
      {
        userId: user.id,
        role: user.role,
      },
      process.env.REFRESH_TOKEN_SECRET,
      {
        expiresIn: "7d",
      }
    );
    await redisClient.set(`refresh_token:${user.id}`, refreshToken, {
      EX: 7 * 24 * 60 * 60,
    });
    return res.status(200).json({
      message: "Login successful",
      accessToken,
      refreshToken,
      user: {
        id: user.id,
        email: user.email,
        phone: user.phone,
        full_name: user.full_name,
        role: user.role,
        avater: user.avatar
      },
    });
  } catch (error) {
    console.error("MYSQL ERROR:", error);
    return res.status(500).json({
      error: error.message,
    });
  }
};

const register = async (req, res) => {
  try {
    const { username, password, rePassword, fullname } = req.body;

    // Validate required
    if (!username || !password || !rePassword) {
      return res.status(400).json({
        message: "Username, password and rePassword are required",
      });
    }

    // Password strength check
    if (password.length < 8) {
      return res.status(400).json({
        message: "Password must be at least 8 characters long",
      });
    }
    if (password.length > 32) {
      return res.status(400).json({
        message: "Password too long",
      });
    }

    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).{8,}$/;

    if (!passwordRegex.test(password)) {
      return res.status(400).json({
        message:
          "Password must include uppercase, lowercase, number and special character",
      });
    }

    if (password !== rePassword) {
      return res.status(400).json({
        message: "Passwords do not match",
      });
    }

    //  Xác định username là email hay phone
    const isEmail = username.includes("@");
    const isPhone = /^[0-9]{10}$/.test(username);

    if (!isEmail && !isPhone) {
      return res.status(400).json({
        message: "Username must be a valid email or phone number",
      });
    }

    //  Regex email (chỉ khi là email)
    if (isEmail) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(username)) {
        return res.status(400).json({
          message: "Invalid email format",
        });
      }
    }

    const whereCondition = isEmail ? { email: username } : { phone: username };
    //  Check user tồn tại
    const existedUser = await db.User.findOne({
      where: whereCondition,
    });

    if (existedUser) {
      return res.status(409).json({
        message: "User already exists",
      });
    }

    //  Hash password
    const hashedPassword = await argon2.hash(password);

    const tokenMail = crypto.randomBytes(32).toString("hex");
    //  Create user
    const newUser = await db.User.create({
      email: isEmail ? username : null,
      phone: isPhone ? username : null,
      password: hashedPassword,
      fullname: fullname || "user default",
      status: 1,
      role: "user",
      is_verified: false,
      verify_token: isEmail ? tokenMail : null,
      verify_token_expire: isEmail ? Date.now() + 15 * 60 * 1000 : null,
    });

    if (isEmail) {
      // send mail
      const verifyLink = `http://localhost:8989/api/auth/verify?token=${tokenMail}`;
      const transporter = createTransporter();
      await transporter.sendMail({
        from: `"mannopro" <${process.env.EMAIL_USER}>`,
        to: username,
        subject: "Verify Account",
        html: `
      <h3>Chào bạn</h3>
     <p>Vui lòng xác thực tài khoản:</p>
      <a href="${verifyLink}">Xác thực</a>
      `,
      });
    }
    //  Response
    return res.status(201).json({
      message: isEmail
        ? "Register successfully. Please verify email before login"
        : "Register successfully",
      user: {
        id: newUser.id,
        email: newUser.email,
        phone: newUser.phone,
        full_name: newUser.fullname,
      },
    });
  } catch (error) {
    console.error("REGISTER ERROR:", error);
    return res.status(500).json({ error: error.message });
  }
};

const refresh = async (req, res) => {
  try {
    const { refreshToken } = req.body;
    if (!refreshToken)
      return res.status(401).json({ message: "Missing refresh token" });

    // Verify JWT để lấy userId
    const REFRESH_SECRET = process.env.REFRESH_TOKEN_SECRET.trim();
    let decoded;
    try {
      decoded = jwt.verify(refreshToken, REFRESH_SECRET);
    } catch (err) {
      return res
        .status(403)
        .json({ message: "Invalid or expired refresh token" });
    }

    const userId = decoded.userId;

    // Lấy user theo userId
    const refresh_token = await redisClient.get(`refresh_token:${userId}`);
    if (!refresh_token)
      return res.status(404).json({ message: "Refresh token not found" });
    // So sánh  refresh token
    if (refresh_token !== refreshToken)
      return res.status(403).json({ message: "Invalid refresh token" });
    const user = await db.User.findByPk(userId);

    // Tạo access token mới
    const newAccessToken = jwt.sign(
      { userId: user.id, role: user.role },
      process.env.ACCESS_TOKEN_SECRET,
      { expiresIn: "1h" }
    );

    return res.json({ accessToken: newAccessToken });
  } catch (error) {
    console.error("REFRESH ERROR:", error);
    return res.status(500).json({ error: error.message });
  }
};

const logout = async (req, res) => {
  try {
    const { refreshToken } = req.body;
    if (!refreshToken)
      return res.status(400).json({ message: "Missing refresh token" });

    let decoded;
    try {
      decoded = jwt.verify(refreshToken, process.env.REFRESH_TOKEN_SECRET);
    } catch (error) {
      return res
        .status(403)
        .json({ message: "Invalid or expired refresh token" });
    }
    // Lấy user theo userId
    const userId = decoded.userId;
    //get token from redis
    const refresh_token = await redisClient.get(`refresh_token:${userId}`);

    if (!refresh_token)
      return res.status(404).json({ message: "Refresh token not found" });

    // so sanh refresh token
    if (refresh_token !== refreshToken)
      return res.status(403).json({ message: "Invalid refresh token" });

    await redisClient.del(`refresh_token:${userId}`);
    return res.status(200).json({ message: "Logout successful" });
  } catch (error) {
    console.error("LOGOUT ERROR:", error);
    return res.status(500).json({
      error: error.message,
    });
  }
};

const forgotPassword = async (req, res) => {
  const { username } = req.body;
  if (!username)
    return res.status(400).json({ message: "Username is required" });
  //find user in database
  const isEmail = username.includes("@");

  const whereCondition = isEmail ? { email: username } : { phone: username };
  const user = await db.User.findOne({
    where: whereCondition,
  });
  // if not found user
  if (!user) return res.status(404).json({ message: "User not found" });

  const resetToken = crypto.randomBytes(32).toString("hex");
  const expire = Date.now() + 15 * 60 * 1000;

  await db.User.update(
    { reset_token: resetToken, reset_token_expire: expire },
    { where: { id: user.id } }
  );
  if (isEmail) {
    const resetLink = `http://localhost:8989/api/auth/reset-password?token=${resetToken}`;

    const transporter = createTransporter();
    await transporter.sendMail({
      to: username,
      subject: "Reset Password",
      html: `
     <h3>Đặt lại mật khẩu</h3>
        <p>Link có hiệu lực trong 15 phút</p>
        <a href="${resetLink}">Đặt lại mật khẩu</a>
    `,
    });
  } else {
    return res
      .status(200)
      .json({ message: "User only resetpassword by email now" });
  }

  return res.status(200).json({
    message: "If email exists, reset link has been sent",
  });
};

const resetPassword = async (req, res) => {
  const { token } = req.query;
  if (!token)
    return res.status(400).json({ message: "Token & new password required" });

  const user = await db.User.findOne({
    where: { reset_token: token, reset_token_expire: { [Op.gt]: Date.now() } },
  });
  if (!user)
    return res.status(400).json({ message: "Invalid or expired token" });

  const password = crypto.randomBytes(6).toString("hex");
  const hashedPassword = await argon2.hash(password);

  await db.User.update(
    { password: hashedPassword, reset_token: null, reset_token_expire: null },
    { where: { id: user.id } }
  );

  const transporter = createTransporter();
  await transporter.sendMail({
    to: user.email,
    subject: "Your new password",
    html: `
     <h3>Mật khẩu mới của bạn</h3>
        <p>Mật khẩu tạm thời:</p>
        <b>${password}</b>
        <p>Vui lòng đăng nhập và đổi mật khẩu ngay</p>
    `,
  });

  return res
    .status(200)
    .json({ message: "New password has been sent to your email" });
};

const verifyEmail = async (req, res) => {
  try {
    const { token } = req.query;
    const user = await db.User.findOne({
      where: {
        verify_token: token,
        verify_token_expire: { [Op.gt]: Date.now() },
      },
    });
    if (!user)
      return res.status(400).json({ message: "Token invalid or expired" });
    if (user.is_verified)
      return res.status(200).json({ message: "Account already verified" });
    user.is_verified = true;
    user.verify_token = null;
    user.verify_token_expire = null;
    await user.save();
    return res.status(200).json({ message: "Verify email successfully" });
  } catch (error) {
    console.error("VERIFY ERROR:", error);
    return res.status(500).json({
      message: "Internal server error",
    });
  }
};

const resendEmail = async (req, res) => {
  try {
    const { email } = req.body;
    if (!email) return res.status(400).json({ message: "Email is required" });

    const user = await db.User.findOne({
      where: { email },
    });
    if (!user) return res.status(404).json({ message: "User is not found" });

    if (user.is_verified)
      return res.status(400).json({ message: "Account already verify" });
    const newToken = crypto.randomBytes(32).toString("hex");
    const expireTime = Date.now() + 15 * 60 * 1000;

    user.verify_token = newToken;
    user.verify_token_expire = expireTime;
    await user.save();

    //resend
    const verifyLink = `http://localhost:8989/api/auth/verify?token=${newToken}`;
    const transporter = createTransporter();
    await transporter.sendMail({
      from: `"mannopro" <${process.env.EMAIL_USER}>`,
      to: email,
      subject: "Resend Verify Account",
      html: `
      <h3>Chào bạn</h3>
     <p>Vui lòng xác thực tài khoản:</p>
      <a href="${verifyLink}">Xác thực</a>
      `,
    });
    return res
      .status(200)
      .json({ message: "Verifycation email has been resent" });
  } catch (error) {
    console.error("RESEND VERIFY ERROR:", error);
    return res.status(500).json({
      error: error.message,
    });
  }
};

const getUser = async (req, res) => {};

export default {
  login,
  register,
  refresh,
  logout,
  forgotPassword,
  resetPassword,
  getUser,
  verifyEmail,
  resendEmail,
};
