import jwt from "jsonwebtoken";
const verifyToken = (req,res,next)=>{
    const authHeader = req.headers['authorization'];
      if (!authHeader) return res.status(401).json({ message: "Missing token" });
     //  Check Bearer format
    if (!authHeader.startsWith("Bearer ")) {
      return res.status(401).json({ message: "Invalid authorization format" });
    }
    // lay token tu chuoi
    const token = authHeader.split(" ")[1];

    //verify
    jwt.verify(token, process.env.ACCESS_TOKEN_SECRET, (err, decoded) => {
    if (err) {
      return res.status(403).json({ message: "Token expired or invalid" });
    }
    // lưu user info cho controller dùng
    req.user = decoded
    next();
  });
}
export default verifyToken