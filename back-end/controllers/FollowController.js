import db from "../models";
const followUser = async (req, res) => {
  const followerId = req.user.userId;
  const { followingId } = req.body;

  if (!followingId)
    return res.status(400).json({ message: "User id is required" });

  //khong theo doi chinh minh
  if (Number(followerId) === Number(followingId))
    return res.status(400).json({ message: "You cannot follow yourself" });

  //check user ton tai
  const userExists = await db.User.findByPk(followingId);
  if (!userExists) return res.status(404).json({ message: "User not found" });

  //check da follow chua
  const existed = await db.Follow.findOne({
    where: {
      follower_id: followerId,
      following_id: followingId,
    },
    paranoid: false,
  });
  if (existed) {
    if (existed.deleted_at !== null) {
      await existed.restore(); // deleted_at = NULL
      return res.status(200).json({ message: "Follow again successfully" });
    }

    return res.status(400).json({ message: "Already following this user" });
  }

  // tao moi quan he follow
  await db.Follow.create({
    follower_id: followerId,
    following_id: followingId,
  });
  return res.status(201).json({ message: "Follow successfully" });
};

const unFollow = async (req, res) => {
  const followerId = req.user.userId;
  const { followingId } = req.body;

  const follow = await db.Follow.findOne({
    where: {
      follower_id: followerId,
      following_id: followingId,
    },
  });
  if (!follow)
    return res.status(404).json({ message: "Follow relationship not found" });
  await follow.destroy();

  return res.status(200).json({ message: "Unfollow successfully" });
};

const getFollowers = async (req, res) => {
  try {
    const myId = req.user.userId;

    console.log(myId);
    const followers = await db.Follow.findAll({
      where: {
        following_id: myId,
        deleted_at: null,
      },
      include: [
        {
          model: db.User,
          as: "Follower",
          attributes: ["id", "full_name", "avatar"],
        },
      ],
    });

    return res.status(200).json({
      data: followers.map((f) => f.Follower),
    });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const getFollowing = async (req, res) => {
  try {
    const myId = req.user.userId;

    console.log(myId);
    const followers = await db.Follow.findAll({
      where: {
        follower_id: myId,
        deleted_at: null,
      },
      include: [
        {
          model: db.User,
          as: "Following",
          attributes: ["id", "full_name", "avatar"],
        },
      ],
    });

    return res.status(200).json({
      data: followers.map((f) => f.Following),
    });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};
const isFollowing = async (req, res) => {
  try {
    const followerId = req.user.userId;
    const { followingId } = req.params;

    if (!followingId)
      return res.status(400).json({ message: "friendId is required" });

    //check user tontai

    const userExists = await db.User.findByPk(followingId);
    if (!userExists) return res.status(404).json({ message: "User not found" });

    // check follow relationship

    const follow = await db.Follow.findOne({
      where: {
        follower_id: followerId,
        following_id: followingId,
        deleted_at: null,
      },
    });

    if (!follow) return res.status(200).json({ isFollowing: false });
    return res.status(200).json({ isFollowing: true });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};
export default {
  followUser,
  unFollow,
  getFollowers,
  getFollowing,
  isFollowing,
};
