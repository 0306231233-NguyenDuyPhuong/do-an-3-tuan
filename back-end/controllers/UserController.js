import db from "../models/index"

const login = async (req, res) => {
  try {
    const data = await db.User.findAll();
    return res.status(200).json({
      message: 'ok',
      data
    });
  } catch (error) {
    return res.status(500).json({
      error: error.message
    });
  }
};


export default {
    login:login
}