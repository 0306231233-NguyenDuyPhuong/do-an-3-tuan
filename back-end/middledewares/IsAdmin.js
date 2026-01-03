const isAdmin = (req, res, next) => {
    console.log("role: ", req.user)
  if (req.user.role !== "admin") {
    return res.status(403).json({
      message: "Only admin can perform this action"
    });
  }
  next();
};

export default isAdmin;