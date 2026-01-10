import { Op } from "sequelize";

const checkBlocked = async (req, res, next) => {
  try {
    const myId = req.user.userId;
    const { targetId } =
      req.body.from ||
      req.body.to ||
      req.body.userId ||
      req.body.otherId ||
      req.body.friendId ||
      req.body.followingId ||
      req.params.userId ||
      req.params.otherId ||
      req.params.friendId ||
      req.body.postId;
    if (!targetId) return next();
    if (Number(myId) === Number(targetId))
      return res.status(400).json({ message: "Invalid friend block" });

    const userExists = await db.User.findByPk(targetId);
    if (!userExists)
      return res.status(404).json({ message: "User does not exist" });
    const blockedUser = await db.Friendship.findOne({
      where: {
        [Op.or]: [
          { user_id: myId, friend_id: targetId },
          { user_id: targetId, friend_id: myId },
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
    console.error("check blocked  error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

export default checkBlocked;
