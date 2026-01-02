import { stat } from "@babel/core/lib/gensync-utils/fs.js";
import db from "../models/index.js";
import { Sequelize, where, Op } from "sequelize";

const getPostUser = async (req, res) => {
  const page = Number(req.query.page) || 1;
  const limit = 10;
  const offset = (page - 1) * limit;
  const userId = req.user.userId;
  const blockedIds = userId.map((item)=>item.id);
  const [postData, postTotal] = await Promise.all([
    db.Post.findAll({
      subQuery: false,
      limit,
      offset,
      order: [['created_at', 'DESC']],
      where: {
        [Op.or]: [
          { privacy: "public" },
          { privacy: "private", user_id: userId }
        ],
        userId: { [Sequelize.Op.notIn]: blockedIds }
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
        [Op.or]: [
          { privacy: "public" },
          { privacy: "private", user_id: userId },
        ],
      },
    })
  ])
  return res.status(200).json({
    message: "Post",
    total: postTotal,
    page,
    limit,
    data: postData
  });
};


const getPostAdmin = async (req, res) => {
  const page = Number(req.query.page) || 1;
  const limit = 10;
  const offset = (page - 1) * limit;

  const [postData, totalPost] = await Promise.all([
    db.Post.findAll({
      subQuery: false,
      limit,
      offset,
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
          model: db.PostMedia
        }
      ],
      group: ["Post.id", "User.id"]
    }),
    db.Post.count(
      {
        distinct: true,
        col: "id",
      }
    )
  ])

  return res.status(200).json({
    message: "Get post success",
    total: totalPost,
    limit,
    page,
    data: postData
  })
}

const getPostById = async (req, res) => {
  const { id } = req.params;
  const postData = await db.Post.findOne({
    id,
    include: db.User
  });
  return res.status(200).json({ message: "Get post success", data: postData });
};

const postPost = async (req, res) => {
  const { user_id, location_id } = req.body;
  const userCheck = await db.User.findOne({
    where: { id: user_id }
  });
  const locationCheck = await db.Location.findOne({
    where: { id: location_id }
  });
  if (!locationCheck) {
    return res.status(404).json({
      message: "Location not found",
    });
  }
  if (!userCheck) {
    return res.status(404).json({
      message: "User not found",
    });
  }
  const postData = await db.Post.create(req.body);
  return res.status(201).json({
    message: "Insert post success",
    data: postData
  });
};

const putPost = async (req, res) => {
  const { user_id, location_id } = req.body;
  const { id } = req.params;
  const userCheck = await db.User.findOne({
    where: { id: user_id }
  });
  const locationCheck = await db.Location.findOne({
    where: { id: location_id }
  });
  if (!locationCheck) {
    return res.status(404).json({
      message: "Location not found",
    });
  }
  if (!userCheck) {
    return res.status(404).json({
      message: "User not found",
    });
  }
  const postData = await db.Post.findByPk(id);
  if (!postData) {
    return res.status(404).json({
      message: "post not found",
    });
  } else {
    await db.Post.update(req.body, { where: { id } });
    return res.status(201).json({
      message: "Update post success",
    });
  }
};

const deletePost = async (req, res) => {
  const { id } = req.params;
  const postData = await db.Post.findByPk(id);
  if (!postData) {
    return res.status(404).json({
      message: "post not found",
    });
  } else {
    await db.Post.update(req.body, { where: { id } });
    return res.status(201).json({
      message: "Delete post success",
    });
  }
};

export default {
  getPostUser,
  getPostAdmin,
  getPostById,
  postPost,
  putPost,
  deletePost,
};
