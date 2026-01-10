import { Op, where } from "sequelize";
import db from "../models";

// status friendship
// 0 = pending
// 1 = accepted
// 2 = rejected
// 3 = blocked

//only_follow:
//0 friend
//1 follow
const sendFriendRequest = async (req, res) => {
  try {
    const { to } = req.body;
    const from = req.user.userId;

    if (!to)
      return res.status(400).json({ message: "Receiver user id is required" });

    if (Number(from) === Number(to))
      return res
        .status(400)
        .json({ message: "You can not send a friend request to yourself" });

    // check user tồn tại
    const userExists = await db.User.findByPk(to);
    if (!userExists)
      return res.status(404).json({ message: "User does not exist" });

    // check đã tồn tại quan hệ hay chưa
    const existed = await db.Friendship.findOne({
      where: {
        [Op.or]: [
          { user_id: from, friend_id: to },
          { user_id: to, friend_id: from },
        ],
      },
    });

    if (existed) {
      // đã bị reject thì gửi lại
      if (existed.status === 2) {
        existed.status = 0;
        existed.user_id = from;
        existed.friend_id = to;
        await existed.save();

        return res.status(200).json({ message: "Friend request sent again" });
      }

      if (existed.status === 1)
        return res.status(400).json({ message: "You are already friends" });

      if (existed.status === 0)
        return res
          .status(400)
          .json({ message: "Friend request already exists" });
    }

    // tạo mới
    await db.Friendship.create({
      user_id: from,
      friend_id: to,
      status: 0,
      only_follow: 0,
    });

    return res
      .status(201)
      .json({ message: "Friend request sent successfully" });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const acceptFriendRequest = async (req, res) => {
  try {
    const { from } = req.body;
    const myId = req.user.userId;

    if (!from)
      return res.status(400).json({
        message: "Sender user id is required",
      });

    if (Number(from) === Number(myId))
      return res.status(400).json({ message: "Invalid friend request" });

    const friendship = await db.Friendship.findOne({
      where: {
        user_id: from,
        friend_id: myId,
        status: 0,
      },
    });

    if (!friendship)
      return res.status(404).json({
        message: "Friend request not found",
      });

    friendship.status = 1;
    friendship.only_follow = 0;
    await friendship.save();

    return res
      .status(200)
      .json({ message: "Friend request accepted successfully" });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const blockUser = async (req, res) => {
  try {
    const { otherId } = req.body;
    const myId = req.user.userId;

    if (!otherId)
      return res.status(400).json({
        message: "Target user id is required",
      });
    const userExists = await db.User.findByPk(otherId);
    if (!userExists)
      return res.status(404).json({ message: "User does not exist" });

    if (Number(otherId) === Number(myId))
      return res.status(400).json({ message: "Invalid friend block" });

    const alreadyBlock = await db.Friendship.findOne({
      where: {
        status: 3,
        user_id: myId,
        friend_id: otherId,
      },
    });
    if (alreadyBlock)
      return res.status(400).json({ message: "User already blocked" });

    //xoa quan he truoc khi buoc
    await db.Friendship.destroy({
      where: {
        [Op.or]: [
          { user_id: myId, friend_id: otherId },
          { user_id: otherId, friend_id: myId },
        ],
      },
    });

    await db.Friendship.create({
      user_id: myId,
      friend_id: otherId,
      status: 3,
      only_follow: 0,
    });
    return res.status(200).json({ message: "User blocked successfully" });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const unBlockUser = async (req, res) => {
  try {
    const { otherId } = req.body;
    const myId = req.user.userId;

    if (!otherId)
      return res.status(400).json({
        message: "Target user id is required",
      });

    if (Number(otherId) === Number(myId))
      return res.status(400).json({ message: "Invalid unblock action" });

    const userExists = await db.User.findByPk(otherId);
    if (!userExists)
      return res.status(404).json({ message: "User does not exist" });
    const blockedUser = await db.Friendship.findOne({
      where: {
        user_id: myId,
        friend_id: otherId,
        status: 3,
      },
    });

    if (!blockedUser)
      return res.status(404).json({ message: "user is not blocked" });

    await blockedUser.destroy();
    return res.status(200).json({ message: "User unblocked successfully" });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const rejectFriendRequest = async (req, res) => {
  try {
    const { from } = req.body;
    const myId = req.user.userId;

    if (!from)
      return res.status(400).json({
        message: "Sender user id is required",
      });

    if (Number(from) === Number(myId))
      return res.status(400).json({ message: "Invalid friend request" });

    const friendship = await db.Friendship.findOne({
      where: {
        user_id: from,
        friend_id: myId,
        status: 0,
      },
    });

    if (!friendship)
      return res.status(404).json({
        message: "Friend request not found",
      });

    friendship.status = 2;
    await friendship.save();

    return res
      .status(200)
      .json({ message: "Friend request rejected successfully" });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const cancelFriendRequest = async (req, res) => {
  try {
    const from = req.user.userId;
    const { to } = req.body;

    if (!to)
      return res.status(400).json({ message: "Receiver user id is required" });

    const friendship = await db.Friendship.findOne({
      where: {
        user_id: from,
        friend_id: to,
        status: 0,
      },
    });

    if (!friendship)
      return res.status(404).json({ message: "Friend request not found" });

    await friendship.destroy();

    return res
      .status(200)
      .json({ message: "Friend request canceled successfully" });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const getFriendRequests = async (req, res) => {
  try {
    const userId = req.user.userId;

    const requests = await db.Friendship.findAll({
      where: {
        friend_id: userId,
        status: 0,
      },
      include: [
        {
          model: db.User,
          as: "sender",
          attributes: ["id", "full_name", "avatar"],
        },
      ],
    });

    return res.status(200).json({ data: requests });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const getFriends = async (req, res) => {
  try {
    const myId = req.user.userId;

    const friendships = await db.Friendship.findAll({
      where: {
        status: 1,
        [Op.or]: [{ user_id: myId }, { friend_id: myId }],
      },
      include: [
        {
          model: db.User,
          as: "sender",
          attributes: ["id", "full_name", "avatar"],
        },
        {
          model: db.User,
          as: "receiver",
          attributes: ["id", "full_name", "avatar"],
        },
      ],
    });

    const friends = friendships.map((f) => {
      const friend = Number(f.user_id) === Number(myId) ? f.receiver : f.sender;

      return {
        id: friend.id,
        full_name: friend.full_name,
        avatar: friend.avatar,
      };
    });

    return res.status(200).json({ data: friends });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const unFriend = async (req, res) => {
  try {
    const myId = req.user.userId;
    const { friendId } = req.body;

    if (!friendId)
      return res.status(400).json({ message: "Friend id is required" });

    const friendship = await db.Friendship.findOne({
      where: {
        status: 1,
        [Op.or]: [
          { user_id: myId, friend_id: friendId },
          { user_id: friendId, friend_id: myId },
        ],
      },
    });

    if (!friendship)
      return res.status(404).json({ message: "Friendship not found" });

    await friendship.destroy();

    return res.status(200).json({ message: "Unfriend successfully" });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const isFriend = async (req, res) => {
  try {
    const { friendId } = req.params;
    const myId = req.user.userId;

    // check user tồn tại
    const userExists = await db.User.findByPk(friendId);
    if (!userExists)
      return res.status(404).json({ message: "User does not exist" });

    const friendship = await db.Friendship.findOne({
      where: {
        status: 1, // accepted
        [Op.or]: [
          { user_id: myId, friend_id: friendId },
          { user_id: friendId, friend_id: myId },
        ],
      },
    });

    return res.status(200).json({
      isFriend: friendship ? true : false,
    });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};
const follow = async (req, res) => {
  try {
    const { to } = req.body;
    const from = req.user.userId;
    if (!to)
      return res.status(400).json({ message: "Receiver user id is required" });
    if (Number(from) === Number(to))
      return res.status(400).json({ message: "You can not follow yourself" });

    // check user tồn tại
    const userExists = await db.User.findByPk(to);
    if (!userExists)
      return res.status(404).json({ message: "User does not exist" });
    const existedRelation = await db.Friendship.findOne({
      where: {
        user_id: from,
        friend_id: to,
      },
    });
    if (!existedRelation) {
      await db.Friendship.create({
        user_id: from,
        friend_id: to,
        status: null,
        only_follow: 1,
      });
      return res.status(200).json({ message: "Followed successfully" });
    }

    if (existedRelation.status == 3)
      return res.status(403).json({ message: "Interaction blocked" });
    if (existedRelation.status === 1)
      return res.status(400).json({ message: "You are already friends" });
    if (existedRelation.status === 2) {
      existedRelation.only_follow = 1;
      await existedRelation.save();
      return res.status(200).json({ message: "Followed successfully" });
    }
    if (existedRelation.status === 0)
      return res.status(400).json({ message: "Friend request is pending" });
    if (existedRelation.only_follow == 1)
      return res
        .status(400)
        .json({ message: "you already followed this user" });
    else {
      existedRelation.only_follow = 1;
      await existedRelation.save();
      return res.status(200).json({ message: "create followed successfully" });
    }
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const getFollower = async (req, res) => {
  try {
    const myId = req.user.userId;
    const follower = await db.Friendship.findAll({
      where: {
        friend_id: myId,
        only_follow: 1,
      },
      include: [
        {
          model: db.User,
          as: "sender",
          attributes: ["id", "full_name", "avatar"],
        },
      ],
    });
    return res.status(200).json({ data: follower });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const getFollowing = async (req, res) => {
  try {
    const myId = req.user.userId;
    const follower = await db.Friendship.findAll({
      where: {
        user_id: myId,
        only_follow: 1,
      },
      include: [
        {
          model: db.User,
          as: "receiver",
          attributes: ["id", "full_name", "avatar"],
        },
      ],
    });
    return res.status(200).json({ data: follower });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const unFollow = async (req, res) => {
  try {
    const myId = req.user.userId;
    const { followingId } = req.body;
    if (!followingId)
      return res.status(400).json({ message: "followingId is required" });

    const isFollow = await db.Friendship.findOne({
      where: {
        user_id: myId,
        friend_id: followingId,
        only_follow: 1,
      },
    });
    if (!isFollow)
      return res.status(404).json({ message: "follow relationship not exist" });
    if (isFollow.status == null) {
      isFollow.destroy();
      return res.status(200).json({ message: "unfollowed successfully" });
    }
    isFollow.only_follow = 0;
    await isFollow.save();
    return res.status(200).json({ message: "unfollowed successfully" });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};
export default {
  sendFriendRequest,
  getFriendRequests,
  acceptFriendRequest,
  rejectFriendRequest,
  cancelFriendRequest,
  getFriends,
  unFriend,
  blockUser,
  isFriend,
  follow,
  getFollower,
  getFollowing,
  unFollow,
  unBlockUser,
};
