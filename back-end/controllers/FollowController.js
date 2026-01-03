
import { where } from "sequelize";
import db from "../models"
const followUser = async(req,res)=>{
    const followerId = req.user.userId
    const {followingId} = req.body

    if(!followingId)   return res.status(400).json({ message: "User id is required" });

    //khong theo doi chinh minh
    if(Number(followUser)===Number(followingId))
         return res.status(400).json({ message: "You cannot follow yourself" });

    //check user ton tai
    const userExists = await db.User.findByPk(followingId)
    if(!userExists) return res.status(404).json({ message: "User not found" });

    //check da follow chua
    const existed = await db.Follow.findOne({
        where:{
            follower_id:followerId,
            following_id:followingId
        }
    })
    if(!existed) return res.status(400).json({ message: "Already following this user" });

    // tao moi quan he follow 
    await db.Follow.create({
        follower_id:followerId,
        following_id:followingId
    })
     return res.status(201).json({ message: "Follow successfully" });

}

const unFollow = async(req,res)=>{
    const followerId = req.user.userId
    const {followingId} = req.body

    const follow = await db.Follow.findOne({
        where:{
            follower_id:followerId,
            following_id:followingId
        }
    })
    if(!follow) return res.status(404).json({message:"Follow relationship not found" })
          await follow.destroy();

    return res.status(200).json({ message: "Unfollow successfully" });
}

export default{
    followUser,
    unFollow
}