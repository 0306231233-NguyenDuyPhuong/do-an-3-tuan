import { where } from "sequelize";
import db from "../models";

//status: trạng thái bình luận (active, hidden, deleted)
const create = async (req, res) => {
  try {
    const { postId, content } = req.body;
    const myId = req.user.userId;
    if (!content) return res.status(400).json({ message: "content not found" });

    const comment = await db.Comment.create({
      post_id: postId,
      user_id: myId,
      content: content,
      status: "active",
    });
    return res
      .status(201)
      .json({ message: "Comment created successfully", data: comment });
  } catch (error) {
    console.error("create comment error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const deleteComment = async (req, res) => {
  const myId = req.user.userId;
  const { commentId } = req.params;
  const comment = await db.Comment.findByPk(commentId, {
    include: [{ model: db.Post }],
  });
  if (!comment) return res.sendStatus(404);

  if (comment.status === "deleted")
    return res.status(400).json({ message: "Comment already deleted" });
  //khong la chu post cung khong la chu comment
  if (comment.user_id !== myId && comment.Post.user_id != myId)
    return res.sendStatus(403);
  comment.status = "deleted";
  comment.deleted_at = new Date();
  await comment.save();

  return res.status(200).json({ message: "Deleted" });
};

const getCommentsPost = async (req, res) => {
  const { postId } = req.params;
  const comments = await db.Comment.findAll({
    where: {
      post_id: postId,
      status: "active",
    },
    include: [
      {
        model: db.User,
        attributes: ["id", "full_name", "avatar"],
      },
    ],
    order: [["created_at", "ASC"]],
  });
  return res.status(200).json({ data: comments });
};

const updateComment = async (req, res) => {
  const { commentId, content } = req.body;
  const myId = req.user.userId;

  if (!commentId)
    return res.status(400).json({ message: "commentId is required" });
  if (!content) return res.status(400).json({ message: "content is required" });
  const comment = await db.Comment.findByPk(commentId);
  if (!comment) return res.status(404).json({ message: "Comment not found" });
  if (myId !== comment.user_id) return res.sendStatus(403);
  comment.content = content;
  await comment.save();
  return res.status(200).json({ data: comment });
};
export default {
  create,
  deleteComment,
  getCommentsPost,
  updateComment,
};
