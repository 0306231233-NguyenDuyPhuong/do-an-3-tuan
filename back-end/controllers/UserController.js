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
    const currentUserId = req.user.userId; 

    const users = await db.User.findAll({
      where: {
        id: {
          [Op.ne]: currentUserId, 
        },
      },
      attributes: ["id", "full_name", "avatar"], 
    });

    return res.json(users);
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
       
        attributes: ["id", "full_name", "email", "phone", "avatar"]
      }),

      db.Post.findAll({
            subQuery: false,
            limit,
            offset,
            order: [['created_at', 'DESC']],
            where: {
              [Op.and]: [
                { status: "approved" },
                {
                  [Op.or]: [
                    { privacy: "private", user_id: id },
                  ]
                }
              ]
      
              // userId: { [Sequelize.Op.notIn]: blockedIds }
            },
            attributes: {
              include: [
                [
                  Sequelize.fn("COUNT", Sequelize.fn("DISTINCT", Sequelize.col("Likes.id"))),
                  "LikeCount",
                ],
                [
                  Sequelize.fn("COUNT", Sequelize.fn("DISTINCT", Sequelize.col("Comments.id"))),
                  "CommentCount"
                ]
              ]
            },
            include: [
              {
                model: db.User,
                attributes: ["id", "full_name", "avatar"]
              },
              {
                model: db.Like,
                attributes: []
              },
              {
                model: db.Comment,
                attributes: []
              },
              {
                model: db.PostMedia,
              }
            ],
            group: ["Post.id", "User.id"]
          }),
          db.Post.count({
                where: {
                  [Op.and]: [
                    { status: "approved" },
                    {
                      [Op.or]: [
                        { privacy: "public" },
                        { privacy: "private", user_id: id },
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




export default {
    login:login,
    getProfile,
    getUsers,
    getUserById
}