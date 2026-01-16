import { where } from "sequelize";
import db from "../models";
const likePost = async (req, res) => {
  try {
    const { postId } = req.body;
    const currentUserId = req.user.userId;

    if (!postId) return res.status(400).json({ message: "postId is required" });

    // kiem tra su ton tai cua post
    const post = await db.Post.findByPk(postId);
    if (!post) return res.status(404).json({ message: "Post not found" });

    //kiem tra da like
    const liked = await db.Like.findOne({
      where: {
        post_id: postId,
        user_id: currentUserId,
      },
    });
    if (liked) return res.status(409).json({ message: "Post already liked" });
    // add like
    await db.Like.create({
      post_id: postId,
      user_id: currentUserId,
    });

    // tang like trong post

    await db.Post.increment("like_count", { by: 1, where: { id: postId } });

    return res.status(201).json({ message: "Liked post successfully" });
  } catch (error) {
    console.error("create like error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};
const isLiked = async (req, res) => {
  try {
    const { postId } = req.params;
    const currentUserId = req.user.userId;

    if (!postId) return res.status(400).json({ message: "postId is required" });

    // kiem tra su ton tai cua post
    const post = await db.Post.findByPk(postId);
    if (!post) return res.status(404).json({ message: "Post not found" });

    //kiem tra da like
    const liked = await db.Like.findOne({
      where: {
        post_id: postId,
        user_id: currentUserId,
      },
    });
    return res.status(409).json({ isLiked: liked ? true : false });
  } catch (error) {}
};
const unlikePost = async (req, res) => {
  try {
    const { postId } = req.body;
    const currentUserId = req.user.userId;

    if (!postId) return res.status(400).json({ message: "postId is required" });

    // kiem tra su ton tai cua post
    const post = await db.Post.findByPk(postId);
    if (!post) return res.status(404).json({ message: "Post not found" });

    //kiem tra da like
    const liked = await db.Like.findOne({
      where: {
        post_id: postId,
        user_id: currentUserId,
      },
    });
    if (!liked) return res.status(409).json({ message: "Post not liked yet" });

    await db.Post.increment("like_count", { by: -1, where: { id: postId } });

    await liked.destroy({});

    // tang like trong post
    return res.status(200).json({ message: "unliked post successfully" });
  } catch (error) {
    console.error("unlike error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const sharePost = async (req, res) => {
  try {
    const { postId } = req.body;
    const currentUserId = req.user.userId;

    if (!postId) return res.status(400).json({ message: "postId is required" });

    // kiem tra su ton tai cua post
    const post = await db.Post.findByPk(postId);
    if (!post) return res.status(404).json({ message: "Post not found" });

    // kiem tra  shared
    const shared = await db.Share.findOne({
      where: {
        post_id: postId,
        user_id: currentUserId,
      },
    });
    if (shared) {
      return res.status(409).json({ message: "Post already shared" });
    }
    // share post
    await db.Share.create({
      post_id: postId,
      user_id: currentUserId,
    });

    await db.Post.increment("share_count", { by: 1, where: { id: postId } });

    return res.status(201).json({ message: "Shared post successfully" });
  } catch (error) {
    console.error(" share post error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const unsharePost = async (req, res) => {
  try {
    const { postId } = req.body;
    const currentUserId = req.user.userId;

    if (!postId) return res.status(400).json({ message: "postId is required" });

    // kiem tra su ton tai cua post
    const post = await db.Post.findByPk(postId);
    if (!post) return res.status(404).json({ message: "Post not found" });

    // kiem tra  shared
    const shared = await db.Share.findOne({
      where: {
        post_id: postId,
        user_id: currentUserId,
      },
    });
    if (!shared) {
      return res.status(404).json({ message: "Post not shared yet" });
    }
    // unshare post
    await shared.destroy();
    await db.Post.increment("share_count", { by: -1, where: { id: postId } });

    return res.status(200).json({ message: "Unshared post successfully" });
  } catch (error) {
    console.error(" share post error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const savePost = async (req, res) => {
  try {
    const { postId } = req.body;
    const myId = req.user.userId;
    if (!postId) return res.status(400).json({ message: "postId is required" });
    const post = await db.Post.findByPk(postId);
    if (!post) return res.status(404).json({ message: "Post  not found" });
    const saved = await db.SavedPost.findOne({
      where: {
        post_id: postId,
        user_id: myId,
      },
    });
    if (saved) return res.status(409).json({ message: "Post already saved" });

    await db.SavedPost.create({
      post_id: postId,
      user_id: myId,
    });
    return res.status(201).json({ message: "Saved post successfully" });
  } catch (error) {
    console.error(" save post error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const unSavePost = async (req, res) => {
  try {
    const { postId } = req.body;
    const myId = req.user.userId;
    if (!postId) return res.status(400).json({ message: "postId is required" });
    const post = await db.Post.findByPk(postId);
    if (!post) return res.status(404).json({ message: "Post  not found" });
    const saved = await db.SavedPost.findOne({
      where: {
        post_id: postId,
        user_id: myId,
      },
    });
    if (!saved) return res.status(404).json({ message: "Post not saved yet" });

    await saved.destroy();
    return res.status(200).json({ message: "Unsaved post successfully" });
  } catch (error) {
    console.error(" save post error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const getSharedPost = async (req, res) => {
  try {
    const myId = req.user.userId;

        const shared = await db.Share.findAll({
      where: { user_id: myId },
      include: [
        {
          model: db.Post,
          where: { status: 1 },
              attributes: {
                include: [
                  [
                    db.sequelize.literal(`(
                      SELECT CASE
                        WHEN COUNT(*) > 0 THEN true
                        ELSE false
                      END
                      FROM Likes
                      WHERE Likes.post_id = Post.id
                      AND Likes.user_id = ${myId}
                    )`),
                    "isLiked"
                  ]
                ]
          },
          include: [
            {
              model: db.User,
              attributes: ["id", "full_name", "avatar"],
            },
            { model: db.Location },
            { model: db.PostMedia },
          ],
        },
      ],
      order: [["createdAt", "DESC"]],
    });





    return res.json({ data: shared });
  } catch (error) {
    console.error("get shared post error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};
// const getSavePost = async (req, res) => {
//   try {
//     const myId = req.user.userId;
//     const saved = await db.SavedPost.findAll({
//       where: {
//         user_id: myId,
//       },
//       include: [
//         // user đang lưu post
//         {
//           model: db.User,
//           attributes: ["id", "full_name", "avatar", "status"],
//         },
//         {
//           model: db.Post,
//           where: { status: 1 },
//            attributes: {
//                 include: [
//                   [
//                     db.sequelize.literal(`(
//                       SELECT CASE
//                         WHEN COUNT(*) > 0 THEN true
//                         ELSE false
//                       END
//                       FROM Likes
//                       WHERE Likes.post_id = Post.id
//                       AND Likes.user_id = ${myId}
//                     )`),
//                     "is_liked"
//                   ]
//                 ]
//           },
//           include: [
//             {
//               // chu bai post
//               model: db.User,
//               attributes: ["id", "full_name", "avatar"],
//             },
//             { model: db.Location },
//             { model: db.PostMedia },
//           ],
//         },
//       ],
//       order: [["created_at", "DESC"]],
//     });
//     return res.json({
//       data: saved,
//     });
//   } catch (error) {
//     console.error(" get  sharedpost error:", error);
//     return res.status(500).json({ message: "Internal server error" });
//   }
// };
const getSavePost = async (req, res) => {
  try {
    const myId = req.user.userId;

    // 1. Phần Query Database (Giữ nguyên)
    const saved = await db.SavedPost.findAll({
      where: {
        user_id: myId,
      },
      include: [
        {
          model: db.User, // User đang lưu (chủ sở hữu bản ghi save)
          attributes: ["id", "full_name", "avatar", "status"],
        },
        {
          model: db.Post,
          required: true, // Chỉ lấy nếu bài viết còn tồn tại
          where: { status: 1 },
          attributes: {
            include: [
              [
                db.sequelize.literal(`(
                  SELECT CASE
                    WHEN COUNT(*) > 0 THEN true
                    ELSE false
                  END
                  FROM Likes
                  WHERE Likes.post_id = Post.id
                  AND Likes.user_id = ${myId}
                )`),
                "is_liked"
              ]
            ]
          },
          include: [
            {
              model: db.User, // Tác giả bài viết
              attributes: ["id", "full_name", "avatar"],
            },
            { model: db.Location },
            { model: db.PostMedia },
          ],
        },
      ],
      order: [["created_at", "DESC"]],
    });

    const finalData = saved.map((item) => {
      const savedItem = item.get({ plain: true });

      if (savedItem.Post) {
        savedItem.Post.is_liked = (savedItem.Post.is_liked === 1 || savedItem.Post.is_liked === true);
      }

      return savedItem;
    });

    return res.json({
      data: finalData,
    });

  } catch (error) {
    console.error("Get saved post error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};
export default {
  likePost,
  unlikePost,
  sharePost,
  unsharePost,
  savePost,
  unSavePost,
  getSharedPost,
  getSavePost,
  isLiked,
};