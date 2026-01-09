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
    if (liked) return res.status(409).json({ message: "liked" });
    // add like
    await db.Like.create({
      post_id: postId,
      user_id: currentUserId,
    });

    // tang like trong post
    return res.status(201).json({ message: "Liked post successfully" });
  } catch (error) {
    console.error("create like error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
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
    return res.status(200).json({ message: "Unshared post successfully" });
  } catch (error) {
    console.error(" share post error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

export default {
  likePost,
  unlikePost,
  sharePost,
  unsharePost,
};
