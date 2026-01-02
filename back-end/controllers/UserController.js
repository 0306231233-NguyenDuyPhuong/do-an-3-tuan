import db from "../models/index"
import jwt from "jsonwebtoken";

const login = async (req, res) => {
  try {
    const data = await db.User.findAll();
    return res.status(200).json({
      message: 'ok',
      data
    });
  } catch (error) {
    console.error('❌ MYSQL ERROR:', error);   // 👈 CỰC KỲ QUAN TRỌNG
    return res.status(500).json({
      error: error.message
    });
  }
};

const getProfile = async (req, res) => {
  try {
    const userId = req.user.userId;
    if (!userId) return res.status(400).json({ message: "Token missing userId" });

    const user = await db.User.findByPk(userId, {
      attributes: ["id", "email", "phone", "full_name", "role", "status"]
    });

    if (!user) return res.status(404).json({ message: "User not found" });

    return res.status(200).json({ user });
  } catch (error) {
    console.error("GET PROFILE ERROR:", error);
    return res.status(500).json({ error: error.message });
  }
};


export default {
    login:login,
    getProfile
    
}