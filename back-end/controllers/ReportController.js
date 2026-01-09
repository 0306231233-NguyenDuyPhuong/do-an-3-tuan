import { where } from "sequelize";
import db from "../models/index.js";
import { date } from "joi";
import REPORTSTATUS from "../constants/ReportStatus.js"

const getReport = async (req, res) => {
  try {
    const page = Number(req.query.page) || 1;
    const limit = 10;
    const offset = (page-1)*limit;
    const reportData = await db.Report.findAndCountAll({
      offset,
      limit
    })
  return res.status(200).json({ 
    message: "Get report success", 
    page, 
    limit,
    data:reportData });
  } catch (error) {
    return res.status(400).json({
      error: error
    })
  }
};

const getReportById = async (req, res) => {
  return res.status(200).json({ message: "ok" });
};

const postReport = async (req, res) => {
  const { reporter_id, target_id, target_type, reason, description } = req.body;
  let targetCheck;
  if(target_type == "user"){
    targetCheck = await db.User.findOne({
      where: {id: target_id}
    })
  } else if(target_type == "post"){
    targetCheck = await db.Post.findOne({
      where: {id: target_id}
    })
  }  else{
    targetCheck = await db.Comment.findOne({
      where: {id: target_id}
    })
  }
  if(!targetCheck){
      return res.status(404).json({
        message: "target not found"
      });
  } else{
    const reportData = await db.Report.create({
        reporter_id,
        target_type,
        target_id,
        reason,
        description,
        status: REPORTSTATUS.PENDING
    });
      return res.status(201).json({ 
        message: "Insert report success",
        data: reportData 
   });
  }
};

const putReport = async (req, res) => {
    const { id } = req.params;
    const reportData = await db.Report.findByPk(id);
    if(!reportData){
      return res.status(404).json({
        message:"Report not found"
      });
    }
    await db.Report.update(req.body, {where: {id}})
    return res.status(200).json({
      message:"Report update success"
    })
};
const deleteReport = async (req, res) => {
      const {id} = req.params;
      const locationCheck = await db.Report.findByPk(id);
      if(!locationCheck){
          return res.status(200).json({
          message: "Location not found",
      });
      } else{
          await db.Report.destroy({where: {id}});
          return res.status(200).json({
          message: "Delete report success",
          });
      }
};

export default {
  getReport,
  getReportById,
  postReport,
  putReport,
  deleteReport,
};
