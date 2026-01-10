import { stat } from "@babel/core/lib/gensync-utils/fs.js";
import db from "../models/index.js";
import { Sequelize, where, Op } from "sequelize";
import { required } from "joi";
const getPostUser = async (req, res) => {
  try {
    const {
      search,
      sort,
      status,
      user_id,
      date,
      dateStart,
      dateEnd,
      location
    } = req.query;

    const page = Number(req.query.page) || 1;
    const limit = 10;
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

    const wherePost = {
      status: 0,
      ...(search && {
        content: { [Op.like]: `%${search}%` }
      }),

      ...(status !== undefined && {
        status: Number(status)
      }),

      ...(user_id !== undefined && {
        user_id: Number(user_id)
      }),

      ...(date && {
        created_at: {
          [Op.between]: [
            `${date} 00:00:00`,
            `${date} 23:59:59`
          ]
        }
      }),

      ...(dateStart && dateEnd && {
        created_at: {
          [Op.between]: [
            `${dateStart} 00:00:00`,
            `${dateEnd} 23:59:59`
          ]
        }
      }),

      [Op.or]: [
        { privacy: 0 },
        { privacy: 1, user_id: userId }
      ]
    };

    const friendBlock = await db.Friendship.findAll({
      where: {
        user_id: userId,
        status: 3
      },
      attributes: ["friend_id"]
    });

    const blockIds = friendBlock.map(item => item.friend_id);

    if (blockIds.length) {
      wherePost.id = { [Op.notIn]: blockIds };
    }

    const whereLocation = {
      ...(location && {
        [Op.or]: [
          { name: { [Op.like]: `%${location}%` } },
          { address: { [Op.like]: `%${location}%` } }
        ]
      })
    };

    const [postData, postTotal] = await Promise.all([
      db.Post.findAll({
        limit,
        offset,
        order,
        where: wherePost,
        include: [
          {
            model: db.User,
            attributes: ["id", "full_name", "avatar"]
          },
          {
            model: db.PostMedia
          },
          {
            model: db.Location,
            where: whereLocation,
            required: !!location
          }
        ],
        distinct: true
      }),

      db.Post.count({
        where: wherePost,
        include: [
          {
            model: db.Location,
            where: whereLocation,
            required: !!location
          }
        ],
        distinct: true
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
    console.error(error);
    return res.status(500).json({ message: "Server error" });
  }
};


const getPostAdmin = async (req, res) => {
  const { search, sort, 
     status, user_id,
     date, dateStart, 
     dateEnd, location } = req.query;
  const page = Number(req.query.page) || 1;
  const limit = 10;
  const offset = (page - 1) * limit;
  const userRole = Number(req.user.role);
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
  }
  whereLoation = {
    ...(location && {
      [Op.or]: [
        { name: { [Op.like]: `%${location}%` } },
        { address: { [Op.like]: `%${location}%` } },
      ]
    }
    )
  }
  const wherePost =
  {
    ...(search && {
      content: { [Op.like]: `%${search}%` }
    }),
    ...(status !== undefined && {
      status: Number(status)
    }),
    ...(user_id !== undefined && {
      user_id: Number(user_id)
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
  /*if (userRole != 1) {
    return res.status(403).json({
      message: "User no access rights"
    })
  }*/

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
          attributes: ["id", "full_name", "avatar"]
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
