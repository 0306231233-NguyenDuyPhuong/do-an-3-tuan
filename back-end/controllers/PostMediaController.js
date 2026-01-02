import db from "../models/index.js";

const getPostMedia = async (req, res) => {
  return res.status(200).json({ message: "ok" });
};

const getPostMediaById = async (req, res) => {
  return res.status(200).json({ message: "ok" });
};

const postPostMedia = async (req, res) => {
  return res.status(200).json({ message: "ok" });
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
