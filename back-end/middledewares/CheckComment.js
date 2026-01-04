import { Op } from "sequelize";
import db from "../models";

const checkCanComment = async (req, res, next) => {
  try {
    const { postId } = req.body;
    if (!postId) return res.status(400).json({ message: "postId is required" });

    // tim post theo id
    const post = await db.Post.findByPk(postId);

    if (!post) return res.status(404).json({ message: "Post not found" });

    // Cac truong hop pending,block.... khong duoc comment
    if (post.status !== "approved")
      return res.status(403).json({ message: "Post is not approved" });
    //pubic ai cung co the comment
    if (post.privacy === "public") return next();
    //private chi co chu post moi co quyen comment
    const user_id = post.user_id;
    const myId = req.user.userId;
    if (post.privacy === "private") {
      if (user_id !== myId)
        return res.status(403).json({
          message: "You are not allowed to comment on this private post",
        });
      return next();
    }

    // friend thi cho co friendship moi comment duoc
    if (post.privacy === "friends") {
      if (user_id === myId) return next();
      const isFriend = await db.Friendship.findOne({
        where: {
          status: 2,
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
    return res.sendStatus(403).json({ message: "not in privacy" });
  } catch (error) {
    console.error("checkCanComment error:", error);
    return res.status(500).json({
      message: "Internal server error",
    });
  }
};

export default checkCanComment;
