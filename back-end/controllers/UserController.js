import db from "../models/index";
import jwt from "jsonwebtoken";
import { NUMBER, Op, Sequelize } from "sequelize";

const getProfile = async (req, res) => {
  try {
    const userId = req.user.userId;
    if (!userId)
      return res.status(400).json({ message: "Token missing userId" });

    const user = await db.User.findByPk(userId, {
      attributes: ["id", "email", "phone", "full_name", "role", "status"],
    });

    if (!user) return res.status(404).json({ message: "User not found" });

    return res.status(200).json({ user });
  } catch (error) {
    console.error("GET PROFILE ERROR:", error);
    return res.status(500).json({ error: error.message });
  }
};

const getUsers = async (req, res) => {
  try {
    const currentUserId = req.user.userId;

    const users = await db.User.findAll({
      where: {
        id: {
          [Op.ne]: currentUserId,
        },
      },
      attributes: ["id", "full_name", "avatar", "status", "role"],
    });

    return res.json({
      message: "Get user success",
      data: users,
    });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const getUserById = async (req, res) => {
  try {
    const { id } = req.params;
    const page = Number(req.query.page) || 1;
    const limit = 10;
    const offset = (page - 1) * limit;
    const [user, postData, total] = await Promise.all([
      db.User.findOne({
        where: {
          id,
        },
        attributes: ["id", "full_name", "email", "phone", "avatar", "status"],
      }),

      db.Post.findAll({
        subQuery: false,
        limit,
        offset,
        order: [["created_at", "DESC"]],
        where: {
          [Op.and]: [
            { status: "approved" },
            {
              [Op.or]: [{ privacy: "private", user_id: id }],
            },
          ],
        },
        include: [
          {
            model: db.User,
            attributes: ["id", "full_name", "avatar"],
          },
          {
            model: db.PostMedia,
          },
        ],
      }),
      db.Post.count({
        where: {
          [Op.and]: [
            { status: 1 },
            {
              [Op.or]: [{ privacy: 0 }, { privacy: 2, user_id: id }],
            },
          ],
        },
      }),
    ]);
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }

    return res.status(200).json({
      message: "Post",
      total: total,
      page,
      limit,
      user: user,
      post: postData,
    });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};
const update = async (req, res) => {
  try {
    console.log(req.body);
    const userId = req.user.userId;
    if (!userId)
      return res.status(400).json({ message: "Token missed userId" });
    const user = await db.User.findByPk(userId);
    if (!user) return res.status(404).json({ message: "User does not found" });
    const data = {};
    if (req.body.full_name !== undefined) data.full_name = req.body.full_name;
    if (req.body.phone !== undefined) data.phone = req.body.phone;
    if (req.body.avatar !== undefined) data.avatar = req.body.avatar;
    if (req.body.gender !== undefined) data.gender = req.body.gender;
    if (Object.keys(data).length === 0) {
      return res.status(400).json({ message: "No data to update" });
    }

    if (![0, 1, 2].includes(data.gender))
      return res
        .status(400)
        .json({ message: "gender must be 0 (female), 1 (male), or 2 (other)" });

    const isPhone = /^0[0-9]{9}$/.test(data.phone);

    if (!isPhone) {
      return res.status(400).json({
        message: "Phone invalid format",
      });
    }
    console.log({ data });
    await user.update(data);
    return res.status(200).json({
      message: "Update user successfully",
      data: {
        id: user.id,
        full_name: user.full_name,
        phone: user.phone,
        avatar: user.avatar,
        gender: user.gender,
      },
    });
  } catch (error) {
    console.log(error.message);
    return res.status(500).json({ message: error.message });
  }
};

export default {
  getProfile,
  getUsers,
  getUserById,
  update,
};
