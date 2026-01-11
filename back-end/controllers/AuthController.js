import db from "../models/index";
import { Op } from "sequelize";
import crypto from "crypto";
import argon2 from "argon2";
import jwt from "jsonwebtoken";

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

    //create access token
    const accessToken = jwt.sign(
      {
        userId: user.id,
        role: user.role,
      },
      process.env.ACCESS_TOKEN_SECRET,
      {
        expiresIn: "15m",
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
    //update refresh_token vao DB

    const hashedRefreshToken = await argon2.hash(refreshToken);
    await db.User.update(
      { refresh_token: hashedRefreshToken },
      { where: { id: user.id } }
    );
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

    //  Create user

    const u_id = crypto.randomUUID();

    const newUser = await db.User.create({
      u_id,
      email: isEmail ? username : null,
      phone: isPhone ? username : null,
      password: hashedPassword,
      fullname: fullname || "user default",
      status: "active",
      role: "user",
    });

    //  Response
    return res.status(201).json({
      message: "Register successfully",
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
    let decoded;
    try {
      decoded = jwt.verify(refreshToken, process.env.REFRESH_TOKEN_SECRET);
    } catch (err) {
      return res
        .status(403)
        .json({ message: "Invalid or expired refresh token" });
    }

    const userId = decoded.userId;

    // Lấy user theo userId
    const user = await db.User.findByPk(userId);

    if (!user || !user.refresh_token)
      return res.status(403).json({ message: "Refresh token not found" });

    // So sánh hash refresh token
    const isMatch = await argon2.verify(user.refresh_token, refreshToken);
    if (!isMatch)
      return res.status(403).json({ message: "Invalid refresh token" });

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
    const user = await db.User.findByPk(userId);
    if (!user || !user.refresh_token)
      return res.status(403).json({ message: "Refresh token not found" });

    // So sánh hash refresh token
    const isMatch = await argon2.verify(user.refresh_token, refreshToken);
    if (!isMatch)
      return res.status(403).json({ message: "Invalid refresh token" });
    // Xóa refresh token
    await db.User.update({ refresh_token: null }, { where: { id: userId } });

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
  const user = await db.User.findOne({
    where: {
      [Op.or]: [{ email: username }, { phone: username }],
    },
  });
  // if not found user
  if (!user) return res.status(404).json({ message: "User not found" });

  const resetToken = crypto.randomBytes(16).toString("hex");
  const expire = Date.now() + 15 * 60 * 1000;

  await db.User.update(
    { reset_token: resetToken, reset_token_expire: expire },
    { where: { id: user.id } }
  );

  // trả token trực tiếp (thay vì gửi email)
  return res
    .status(200)
    .json({ message: "Token generated", token: resetToken });
};

const resetPassword = async (req, res) => {
  const { token, password } = req.body;
  if (!token || !password)
    return res.status(400).json({ message: "Token & new password required" });

  // Password strength check
  if (password.length < 8) {
    return res.status(400).json({
      message: "Password must be at least 8 characters long",
    });
  }

  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).{8,}$/;

  if (!passwordRegex.test(password)) {
    return res.status(400).json({
      message:
        "Password must include uppercase, lowercase, number and special character",
    });
  }
  if (password.length > 32) {
    return res.status(400).json({
      message: "Password too long",
    });
  }

  const user = await db.User.findOne({
    where: { reset_token: token, reset_token_expire: { [Op.gt]: Date.now() } },
  });
  if (!user)
    return res.status(400).json({ message: "Invalid or expired token" });

  const hashedPassword = await argon2.hash(password);

  await db.User.update(
    { password: hashedPassword, reset_token: null, reset_token_expire: null },
    { where: { id: user.id } }
  );

  return res.status(200).json({ message: "Password updated successfully" });
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
};
