import db from "../models/index.js";

const getPostMedia = async (req, res) => {
  const postData = await db.PostMedia.findAll();
  return res.status(200).json({ message: "Get post media success",
    data: postData
   });
};

const getPostMediaById = async (req, res) => {
  return res.status(200).json({ message: "ok" });
};

const postPostMedia = async (req, res) => {
  try{
    const {post_id} = req.body;
  const postCheck = await db.Post.findByPk(post_id)
  if(!postCheck){
    return res.status(404).json({
      message:"Post not found"
    })
  }
  const postData = await db.PostMedia.create(req.body);
  return res.status(200).json({ message: "Create post media success",
    data: postData
   });
  } catch(e){
    return res.status(500).json({
      error: e
    })
  }
};

const deletePostMedia = async (req, res) => {
  return res.status(200).json({ message: "ok" });
};

export default {
  getPostMedia,
  getPostMediaById,
  postPostMedia,
  deletePostMedia,
};
