import { Op } from "sequelize";

const db = require("../models");

const checkBlockedPost = async (req, res, next) => {
  try {
    const targetId = req.body.postId;
    const myId = req.user.userId;
    console.log({ targetId, myId });
    if (!targetId) return next();

    const post = await db.Post.findByPk(targetId);
    if (!post) return res.status(404).json({ message: "Post not found" });

    const userId = post.user_id;
    // if (Number(myId) === Number(userId))
    //   return res.status(400).json({ message: "Invalid friend block" });
    const userExists = await db.User.findByPk(post.user_id);
    if (!userExists)
      return res.status(404).json({ message: "User does not exist" });
    const blockedUser = await db.Friendship.findOne({
      where: {
        [Op.or]: [
          { user_id: myId, friend_id: userId },
          { user_id: userId, friend_id: myId },
        ],
        status: 3,
      },
    });
    if (!blockedUser) return next();
    else
      return res
        .status(403)
        .json({ message: "Interaction blocked between users" });
  } catch (error) {
    console.error("check blockedPost  error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};
export default checkBlockedPost;
