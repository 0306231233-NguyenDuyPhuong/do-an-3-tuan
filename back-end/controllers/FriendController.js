import { Op } from "sequelize";
import db from "../models";

// status friendship
// 1 = pending
// 2 = accepted
// 3 = rejected


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

    // check đã tồn tại quan hệ hay chưa (2 chiều)
    const existed = await db.Friendship.findOne({
      where: {
        [Op.or]: [
          { user_id: from, friend_id: to },
          { user_id: to, friend_id: from },
        ],
      },
    });

    if (existed) {
      // đã bị reject → gửi lại
      if (existed.status === 3) {
        existed.status = 1;
        existed.user_id = from;
        existed.friend_id = to;
        await existed.save();

        return res
          .status(200)
          .json({ message: "Friend request sent again" });
      }

      if (existed.status === 2)
        return res
          .status(400)
          .json({ message: "You are already friends" });

      if (existed.status === 1)
        return res
          .status(400)
          .json({ message: "Friend request already exists" });
    }

    // tạo mới
    await db.Friendship.create({
      user_id: from,
      friend_id: to,
      status: 1,
    });

    return res
      .status(201)
      .json({ message: "Friend request sent successfully" });
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
        status: 1,
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
        status: 1,
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
      .json({ message: "Friend request accepted successfully" });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const blockFriendRequest = async (req, res) => {
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
        status: 1,
      },
    });

    if (!friendship)
      return res.status(404).json({
        message: "Friend request not found",
      });

    friendship.status = 4;
    await friendship.save();

    return res
      .status(200)
      .json({ message: "Friend request block successfully" });
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
        status: 1,
      },
    });

    if (!friendship)
      return res.status(404).json({
        message: "Friend request not found",
      });

    friendship.status = 3;
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
      return res
        .status(400)
        .json({ message: "Receiver user id is required" });

    const friendship = await db.Friendship.findOne({
      where: {
        user_id: from,
        friend_id: to,
        status: 1,
      },
    });

    if (!friendship)
      return res
        .status(404)
        .json({ message: "Friend request not found" });

    await friendship.destroy();

    return res
      .status(200)
      .json({ message: "Friend request canceled successfully" });
  } catch (error) {
    return res.status(500).json({ message: error.message });
  }
};

const getFriends = async (req, res) => {
  try {
    const myId = req.user.userId;

    const friendships = await db.Friendship.findAll({
      where: {
        status: 2,
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
      const friend =
        Number(f.user_id) === Number(myId) ? f.receiver : f.sender;

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
      return res
        .status(400)
        .json({ message: "Friend id is required" });

    const friendship = await db.Friendship.findOne({
      where: {
        status: 2,
        [Op.or]: [
          { user_id: myId, friend_id: friendId },
          { user_id: friendId, friend_id: myId },
        ],
      },
    });

    if (!friendship)
      return res
        .status(404)
        .json({ message: "Friendship not found" });

    await friendship.destroy();

    return res.status(200).json({ message: "Unfriend successfully" });
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
  blockFriendRequest
};
