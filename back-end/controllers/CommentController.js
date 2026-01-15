import { where } from "sequelize";
import db from "../models";

//status: trạng thái bình luận (0.hidden,1.active.2. deleted)
const create = async (req, res) => {
  try {
    const { postId, content } = req.body;
    const myId = req.user.userId;
    if (!postId)
      return res.status(400).json({ message: "Post id is required" });
    if (!content) return res.status(400).json({ message: "content not found" });
    const postExists = await db.Post.findByPk(postId);
    if (!postExists)
      return res.status(404).json({ message: "Post does not exist" });
    const comment = await db.Comment.create({
      post_id: postId,
      user_id: myId,
      content: content,
      status: 1,
    });
    await db.Post.increment("comment_count", { by: 1, where: { id: postId } });
    return res
      .status(201)
      .json({ message: "Comment created successfully", data: comment });
  } catch (error) {
    console.error("create comment error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const deleteComment = async (req, res) => {
  try {
    const myId = req.user.userId;
    const { commentId } = req.params;
    const comment = await db.Comment.findByPk(commentId, {
      include: [{ model: db.Post }],
    });
    if (!comment) return res.sendStatus(404);
    if (comment.status === 2)
      return res.status(400).json({ message: "Comment already deleted" });
    //khong la chu post cung khong la chu comment
    if (comment.user_id !== myId && comment.Post.user_id !== myId)
      return res.sendStatus(403);
    comment.status = 2;
    comment.deleted_at = new Date();
    await comment.save();
    await db.Post.increment("comment_count", {
      by: -1,
      where: { id: comment.post_id },
    });
    return res.status(200).json({ message: "Deleted" });
  } catch (error) {
    console.error("delete comment error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const getCommentsPost = async (req, res) => {
  try {
    const { postId } = req.params;
    if (!postId)
      return res.status(400).json({ message: "Post id is required" });

    const comments = await db.Comment.findAll({
      where: {
        post_id: postId,
        status: 1,
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
  } catch (error) {
    console.error("get comments error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const getCommentsAdmin = async (req, res) => {
  try {
    const { postId } = req.params;
    if (!postId)
      return res.status(400).json({ message: "Post id is required" });

    const comments = await db.Comment.findAndCountAll({
      where: {
        post_id: postId,
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
  } catch (error) {
    console.error("get comments error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const updateComment = async (req, res) => {
  try {
    const { commentId, content } = req.body;
    const myId = req.user.userId;

    if (!commentId)
      return res.status(400).json({ message: "commentId is required" });
    if (!content)
      return res.status(400).json({ message: "content is required" });
    const comment = await db.Comment.findByPk(commentId);
    if (!comment) return res.status(404).json({ message: "Comment not found" });
    if (myId !== comment.user_id)
      return res.status(403).json({ message: "Forbidden" });
    if (comment.status === 2)
      return res.status(400).json({ message: "Cannot update deleted comment" });
    comment.content = content;
    await comment.save();
    return res.status(200).json({ data: comment });
  } catch (error) {
    console.error("update comments error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const putCommentAdmin = async (req, res) => {
  const { id } = req.params;
  const role = req.user.role;
  if (role !== 1) {
    return res.status(400).json({
      message: "User not admin",
    });
  }
  await db.Comment.update(req.body, { where: { id } });
  return res.status(200).json({
    message: "Update post success",
  });
};

export default {
  create,
  deleteComment,
  getCommentsPost,
  updateComment,
  putCommentAdmin,
  getCommentsAdmin,
};
