import { number } from "joi";
import db, { sequelize } from "../models/index"
import { where } from "sequelize";
import { raw } from "mysql2";

const getChartDashboard = async(req, res)=>{
    try{
        const userRole =Number(req.user.role);
        /*if (userRole != 1) {
        return res.status(403).json({
        message: "User no access rights"
        })
        }*/
       
    const [userData, postData, reportData, reportByTpype] = await Promise.all([
        db.User.findAll({
            attributes:[
                [sequelize.fn("DATE", sequelize.col("created_at")), 'date'],
                [sequelize.fn("COUNT", sequelize.col("id")), 'count']
            ],
            group: [sequelize.fn("DATE", sequelize.col("created_at"))],
            order: [[sequelize.fn('DATE', sequelize.col('created_at')), 'ASC']],
            raw: true
        }),
        db.Post.findAll({
            attributes: [
                [sequelize.fn("DATE", sequelize.col("created_at")), 'date'],
                [sequelize.fn('COUNT', sequelize.col("id")), 'count']
            ],
            group: [sequelize.fn('DATE', sequelize.col("created_at"))],
            order: [[sequelize.fn('DATE', sequelize.col('created_at')), 'ASC']],
            raw: true
        }),
        db.Report.findAll({
            attributes: [
                [sequelize.fn("DATE", sequelize.col("created_at")), "date"], 
                [sequelize.fn('COUNT', sequelize.col("created_at")), "count"]
            ], 
            group: [sequelize.fn('DATE', sequelize.col("created_at"))], 
            order: [[sequelize.fn("DATE", sequelize.col("created_at")), 'ASC']],
            raw: true
        }),
        db.Report.findAll({
            attributes: [
                "target_type", 
                [sequelize.fn("COUNT", sequelize.col("created_at")), "count"]
            ],
            group: ["target_type"],
            raw: true
        })
    ])
    return res.status(200).json({
        message:"Get chart success",
        usersChart: userData, 
        postsChart: postData, 
        reportsChart: reportData, 
        reportByType: reportByTpype
    })
    } catch(e){
        return res.status(500).json({
            error: e
        })
    }
}

const getCountDashboard = async(req, res)=>{
    const [userCount, postCount, reportCount] = await Promise.all([
        db.User.count(),
        db.Post.count(),
        db.Report.count(),
    ])
    return res.status(200).json({
        message: "Get count user, post, report",
        users: userCount,
        posts: postCount,
        reports: reportCount
    })
}

export default {
    getChartDashboard,
    getCountDashboard
}