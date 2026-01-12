import db from "../models/index"
import jwt from "jsonwebtoken";
import { Op,Sequelize } from "sequelize";

const login = async (req, res) => {
  try {
    const data = await db.User.findAll();
    return res.status(200).json({
      message: 'ok',
      data
    });
  } catch (error) {
    console.error('MYSQL ERROR:', error);  
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


const getUsers = async (req, res) => {
  try {
    const {search, userId, role} = req.query;
    const currentUserId = req.user.userId; 
    const whereUser = {
      ...(search&& {
        full_name: {[Op.like]: `%${search}%`}
      }),
      ...(userId !== undefined && {
        id: userId
      }),
      ...(role !== undefined && {
        role: role
      })
    }
    const users = await db.User.findAll({
      where: whereUser,
      attributes: ["id", "full_name", "avatar", "status", "role"], 
    });

    return res.json(
      {
        message: "Get user success",
        data:users
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
          id
        },
        attributes: ["id", "full_name", "email", "phone", "avatar", "status"]
      }),

      db.Post.findAll({
            subQuery: false,
            limit,
            offset,
            order: [['created_at', 'DESC']],
            where: {
              [Op.and]: [
                { status: 1 },
                {
                  [Op.or]: [
                    { /*privacy: 2, */user_id: id },
                  ]
                }
              ]
              },
            include: [
              {
                model: db.User,
                attributes: ["id", "full_name", "avatar"]
              },
              {
                model: db.PostMedia,
              }
            ],
          }),
          db.Post.count({
                where: {
                  [Op.and]: [
                    { status: 1},
                    {
                      [Op.or]: [
                        { privacy: 0 },
                        { privacy: 2, user_id: id },
                      ]
                    }
                  ]
          
                },
              })
    ])
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }

    return res.status(200).json({
          message: "Post",
          total: total,
          page,
          limit,
          user: user,
          post: postData
    });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const getAdminUserById = async(req,res)=>{
  try {
    const { id } = req.params;
    const page = Number(req.query.page) || 1;
    const limit = 10;
    const offset = (page - 1) * limit;
    const [user, postData, total] = await Promise.all([
      db.User.findOne({
        where: {
          id
        },
        attributes: ["id", "full_name", "email", "phone", "avatar", "status"]
      }),

      db.Post.findAll({
            subQuery: false,
            limit,
            offset,
            order: [['created_at', 'DESC']],
            where: {
              [Op.and]: [
                //{ status: "approved" },
                {
                  [Op.or]: [
                    { /*privacy: "private",*/ user_id: id },
                  ]
                }
              ]
              },
            include: [
              {
                model: db.User,
                attributes: ["id", "full_name", "avatar"]
              },
              {
                model: db.PostMedia,
              }
            ],
          }),
          db.Post.count({
                where: {
                  [Op.and]: [
                    { status: 1},
                    {
                      [Op.or]: [
                        { privacy: 0 },
                        { privacy: 2, user_id: id },
                      ]
                    }
                  ]
          
                },
              })
    ])
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }

    return res.status(200).json({
          message: "Post",
          total: total,
          page,
          limit,
          user: user,
          post: postData
    });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
}


const putUserAdmin = async (req, res) => {
  const { id } = req.params;
  const role = req.user.role;
  if (role !== 1) {
    return res.status(400).json({
      message: 'User not admin'
    })
  }
  await db.User.update(req.body, { where: { id } });
  return res.status(200).json({
    message: "Update post success"
  })
}

export default {
    login,
    getProfile,
    getUsers,
    getUserById,
    getAdminUserById,
    putUserAdmin
}