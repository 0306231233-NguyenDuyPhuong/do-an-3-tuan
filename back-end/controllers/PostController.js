import { stat } from "@babel/core/lib/gensync-utils/fs.js";
import db from "../models/index.js";
import { Sequelize, where, Op } from "sequelize";

const getPostUser = async (req, res) => {
  try {
    const page = Number(req.query.page) || 1;
    const limit = 10;
    const offset = (page - 1) * limit;
    const userId = Number(req.user.userId);
    const userRole = Number(req.user.role);
    if (userRole != 0) {
      return res.status(403).json({
        message: "User no access rights"
      })
    }

    const friendBlock = await db.Friendship.findAll({
      where: {
        user_id: userId,
        [Op.and]:[
          {
            status:3
          }
        ]
      },
      attributes: ["friend_id"]
    })
    const blockIds = Object.values(friendBlock).map(item=>item.friend_id)
    const [postData, postTotal] = await Promise.all([
      db.Post.findAll({
        subQuery: false,
        limit,
        offset,
        order: [['created_at', 'DESC']],
        where: {
          id: {[Op.notIn]: blockIds},
          [Op.and]: [
            { status: 1 }, // sửa =0 -> 1
            {
              [Op.or]: [
                { privacy: 0 },
                { privacy: 1, user_id: userId }
              ]
            },
          ]
        },
        include: [
          {
            model: db.User,
            attributes: ["id", "full_name", "avatar"]
          },
          {
            model: db.PostMedia
          },
          {
            model: db.Location
          }
        ],
        group: ["Post.id", "User.id"]
      }),
      db.Post.count({
        where: {
          [Op.and]: [
            { status: 1 },//sửa status=0
            {
              [Op.or]: [
                { privacy: 0 },
                { privacy: 1, user_id: userId }
              ]
            }
          ]
        }
      })
    ]);

    return res.status(200).json({
      message: "Post",
      total: postTotal,
      page,
      limit,
      data: postData
    });
  } catch (error) {
    return res.status(500).json({
      error: error
    })
  }
};

const getPostAdmin = async (req, res) => {
  const page = Number(req.query.page) || 1;
  const limit = 10;
  const offset = (page - 1) * limit;
  const userRole = Number(req.user.role);
  if (userRole != 1) {
    return res.status(403).json({
      message: "User no access rights"
    })
  }
  const [postData, totalPost] = await Promise.all([
    db.Post.findAll({
      subQuery: false,
      limit,
      offset,
      include: [
        {
          model: db.User,
          attributes: ["id", "full_name", "avatar"]
        },
        {
          model: db.PostMedia,
          separate: true
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
    where: {
      id
    },
    include: [
      {
        model: db.User,
        attributes: ["id", "full_name", "avatar", "status"]
      },
      {
        model: db.Location
      },
      {
        model: db.PostMedia,
      }
    ],
    group: ["Post.id", "User.id", "Location.id", "PostMedia.id"]
  });
  return res.status(200).json({
    message: "Get post success",
    data: postData
  });
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

const putPostUser = async (req, res) => {
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

const putPostAdmin = async (req, res) => {
  const { id } = req.params;
  const role = req.user.role;
  if (role !== 1) {
    return res.status(400).json({
      message: 'User not admin'
    })
  }
  await db.Post.update(req.body, { where: { id } });
  return res.status(200).json({
    message: "Update post success"
  })
}

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
  putPostUser,
  putPostAdmin,
  deletePost,
};