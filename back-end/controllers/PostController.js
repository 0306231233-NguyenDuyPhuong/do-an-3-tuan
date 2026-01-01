import db from "../models/index.js";
import PostStatus from "../constants/PostStatus.js";
import { date, when } from "joi";
import { Sequelize, where, Op } from "sequelize";
import ReportStatus from "../constants/ReportStatus.js"

const getPost = async (req, res) => {
  const userId = req.user.userId;
  const postData = await db.Post.findAll({
    where: {
      [Op.or]:[
        {privacy: "public"},
        {privacy: "private", user_id: userId}
      ],
    },
    attributes: {
      include: [
        [
          Sequelize.fn("COUNT", Sequelize.col("Likes.id")),
          "LikeCount", 
        ],
        [
          Sequelize.fn("COUNT", Sequelize.col("Comments.id")),
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
      }
    ],
    group: ["Post.id", "User.id"]
  });
  return res.status(200).json({
    message: "Post",
    data: postData
  });
};

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
  getPost,
  getPostById,
  postPost,
  putPost,
  deletePost,
};
