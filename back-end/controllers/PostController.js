import { stat } from "@babel/core/lib/gensync-utils/fs.js";
import db from "../models/index.js";
import { Sequelize, where, Op } from "sequelize";

const getPostUser = async (req, res) => {
  try {
    const { search, sort, date, dateStart, dateEnd } = req.query;
    const page = Number(req.query.page) || 1;
    const limit = 5;
    const offset = (page - 1) * limit;
    const userId = Number(req.user.userId);
    const userRole = Number(req.user.role);

    if (userRole !== 0) {
      return res.status(403).json({ message: "User not role user" });
    }

    let order = [["created_at", "DESC"]];
    if (sort === "trending") {
      order = [[
        db.sequelize.literal(`
          (like_count*1 + comment_count*2 + share_count*3)
        `),
        "DESC"
      ]];
    }
    const friendBlock = await db.Friendship.findAll({
      where: { user_id: userId, status: 3 },
      attributes: ["friend_id"]
    });

    const friendList = await db.Friendship.findAll({
      where: { [Op.or]:[
        {user_id: userId},
        {friend_id: userId}
      ], status: 1 },
      attributes: ["user_id", "friend_id"],
      raw: true
    });

    const blockIds = friendBlock.map(item => item.friend_id);
    const friendIds = friendList.map(item =>
      item.user_id === userId ? item.friend_id : item.user_id
    );


    let createdAtFilter = {};
    if (date) {
      createdAtFilter = {
        [Op.between]: [`${date} 00:00:00`, `${date} 23:59:59`]
      };
    } else if (dateStart && dateEnd) {
      createdAtFilter = {
        [Op.between]: [`${dateStart} 00:00:00`, `${dateEnd} 23:59:59`]
      };
    }

    const wherePost = {
      status: 1,
      ...(blockIds.length && {
        user_id: { [Op.notIn]: blockIds }
      }),

      ...(Object.keys(createdAtFilter).length && {
        created_at: createdAtFilter
      }),

      ...(search && {
        [Op.or]: [
          { content: { [Op.like]: `%${search}%` } },
          ...(Number(search) ? [{ id: Number(search) }] : []),
          { '$User.full_name$': { [Op.like]: `%${search}%` } },
          { '$Location.name$': { [Op.like]: `%${search}%` } },
          { '$Location.address$': { [Op.like]: `%${search}%` } },
        ]
      }),

      [Op.or]: [
        { privacy: 0 },
        { privacy: 2, user_id: userId },
        {
          privacy: 1,
          user_id: { [Op.in]: friendIds }
        }
      ]
    };

    const [postData, postTotal] = await Promise.all([
      db.Post.findAll({
        limit,
        offset,
        order,
        subQuery: false,
        where: wherePost,
        attributes: {
          include: [
            [
              db.sequelize.literal(`(
                SELECT COUNT(*)
                FROM likes
                WHERE likes.post_id = Post.id
                AND likes.user_id = ${userId}
              )`),
              "is_liked"
            ]
          ]
        },
        include: [
          { model: db.User, attributes: ["id", "full_name", "avatar"], required: false },
          { model: db.PostMedia },
          { model: db.Location, required: false }
        ],
        distinct: true
      }),

      db.Post.count({
        where: wherePost,
        include: [
          { model: db.User, attributes: [], required: false },
          { model: db.Location, attributes: [], required: false }
        ],
        distinct: true
      })
    ]);

    const formattedData = postData.map(post => {
      const p = post.toJSON();
      p.is_liked = Boolean(p.is_liked);
      return p;
    });

    return res.status(200).json({
      message: "Post",
      total: postTotal,
      page,
      limit,
      data: formattedData
    });

  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: error.message });
  }
};

const getPostAdmin = async (req, res) => {
  try {
    const { search, sort,
      status, date, dateStart,
      dateEnd, location } = req.query;
    const page = Number(req.query.page) || 1;
    const limit = 10;
    const offset = (page - 1) * limit;
    const userRole = Number(req.user.role);

    if (userRole != 1) {
      return res.status(403).json({
        message: "User no access rights"
      })
    }
    let whereLoation = {};
    let order = [["created_at", "DESC"]]
    if (sort === "trending") {
      order = [[
        db.sequelize.literal(`
        (
        like_count*1 +
        comment_count *2 +
        share_count *3
        )`),
        "DESC"
      ]]
    } else {
      if (status == 1) {
        order = [[
          "status"
        ]]
      } else {
        order = [[
          "status", "DESC"
        ]]
      }
    }

    const wherePost =
    {
      ...(search && {
        [Op.or]: [
          { content: { [Op.like]: `%${search}%` } },
          { id: { [Op.like]: `%${search}%` } },
          { '$User.full_name$': { [Op.like]: `%${search}%` } },
          { '$Location.name$': { [Op.like]: `%${search}%` } },
          { '$Location.address$': { [Op.like]: `%${search}%` } },
        ]
      }),
      ...(status !== undefined && {
        status: Number(status)
      }),
      ...(date !== undefined && {
        created_at: {
          [Op.between]: [
            `${date} 00:00:00`,
            `${date} 23:59:59`
          ]
        }
      }),
      ...(dateStart !== undefined && dateEnd !== undefined && {
        created_at: {
          [Op.between]: [
            `${dateStart} 00:00:00`,
            `${dateEnd} 23:59:59`
          ]
        },

      })
    }

    const [postData, totalPost] = await Promise.all([
      db.Post.findAll({
        subQuery: false,
        limit,
        offset,
        order,
        where: wherePost,
        include: [
          {
            model: db.User,
            attributes: ["id", "full_name", "avatar"],
          },
          {
            model: db.PostMedia,
            separate: true
          },
          {
            model: db.Location,
            where: whereLoation,
            required: !!location
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
  } catch (err) {
    return res.status(500).json({
      error: err
    })
  }
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
  try {
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
  } catch (e) {
    return res.status(500).json({
      error: e
    })
  }
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
