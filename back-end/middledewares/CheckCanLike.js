import { Op } from "sequelize";
import db from "../models";

//privacy 0: public, 1: friends, 2: private
//status 0: deleted, 1: approved
const checkCanLike = async (req, res, next) => {
  try {
    const { postId } = req.body;
    if (!postId) return res.status(400).json({ message: "postId is required" });

    // tim post theo id
    const post = await db.Post.findByPk(postId);

    if (!post) return res.status(404).json({ message: "Post not found" });

    // Cac truong hop pending,block.... khong duoc like
    if (post.status !== 1)
      return res.status(403).json({ message: "Post is not approved" });
    //pubic ai cung co the like
    if (post.privacy === 0) return next();
    //private chi co chu post moi co quyen like
    const user_id = post.user_id;
    const myId = req.user.userId;
    if (post.privacy === 2)
      return res.status(403).json({ message: "Cannot like private post" });
    // friend thi cho co friendship moi like duoc
    if (post.privacy === 1) {
      if (user_id === myId) return next();
      const isFriend = await db.Friendship.findOne({
        where: {
          status: 1,
          [Op.or]: [
            { user_id: myId, friend_id: user_id },
            { user_id: user_id, friend_id: myId },
          ],
        },
      });
      if (!isFriend)
        return res
          .status(403)
          .json({ message: "You are not friend with post owner" });
      return next();
    }
    // khong thuoc privacy nao
    return res.status(403).json({ message: "not in privacy" });
  } catch (error) {
    console.error("checkCanlike error:", error);
    return res.status(500).json({
      message: "Internal server error",
    });
  }
};

export default checkCanLike;
